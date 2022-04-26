package org.abratuhi.mavendepbuilder.model;

import lombok.*;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(of = "name")
public class Project implements Graphable {
  @Builder.Default
  private Integer id = -1;
  @Builder.Default
  private String name = "";

	@Builder.Default
	private Map<String, Project> dependencies = new HashMap<>();

  @Builder.Default
  private Set<JavaPackage> packages = new HashSet<>();

  @Builder.Default
  private boolean local = false;

  @Builder.Default
  private boolean resolvable = false;

  public Optional<JavaPackage> getPackageByName(final String name) {
    return packages.stream().filter(pakkage -> StringUtils.equals(name, pakkage.getName())).findFirst();
  }

  public JavaPackage getOrAdd(final String packageName) {
    return packages.stream().filter(pakkage -> StringUtils.equals(packageName, pakkage.getName())).findFirst().orElseGet(() -> {
      JavaPackage javaPackage = new JavaPackage(this.name, packageName);
      packages.add(javaPackage);
      return javaPackage;
    });
  }

  public JavaClass findClass(final String name) {
    return packages.stream().map(javaPackage -> javaPackage.findClass(name)).filter(javaClass -> javaClass != null).findFirst().orElse(null);
  }

  public List<JavaClass> getClasses() {
    return packages.stream().map(JavaPackage::getClasses).flatMap(Collection::stream).collect(Collectors.toList());
  }

  public String getLabel() {
    return getName();
  }

  public String getColor() {
    if (local) {
      return "#ccccff";
    } else {
      if (resolvable) {
        return "#00ff00";
      } else {
        return "#ff0000";
      }
    }
  }
}
