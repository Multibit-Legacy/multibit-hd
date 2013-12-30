package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Factory to provide the following to views:</p>
 * <ul>
 * <li>Creation of panels</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Panels {

  public static final String CREATE_WALLET_ACTION_NAME = "Create";
  public static final String RESTORE_WALLET_ACTION_NAME = "Restore";
  public static final String HARDWARE_WALLET_ACTION_NAME = "Hardware";
  public static final String SELECT_WALLET_ACTION_NAME = "Select";
  public static final String WELCOME_ACTION_NAME = "Welcome";

  /**
   * A global reference to the application frame
   */
  public static JFrame frame;

  private static Optional<LightBoxPanel> lightBoxPanel = Optional.absent();

  /**
   * @param layout The layout manager for the panel (typically MigLayout)
   *
   * @return A simple theme-aware detail panel with the given layout
   */
  public static JPanel newPanel(LayoutManager2 layout) {

    JPanel panel = new JPanel(layout);

    // Theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Ensure LTR and RTL is detected by the layout
    panel.applyComponentOrientation(Languages.currentComponentOrientation());

    return panel;

  }

  /**
   * @return A simple theme-aware panel with a single cell MigLayout
   */
  public static JPanel newPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,fill,insets 0", // Layout
      "[]", // Columns
      "[]" // Rows
    ));

    return panel;

  }

  /**
   * <p>Show a light box</p>
   *
   * @param panel The panel to act as the focus of the light box
   */
  public synchronized static void showLightBox(JPanel panel) {

    Preconditions.checkState(!lightBoxPanel.isPresent(), "Light box should never be called twice");

    lightBoxPanel = Optional.of(new LightBoxPanel(panel));

  }

  /**
   * <p>Hides the currently showing light box panel</p>
   */
  public synchronized static void hideLightBox() {

    if (lightBoxPanel.isPresent()) {
      lightBoxPanel.get().close();
    }

    lightBoxPanel = Optional.absent();

  }

  /**
   * <p>Invalidate a panel so that Swing will later redraw it properly with layout changes (normally as a result of a locale change)</p>
   *
   * @param panel The panel to invalidate
   */
  public static void invalidate(JPanel panel) {

    // Added new content so validate/repaint
    panel.validate();
    panel.repaint();

  }
}
