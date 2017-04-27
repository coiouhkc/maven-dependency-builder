package org.abratuhi.mavendepbuilder.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "name")
public class Project implements Graphable{
	@Getter @Setter private Integer id = -1;
	@Getter @Setter private String name = new String();
	@Getter @Setter private Set<JavaPackage> packages = new HashSet<>();

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
}
