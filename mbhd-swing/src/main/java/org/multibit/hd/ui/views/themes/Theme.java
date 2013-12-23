package org.multibit.hd.ui.views.themes;

import java.awt.*;

/**
 * <p>Strategy to provide the following to themes:</p>
 * <ul>
 * <li>Various accessor methods</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public interface Theme {

  /**
   * @return The panel background colour for headers/footers
   */
  Color headerPanelBackground();

  /**
   * @return The panel background colour for detail/wizards
   */
  Color detailPanelBackground();

  /**
   * @return The panel background colour for the sidebar
   */
  Color sidebarPanelBackground();

  /**
   * @return The background colour for read only data display elements (like seed phrase display etc)
   */
  Color readOnlyBackground();

  /**
   * @return The background colour for data handling elements (like text areas, tree views etc)
   */
  Color dataEntryBackground();

  /**
   * @return The normal font colour for the theme
   */
  Color text();

  /**
   * @return A faded version of the normal font colour for the theme
   */
  Color fadedText();

  /**
   * @return The inverse font colour for the theme (to avoid clashing with panel backgrounds)
   */
  Color inverseText();

  /**
   * @return A faded version of the inverse font colour for the theme (to avoid clashing with panel backgrounds)
   */
  Color inverseFadedText();

  /**
   * @return The background colour of a danger alert
   */
  Color dangerAlertBackground();

  /**
   * @return The background colour of a danger alert
   */
  Color dangerAlertFadedBackground();

  /**
   * @return The border of a danger alert
   */
  Color dangerAlertBorder();

  /**
   * @return The text of a danger alert
   */
  Color dangerAlertText();

  /**
   * @return The background colour of a warning alert
   */
  Color warningAlertBackground();

  /**
   * @return The border of a warning alert
   */
  Color warningAlertBorder();

  /**
   * @return The text of a warning alert
   */
  Color warningAlertText();

  /**
   * @return The background colour of a success alert
   */
  Color successAlertBackground();

  /**
   * @return The border of a success alert
   */
  Color successAlertBorder();

  /**
   * @return The text of a success alert
   */
  Color successAlertText();

  /**
   * @return The background colour of an info alert
   */
  Color infoAlertBackground();

  /**
   * @return The border of an info alert
   */
  Color infoAlertBorder();

  /**
   * @return The text of an info alert
   */
  Color infoAlertText();
}
