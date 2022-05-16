package org.abratuhi.mavendepbuilder.parser;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.abratuhi.mavendepbuilder.jaxb.Model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.FileReader;

@UtilityClass
public class PomParser {

  @SneakyThrows
  public Model parsePomXml(File file) {
    JAXBContext context = JAXBContext.newInstance(Model.class);
    return ((JAXBElement<Model>) context.createUnmarshaller()
        .unmarshal(new FileReader(file))).getValue();
  }
}
