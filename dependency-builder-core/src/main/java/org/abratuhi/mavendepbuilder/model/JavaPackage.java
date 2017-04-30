package org.abratuhi.mavendepbuilder.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.abratuhi.mavendepbuilder.graph.Graphable;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString (of = "name")
@EqualsAndHashCode(of = "name")
public class JavaPackage implements Graphable {
	@Getter @Setter private Integer id = -1;
	@Getter @Setter private String project = new String();
	@Getter @Setter private String name = new String();
	@Getter @Setter private Set<JavaClass> classes = new HashSet<>();

	public JavaPackage(final String project, final String name) {
		setProject(project);
		setName(name);
	}

	public JavaClass findClass(final String name) {
		return classes.stream().filter(javaClass -> javaClass.getName().equals(name)).findFirst().orElse(null);
	}

	public String getLabel() {
		return getProject() + "/" + (StringUtils.isEmpty(getName())? "default" : getName());
	}
}
