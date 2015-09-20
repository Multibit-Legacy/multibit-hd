import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * <p>Various tools to provide the following to resource bundles:</p>
 * <ul>
 * <li>Ordering keys across all resource bundles</li>
 * <li>Locating similar items</li>
 * <li>Locating items with placeholders to assist appropriate parameter inclusion (e.g. branding)</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ResourceBundleTools {

  public static void main(String[] args) throws IOException {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    //findSimilarEntries();

    //findEntriesWithPlaceholders();

    matchBaseOrder();

  }

  /**
   * <p>Re-orders all resource bundles to match the base resource bundle.</p>
   * <p>This ensures that all MessageKey entries are in the same position across all bundles after a rename.</p>
   */
  private static void matchBaseOrder() throws IOException {

    // Get the contents of the base resource bundle in order
    final List<String> baseLines = Resources.readLines(Resources.getResource("languages/language.properties"), Charsets.UTF_8);

    // Create an ordered map for rapid lookup
    Map<String, String> baseMap = Maps.newLinkedHashMap();
    for (String baseLine : baseLines) {
      // Avoid comments and malformed pairs
      if (baseLine.startsWith("#") || !baseLine.contains("=")) {
        continue;
      }

      // Have a candidate pair
      String[] pair = baseLine.split("=");
      baseMap.put(pair[0], pair[1]);
    }

    // Remove any keys that are present in the base only
    for (LanguageKey languageKey : LanguageKey.values()) {
      baseMap.remove(languageKey.getKey());
    }

    // Get all the available languages
    for (LanguageKey languageKey : LanguageKey.values()) {

      String sourceFileName = "mbhd-swing/src/main/resources/languages/language_" + languageKey.getKey() + ".properties";

      String targetFileName = "mbhd-swing/src/main/resources/languages/language_" + languageKey.getKey() + ".properties";

      System.out.println("Processing: " + sourceFileName);

      List<String> targetLines = Lists.newArrayList();
      try {
        targetLines = Resources.readLines(Resources.getResource("languages/language_" + languageKey.getKey() + ".properties"), Charsets.UTF_8);
      } catch (IllegalArgumentException e) {
        System.err.println("Creating: " + targetFileName);
      }

      // Create a map for rapid lookup
      Map<String, String> targetMap = Maps.newHashMap();
      for (String targetLine : targetLines) {
        // Avoid comments and malformed pairs
        if (targetLine.startsWith("#") || !targetLine.contains("=")) {
          continue;
        }

        // Have a candidate pair
        String[] pair = targetLine.split("=");

        // Remove legacy keys
        if (!pair[0].contains(".")) {
          targetMap.put(pair[0], pair[1]);
        }
      }

      final BufferedWriter writer = new BufferedWriter(new FileWriter(targetFileName));

      // Use the ordering of the base resource to look up the matching entry in the target
      for (Map.Entry<String, String> baseEntry : baseMap.entrySet()) {

        String baseKey = baseEntry.getKey();
        String baseValue = baseEntry.getValue();

        // Add the base pair if not present in the target
        if (!targetMap.containsKey(baseKey)) {
          targetMap.put(baseKey, baseValue);
        }

        // Write the line to the target
        writer.write(baseKey + "=" + targetMap.get(baseKey));
        writer.newLine();

      }

      // Flush and close writer
      writer.flush();
      writer.close();

    }


  }

  /**
   * <p>Looks for entries outside of the MessageKey collection that are similar to those added.</p>
   * <p>If possible MessageKey should be updated to absorb the existing translated entry.</p>
   */
  public static void findSimilarEntries() {

    ResourceBundle baseBundle = ResourceBundle.getBundle(Languages.BASE_NAME);

    // Work through all the MessageKey entries
    for (MessageKey messageKey : MessageKey.values()) {

      String content = Languages.safeText(messageKey);

      // Work through all the keys in the base resource bundle (super set of MessageKey)
      for (String bundleKey : baseBundle.keySet()) {

        // Ignore MessageKey entries
        if (messageKey.getKey().equals(bundleKey)) {
          continue;
        }

        String otherContent = baseBundle.getString(bundleKey);

        if (otherContent.contains(content)) {
          // Printing resource info
          System.out.printf("MessageKey: '%s' ('%s')%n'%s' ('%s'))%n", messageKey.getKey(), content, bundleKey, otherContent);
          System.out.println("---");
        }

      }


    }

  }

  /**
   * <p>Looks for entries outside of the MessageKey collection that are similar to those added.</p>
   * <p>If possible MessageKey should be updated to absorb the existing translated entry.</p>
   */
  public static void findEntriesWithPlaceholders() {

    ResourceBundle baseBundle = ResourceBundle.getBundle(Languages.BASE_NAME);

    // Work through all the MessageKey entries
    for (MessageKey messageKey : MessageKey.values()) {

      String entry = baseBundle.getString(messageKey.getKey());

      if (entry.contains("{")) {
        System.out.println(messageKey.name());
      }

    }

  }

}
