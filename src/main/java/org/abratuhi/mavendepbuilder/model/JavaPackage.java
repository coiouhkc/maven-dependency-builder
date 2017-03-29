package org.abratuhi.mavendepbuilder.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "name")
public class JavaPackage {
	@Getter @Setter private Integer id = -1;
	@Getter @Setter private String name = new String();
	@Getter @Setter private Set<JavaClass> classes = new HashSet<>();

	public JavaPackage(final String name) {
		setName(name);
	}
}
