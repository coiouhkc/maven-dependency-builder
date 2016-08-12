package org.abratuhi.mavendepbuilder.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bratuhia on 04.08.2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "name")
public class Project {
	@Getter @Setter private Integer id = -1;
	@Getter @Setter private String name = new String();
	@Getter @Setter private List<JavaClass> classes = new ArrayList<>();
}
