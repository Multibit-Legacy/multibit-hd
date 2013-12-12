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
  Color dangerBackground();

  /**
   * @return The border of a danger alert
   */
  Color dangerBorder();

  /**
   * @return The text of a danger alert
   */
  Color dangerText();

  /**
   * @return The background colour of a warning alert
   */
  Color warningBackground();

  /**
   * @return The border of a warning alert
   */
  Color warningBorder();

  /**
   * @return The text of a warning alert
   */
  Color warningText();

  /**
   * @return The background colour of a success alert
   */
  Color successBackground();

  /**
   * @return The border of a success alert
   */
  Color successBorder();

  /**
   * @return The text of a success alert
   */
  Color successText();

  /**
   * @return The background colour of an info alert
   */
  Color infoBackground();

  /**
   * @return The border of an info alert
   */
  Color infoBorder();


  /**
   * @return The text of an info alert
   */
  Color infoText();
}
