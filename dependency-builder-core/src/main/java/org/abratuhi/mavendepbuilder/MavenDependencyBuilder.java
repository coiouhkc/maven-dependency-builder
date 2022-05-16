package org.abratuhi.mavendepbuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.abratuhi.mavendepbuilder.graph.DependencyEdge;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.abratuhi.mavendepbuilder.jaxb.Dependency;
import org.abratuhi.mavendepbuilder.jaxb.Model;
import org.abratuhi.mavendepbuilder.layout.csv.CSVLayout;
import org.abratuhi.mavendepbuilder.layout.dot.DotLayout;
import org.abratuhi.mavendepbuilder.layout.gml.GMLLayout;
import org.abratuhi.mavendepbuilder.layout.graphml.GraphMLLayout;
import org.abratuhi.mavendepbuilder.model.JavaClass;
import org.abratuhi.mavendepbuilder.model.JavaPackage;
import org.abratuhi.mavendepbuilder.model.Project;
import org.abratuhi.mavendepbuilder.options.LayoutOptions;
import org.abratuhi.mavendepbuilder.parser.PomParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class MavenDependencyBuilder {

	private Set<String> excludePatterns = new HashSet<>();

  private static final Logger LOGGER = Logger.getLogger(MavenDependencyBuilder.class);

  public <S extends Graphable> void layout(
      DefaultDirectedGraph<S, DependencyEdge> dependencyGraph,
      List<DependencyEdge> violations,
      File toFile,
      LayoutOptions layoutOptions
  ) throws IOException {
    switch (layoutOptions.getFormatLayout()) {
      case GML: {
        new GMLLayout().doLayout(dependencyGraph, violations, toFile, layoutOptions);
        break;
      }
      case GRAPHML: {
        new GraphMLLayout().doLayout(dependencyGraph, violations, toFile, layoutOptions);
        break;
      }
      case DOT: {
        new DotLayout().doLayout(dependencyGraph, violations, toFile, layoutOptions);
        break;
      }
      case CSV: {
        new CSVLayout().doLayout(dependencyGraph, violations, toFile, layoutOptions);
      }
      default:
        throw new IllegalArgumentException("Unsupported format: " + layoutOptions.getFormatLayout());
    }
  }

  private Project get(Set<Project> projects, String className) {
    return projects.stream()
        .filter(project ->
            project.getClasses().stream()
                .map(JavaClass::getName)
                .collect(Collectors.toList())
                .contains(className))
        .findFirst()
        .orElse(null);
  }

  private JavaPackage get(List<JavaPackage> packages, String className) {
    return packages.stream()
        .filter(project ->
            project.getClasses().stream()
                .map(JavaClass::getName)
                .collect(Collectors.toList())
                .contains(className))
        .findFirst()
        .orElse(null);
  }

  /* default */ <S extends Graphable> DefaultDirectedWeightedGraph<S, DependencyEdge> buildDependencyGraph(Set<Project> projects, LayoutOptions.DependencyType dependencyType) {
    switch (dependencyType) {
      case PROJECT:
        return (DefaultDirectedWeightedGraph<S, DependencyEdge>) buildProjectDependencyGraph(projects);
      case PACKAGE:
        return (DefaultDirectedWeightedGraph<S, DependencyEdge>) buildPackageDependencyGraph(projects);
      default:
        throw new IllegalArgumentException("Unsupported dependency type: " + dependencyType);
    }
  }

  /* default */ DefaultDirectedWeightedGraph<Project, DependencyEdge> buildProjectDependencyGraph(Set<Project> projects) {
    Map<String, Project> projectMap = projects.stream().collect(Collectors.toMap(Project::getName, Function.identity()));
    projects.stream()
        .flatMap(project -> project.getDependencies().values().stream())
        .forEach(project -> projectMap.putIfAbsent(project.getName(), project));

    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();

    projectMap.values().stream()
        .filter(project -> !project.isLocal())
        .forEach(project -> {
          String groupId = project.getName().split(":")[0];
          String artifactId = project.getName().split(":")[1];
          try {
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create(
                        "https://repo.maven.apache.org/maven2/" +
                            groupId.replaceAll("\\.", "/") +
                            "/" +
                            artifactId +
                            "/maven-metadata.xml"))
                .GET()
                .build();
            // FIXME: use async version
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Resolved " + project.getName() + " as " + response.statusCode() + ", uri = " + request.uri());
            project.setResolvable(response.statusCode() == 200);
          } catch (Throwable t) {
            LOGGER.error("Failed " + project.getName());
          }
        });


    DefaultDirectedWeightedGraph<Project, DependencyEdge> g = new DefaultDirectedWeightedGraph<>(DependencyEdge.class);
    projectMap.values().forEach(g::addVertex);
    projectMap.values().forEach(project ->
        project.getDependencies().values()
            .forEach(dependency -> g.addEdge(project, dependency))
    );

    return g;
  }

  /* default */ DefaultDirectedWeightedGraph<JavaPackage, DependencyEdge> buildPackageDependencyGraph(Set<Project> projects) {
    List<JavaPackage> packages = projects.stream().map(Project::getPackages).flatMap(Collection::stream).collect(
        Collectors.toList());
    Map<JavaPackage, Map<JavaPackage, Set<String>>> fromToClasses = new HashMap<>();
    packages.forEach(from ->
        from.getClasses().stream().map(JavaClass::getImports).flatMap(Collection::stream).forEach(importedJavaClass -> {
          JavaPackage to = get(packages, importedJavaClass);
          if (to != null) {
            fromToClasses.putIfAbsent(from, new HashMap<>());
            fromToClasses.get(from).putIfAbsent(to, new HashSet<>());
            fromToClasses.get(from).get(to).add(importedJavaClass);
          }
        })
    );

    DefaultDirectedWeightedGraph<JavaPackage, DependencyEdge> g = new DefaultDirectedWeightedGraph<>(DependencyEdge.class);
    packages.forEach(g::addVertex);
    fromToClasses.entrySet().forEach(fromEntry ->
        fromEntry.getValue().entrySet().forEach(toEntry -> {
          if (!fromEntry.equals(toEntry)) {
            DependencyEdge de = g.addEdge(fromEntry.getKey(), toEntry.getKey());
            g.setEdgeWeight(de, fromEntry.getValue().size());
          }
        })
    );

    return g;
  }

  /**
   * Parse directory recursively, searching for projects and source classes.
   *
   * @param dir initial traversal directory
   * @throws IOException
   * @return set of found maven projects
   */
  public Set<Project> visit(File dir) throws IOException {
    Set<Project> result = visitDirectory(dir);
    AtomicInteger idIndex = new AtomicInteger(0);
    result.forEach(project -> project.setId(idIndex.getAndIncrement()));
    result.stream().map(Project::getPackages).flatMap(Collection::stream).forEach(javaPackage -> javaPackage.setId(idIndex.getAndIncrement()));
    result.stream().map(Project::getClasses).flatMap(Collection::stream).forEach(javaClass -> javaClass.setId(idIndex.getAndIncrement()));
    return result;
  }

  /**
   * Visit directory (recursively), use directory name as new project, iff it has pom.xml and a src directory containing at least one .java file.
   *
   * @param dir
   * @return
   * @throws IOException
   */
  /* default */ Set<Project> visitDirectory(File dir) throws IOException {
    final Set<Project> result = new HashSet<>();
    if (!dir.isDirectory()) {
      return result;
    }

		if (excludePatterns.stream().anyMatch(pattern -> dir.getName().matches(pattern))) {
			return result;
		}

    final Project project = new Project();

    if (new File(dir, "pom.xml").exists()) {
      Model model = PomParser.parsePomXml(new File(dir, "pom.xml"));
      project.setName(getProjectGroupId(model) + ":" + getProjectArtifactId(model));
      project.setLocal(true);
      if (model.getDependencies() != null) {
        project.setDependencies(
            model.getDependencies().getDependency().stream()
                .map(dependency -> Project.builder()
                    .name(getDependencyGroupId(dependency, model) + ":" + getDependencyArtifactId(dependency))
                    .build())
                .collect(Collectors.toMap(Project::getName, Function.identity(), (p1, p2) -> p1))
        );
      }

      if (model.getParent() != null) {
        Project parentProject = Project.builder()
            .name(model.getParent().getGroupId() + ":" + model.getParent().getArtifactId())
            .build();
        project.getDependencies().putIfAbsent(parentProject.getName(), parentProject);
      }
    }

    if (new File(dir, "src").exists()) {
      Files.walk(new File(dir, "src").toPath())
          .filter(path -> path.getFileName().toString().endsWith(".java"))
          .forEach(path -> {
            try {
              JavaClass javaClass = visitJavaClass(path.toFile());
              JavaPackage javaPackage = project.getOrAdd(javaClass.getPakkage());
              javaPackage.getClasses().add(javaClass);
            } catch (IOException e) {
              LOGGER.error(e);
            }
          });
    }

    if (StringUtils.isNotEmpty(project.getName())) {
      result.add(project);
//			result.addAll(project.getDependencies().values());
    }

    Files.list(dir.toPath()).forEach(path -> {
      try {
        result.addAll(visitDirectory(path.toFile()));
      } catch (IOException e) {
        LOGGER.error(e);
      }
    });

    return result;
  }

  private String getProjectGroupId(Model model) {
    return model.getGroupId() != null ? model.getGroupId().trim() : model.getParent().getGroupId().trim();
  }

  private String getProjectArtifactId(Model model) {
    return model.getArtifactId().trim();
  }

  private String getDependencyGroupId(Dependency dependency, Model model) {
    return dependency.getGroupId().equals("${project.groupId}") ? getProjectGroupId(model).trim() : dependency.getGroupId().trim();
  }

  private String getDependencyArtifactId(Dependency dependency) {
    return dependency.getArtifactId().trim();
  }

  /**
   * Visit java class, skip all lines not starting with <code>package</code> or <code>import</code>, interpret those lines as className and importedClasses correspondingly.
   *
   * @param javaClass
   * @return
   * @throws IOException
   */
  /* default */ JavaClass visitJavaClass(File javaClass) throws IOException {
    if (!javaClass.exists() || !javaClass.isFile()) {
      return null;
    }

    final JavaClass clazz = new JavaClass();

    String[] javaClassLines = FileUtils.readFileToString(javaClass).split("\n");
    Arrays.stream(javaClassLines).forEach(line -> {
      if (line.startsWith("package")) {
        String sPackage = line.replaceAll("package", "").replaceAll(";", "").trim();
        String sName = javaClass.getName().replaceAll("\\.java", "");
        clazz.setPakkage(sPackage);
        clazz.setName(sPackage + "." + sName);
      }

      if (line.startsWith("import")) {
        String sClass = line.replaceAll("import", "").replaceAll("static", "").replaceAll(";", "").trim();
        clazz.getImports().add(sClass);
      }
    });

    return clazz;
  }
}
