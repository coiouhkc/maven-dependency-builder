# maven-dependency-builder
Tool to generate dependency graphs between Maven projects based of class imports (.java)

The tool produces a dependency graph in `.gml` format, where nodes correspond to Maven projects and edges correspond to `import` dependency of a Java class in one project from another.
Between two Maven projects there may exist at most one edge, containing all the concat imports in the edge's label.

# Build
To create a single assembly (executable jar with all dependencies)
```
mvn clean compile assembly:single
```

# Usage
From the project directory after build:
```
java -jar target\maven-dependency-builder-1.0-SNAPSHOT-jar-with-dependencies.jar
usage: java -jar <this_lib>
 -c,--check-for-violation     Whether to check for violations
  -e,--edge-layout <arg>       Edge layout type (none/weight/text)
  -f,--format <arg>            Output file format (gml/graphml)
  -i,--input-directory <arg>   Input directory to parse for maven projects
  -n,--node-layout <arg>       Node layout type (none/text)
  -o,--output-file <arg>       Output file
  -t,--dependency-type <arg>   Dependency type (project/package)
```
# Notes
This tool assumes unique maven project names and unique class names.
