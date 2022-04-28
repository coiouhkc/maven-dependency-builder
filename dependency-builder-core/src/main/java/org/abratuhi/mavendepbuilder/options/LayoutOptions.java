package org.abratuhi.mavendepbuilder.options;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alexei Bratuhin
 */
@Getter
@AllArgsConstructor
public class LayoutOptions {

  private FormatLayout formatLayout;
  private NodeLayout nodeLayout;
  private EdgeLayout edgeLayout;

  public enum NodeLayout {
    TEXT,
    NONE;

    public static NodeLayout fromString(String str) {
      return NodeLayout.valueOf(str.toUpperCase());
    }
  }

  public enum EdgeLayout {
    TEXT,
    WEIGHT,
    NONE;

    public static EdgeLayout fromString(String str) {
      return EdgeLayout.valueOf(str.toUpperCase());
    }
  }

  public enum FormatLayout {
    GML,
    GRAPHML,
    DOT;

    public static FormatLayout fromString(String str) {
      return FormatLayout.valueOf(str.toUpperCase());
    }
  }

  public enum DependencyType {
    PROJECT,
    PACKAGE,
    CLASS;

    public static DependencyType fromString(String str) {
      return DependencyType.valueOf(str.toUpperCase());
    }
  }
}
