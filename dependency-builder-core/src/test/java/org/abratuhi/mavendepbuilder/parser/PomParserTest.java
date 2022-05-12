package org.abratuhi.mavendepbuilder.parser;

import org.abratuhi.mavendepbuilder.jaxb.Model;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PomParserTest {
  @Test
  void parsePomXml() {
    Model model = PomParser.parsePomXml(new File("src/test/resources/payara-issue-959-deps-0/pom.xml"));
    assertEquals(model.getGroupId(), "org.abratuhi");
    assertEquals(model.getArtifactId(), "payara-issue-959");
  }
}
