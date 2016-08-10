package org.abratuhi.mavendepbuilder.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bratuhia on 04.08.2016.
 */
@NoArgsConstructor
@AllArgsConstructor
public class JavaClass {
	@Getter @Setter private String name;
	@Getter @Setter private List<String> imports = new ArrayList<>();
}
