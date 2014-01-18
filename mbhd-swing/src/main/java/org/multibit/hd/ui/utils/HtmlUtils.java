package org.multibit.hd.ui.utils;

import com.google.common.base.Strings;
import org.multibit.hd.ui.i18n.Languages;

/**
 * <p>Utilities to provide the following to application:</p>
 * <ul>
 * <li>Decorating text with HTML</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HtmlUtils {

  /**
   * Utilities have private constructor
   */
  private HtmlUtils() {
  }


  /**
   * @param lines The lines to wrap in HTML
   *
   * @return A single block of HTML that provides correct text alignment and line breaks for the locale
   */
  public static String localiseWithLineBreaks(String[] lines) {

    final StringBuilder sb;

    if (Languages.isLeftToRight()) {
      sb = new StringBuilder("<html><div align=left>");
    } else {
      sb = new StringBuilder("<html><div align=right>");
    }
    for (String line : lines) {
      sb.append(line);
      sb.append("<br/><br/>");
    }
    sb.append("</div></html>");

    return sb.toString();
  }

  /**
   * @param fragment   The text fragment to use as the basis for emboldened text
   * @param sourceText The source text containing the fragment
   *
   * @return The source text with HTML markup to embolden the matching fragments preserving the source case
   */
  public static String applyBoldFragments(String fragment, String sourceText) {

    if (Strings.isNullOrEmpty(fragment) || Strings.isNullOrEmpty(sourceText)) {
      return "<html>"+sourceText+"</html>";
    }

    String lowerFragment = fragment.toLowerCase();
    String lowerSource = sourceText.toLowerCase();

    // Find the match locations within the source text
    int sourceIndex = 0;
    int matchIndex;
    StringBuilder sb = new StringBuilder("<html>");
    do {

      // Match using case-insensitivity
      matchIndex = lowerSource.indexOf(lowerFragment, sourceIndex);

      if (matchIndex > -1) {

        // Decorate the original source text to preserve case
        sb.append(sourceText.substring(sourceIndex, matchIndex))
          .append("<b>")
          .append(sourceText.substring(matchIndex, matchIndex + fragment.length()))
          .append("</b>");

        sourceIndex = matchIndex + fragment.length();
      }

    } while (matchIndex > -1);

    sb.append(sourceText.substring(sourceIndex));
    sb.append("</html>");

    return sb.toString();
  }

}
