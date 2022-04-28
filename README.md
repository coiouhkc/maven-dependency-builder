# maven-dependency-builder
Tool to generate dependency graphs between Maven projects or Java packages based on class imports (.java) and to analyze their cycle-free property (similar to structure101 or stan4j).

The tool produces a dependency graph in `.gml`, `.graphml`, `.dot` or `.csv` format, where nodes correspond to Maven projects or Java packages and edges correspond to `import / import static` dependency of a Java class in one project/package to another.
Between two Maven projects or Java package there may exist at most one edge, containing all the concat'ed imports in the edge's label (depending on the specified layout options).

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
  -f,--format <arg>            Output file format (gml/graphml/dot/csv)
  -i,--input-directory <arg>   Input directory to parse for maven projects
  -n,--node-layout <arg>       Node layout type (none/text)
  -o,--output-file <arg>       Output file
  -t,--dependency-type <arg>   Dependency type (project/package)
```

As Maven Plugin from command line:
```
mvn org.abratuhi:dependency-builder-maven-plugin:1.0-SNAPSHOT:mavendepbuilder -Dmavendepbuilder.inputDir=. -Dmavendepbuilder.outputFile=./current-default-dependencies.gml
```
# Notes
**_Work-In-Progress!_** - this tool is currently under heavy development, please report any bugs or feature requests on Github.

# Known issues
* missing support for projects stored in not uniquely named directories
* missing support for import of nested classes/enums/etc.
* missing support for package (`.*`) import
* missing support for reflection

# Up next
* classes dependencies
* distinct edges for violations (color, line type, etc.)
