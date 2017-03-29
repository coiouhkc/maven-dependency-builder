package org.abratuhi.mavendepbuilder.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexei Bratuhin
 */
@NoArgsConstructor
@AllArgsConstructor
public class JavaClass {
	@Getter @Setter private Integer id = -1;
	@Getter @Setter private String name;
	@Getter @Setter private String pakkage;
	@Getter @Setter private List<String> imports = new ArrayList<>();
}
