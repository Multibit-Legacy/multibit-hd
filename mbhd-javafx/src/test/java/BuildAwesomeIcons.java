import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Utility to provide the following to JavaFX UI:</p>
 * <ul>
 * <li>Provision of support classes to work with current version of Font Awesome</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BuildAwesomeIcons {

  private static final String FONT_AWESOME_RESOURCE_PATH = "fonts/font-awesome-4.0.1";

  public static void main(String[] args) throws IOException {

    List<String> awesomeIconLess = Resources.readLines(
      Resources.getResource(FONT_AWESOME_RESOURCE_PATH + "/less/icons.less"),
      Charsets.UTF_8
    );

    List<String> awesomeVariablesLess = Resources.readLines(
      Resources.getResource(FONT_AWESOME_RESOURCE_PATH + "/less/variables.less"),
      Charsets.UTF_8
    );

    // Extract the variable names
    Map<String, String> awesomeVariables = Maps.newLinkedHashMap();
    buildVariableMap(awesomeVariablesLess, awesomeVariables);

    // Extract the icon names
    Map<String, String> iconMap = Maps.newLinkedHashMap();
    buildIconMap(awesomeIconLess, awesomeVariables, iconMap);

    // Write the Java source code for "AwesomeIcons"
    StringBuilder sb = buildSourceCode(iconMap);

    System.out.println(sb.toString());

  }

  private static void buildVariableMap(List<String> awesomeVariablesLess, Map<String, String> awesomeVariables) {
    for (String line : awesomeVariablesLess) {

      // Look for variable definition
      if (line.trim().startsWith("@")) {

        // Split
        Iterator<String> iterator = Splitter.on(":").split(line).iterator();
        String key = iterator.next();
        String value = iterator.next();

        key = key.trim();
        value = value.replace("\\f", "\\uf").substring(0, value.length()).trim();

        System.out.printf("%s:%s%n", key, value);

        awesomeVariables.put(key, value);

      }

    }
  }

  private static void buildIconMap(List<String> awesomeIconLess, Map<String, String> awesomeVariables, Map<String, String> awesomeIcons) {
    for (String line : awesomeIconLess) {

      // Look for variable definition
      if (line.trim().startsWith(".@")) {

        // Split
        Iterator<String> iterator = Splitter.on(":").split(line).iterator();
        String key = iterator.next();
        String value = "";
        while (iterator.hasNext()) {
          value = iterator.next();
        }

        key = key.replace(".@{fa-css-prefix}-", "").trim();
        if (value.contains(",")) {
          value = ",";
        } else {
          value = value.substring(0, value.lastIndexOf(";")+1).trim();
        }

        System.out.printf("%s:%s -> ", key, value);

        // Perform variable substitution
        for (Map.Entry<String, String> entry : awesomeVariables.entrySet()) {

          String token = entry.getKey()+";";
          value = value.replace(token, entry.getValue());

          if (!value.contains("@")) {
            break;
          }

        }

        key = key.replace("-", "_").toUpperCase();

        System.out.printf("%s:%s%n", key, value);

        awesomeIcons.put(key, value);

      }

    }
  }

  private static StringBuilder buildSourceCode(Map<String, String> iconMap) {
    StringBuilder sb = new StringBuilder();
    sb.append("package org.multibit.hd.ui.javafx.fonts;\n")
      .append("\n")
      .append("/**\n")
      .append(" * <p>*** AUTO-GENERATED CODE ***</p>\n")
      .append(" * <p>Utility to provide the following to UI:</p>\n")
      .append(" * <ul>\n")
      .append(" * <li>Provision of easy references to Font Awesome Iconography</li>\n")
      .append(" * </ul>\n")
      .append(" * <p>See the BuildAwesomeIcons utility if upgrading Font Awesome</p>\n")
      .append(" * @since 0.0.1\n")
      .append(" *\n")
      .append(" */\n")
      .append("public enum AwesomeIcons {\n")
      .append("\n")
      .append("  /**\n")
      .append("  * Utilities do not have public constructors\n")
      .append("  */\n")
      .append("  private AwesomeIcons() {}\n")
      .append("\n")
    // End
    ;

    boolean aliased = false;
    for (Map.Entry<String, String> entry : iconMap.entrySet()) {

      if (aliased) {
        // Check for multiple aliasing
        if (entry.getValue().equals(",")) {
          // More aliasing to come
          sb.append(", ")
            .append(entry.getKey());
          continue;
        } else {
          // Finish off the aliased entries
          sb.append(", ")
            .append(entry.getKey())
            .append(" = ")
            .append(entry.getValue())
            .append(";\n");
          aliased = false;
          continue;
        }
      }

      // Check if we are continuing an aliased entry (multiple values for the same result)
      if (entry.getValue().equals(",")) {
        // Prepare for an aliased entry
        sb.append("  public static String ")
          .append(entry.getKey());
        // Ignore the value
        aliased = true;
      } else {
        // Normal entry
        sb.append("  public static String ")
          .append(entry.getKey())
          .append(" = ")
          .append(entry.getValue())
          .append(";\n");
        aliased = false;
      }

    }

    sb.append("}\n");
    return sb;
  }


}
