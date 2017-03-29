package org.abratuhi.mavendepbuilder.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "name")
public class Project {
	@Getter @Setter private Integer id = -1;
	@Getter @Setter private String name = new String();
	@Getter @Setter private Set<JavaPackage> packages = new HashSet<>();
	@Getter @Setter private Set<JavaClass> classes = new HashSet<>();

	public Optional<JavaPackage> getPackageByName(final String name) {
		return packages.stream().filter(pakkage -> StringUtils.equals(name, pakkage.getName())).findFirst();
	}
}
