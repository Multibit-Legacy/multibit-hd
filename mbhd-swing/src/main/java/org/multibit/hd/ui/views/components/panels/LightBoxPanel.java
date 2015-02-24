package org.multibit.hd.ui.views.components.panels;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.views.components.Panels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <p>Component to provide the following to UI:</p>
 * <ul>
 * <li>Provision of a light box effect where the background is dimmed</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class LightBoxPanel extends JPanel {

  private final JPanel screenPanel;
  private static final Logger log = LoggerFactory.getLogger(LightBoxPanel.class);

  /**
   * @param screenPanel The panel containing the light box components (e.g. a wizard screen panel)
   * @param layer       The layer which to place the panel (JLayeredPane.MODAL_LAYER for wizards, DRAG_LAYER for popovers within wizards)
   */
  public LightBoxPanel(JPanel screenPanel, Integer layer) {

    Preconditions.checkNotNull(screenPanel, "'panel' must be present");

    Preconditions.checkState(screenPanel.getWidth() > 0, "'width' must be greater than zero");
    Preconditions.checkState(screenPanel.getHeight() > 0, "'height' must be greater than zero");

    this.screenPanel = screenPanel;

    // Ensure we set the opacity (platform dependent)
    setOpaque(false);

    // Ensure we are visible
    setVisible(true);

    // Ensure this panel covers all the available frame area
    setSize(Panels.applicationFrame.getWidth() + 100, Panels.applicationFrame.getHeight() + 100);

    // Prevent mouse events reaching through the darkened border
    addMouseListener(new ModalMouseListener());

    // Add this panel to the frame's layered panel as the palette layer (directly above the default)
    if (JLayeredPane.MODAL_LAYER.equals(layer)) {
      Panels.applicationFrame.getLayeredPane().add(this, JLayeredPane.PALETTE_LAYER);
    } else {
      Panels.applicationFrame.getLayeredPane().add(this, JLayeredPane.POPUP_LAYER);
    }

    // Provide a starting position
    calculatePosition();

    // Add the light box panel to the frame
    Panels.applicationFrame.getLayeredPane().add(screenPanel, layer);

    log.debug("Light box panel added to application frame");

  }

  /**
   * <p>Calculate the position of the center panel</p>
   */
  private void calculatePosition() {

    int currentFrameWidth = Panels.applicationFrame.getWidth();
    int currentFrameHeight = Panels.applicationFrame.getHeight();

    // Ensure this panel covers all the available frame area allowing for fast dragging
    setSize(currentFrameWidth * 2, currentFrameHeight * 2);

    // Center the light box panel in the frame
    int x = (currentFrameWidth - screenPanel.getWidth()) / 2;
    int y = ((currentFrameHeight - screenPanel.getHeight()) / 2) - 10; // The 10 offset is a magic number

    // Avoid any negative values if resizing gets cramped
    x = x < 0 ? 0 : x;
    y = y < 0 ? 0 : y;

    screenPanel.setLocation(x, y);
  }

  /**
   * <p>Close the light box</p>
   *
   * <p>During handover the dark panel is allowed to remain to give visual continuity</p>
   */
  public void close() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on the EDT");

    // NOTE: We cannot remove by reference reliably so this approach is used

    // The light box panels are stacked in visual order therefore the usual index positions are
    // 0: Wizard content panel (JPanel.WizardCardLayout)
    // 1: Dark panel (LightBoxPanel)
    // 2: Main content (JPanel.JRootPane)
    //
    // If a popover is present it is stacked over the light box leading to
    // 0: Popover content panel (RoundedPanel)
    // 1: Popover dark panel (LightBoxPanel)
    // 2: Wizard content panel (JPanel.WizardCardLayout)
    // 3: Dark panel (LightBoxPanel)
    // 4: Main content (JPanel.JRootPane)

    JLayeredPane layeredPane = Panels.applicationFrame.getLayeredPane();

    Component[] components = layeredPane.getComponents();

    if (log.isDebugEnabled()) {
      for (int i = 0; i < components.length; i++) {
        log.debug("[{}]: {}", i, components[i].getClass().getSimpleName());
      }
    }

    boolean popoverPresent = components.length > 3;

    // Check for tooltips
    if (components.length == 4 || components.length == 6) {

      layeredPane.remove(0);

      log.debug("Removed tooltip panel");

    }

    if (components.length > 2 && components.length < 7) {

      // Remove the dark panel
      layeredPane.remove(1);

      // Remove the content panel (components will have shuffled)
      layeredPane.remove(0);

      if (popoverPresent) {
        log.debug("Popover light box panel removed from application frame");
      } else {
        log.debug("Wizard light box panel removed from application frame");
      }
    } else {
      log.error("Unknown component hierarchy in light box.");
      for (int i = 0; i < components.length; i++) {
        log.error("[{}]: {}", i, components[i].getClass().getSimpleName());
      }
    }

    // Repaint
    Panels.applicationFrame.validate();
    Panels.applicationFrame.repaint();

  }

  @Override
  protected void paintComponent(Graphics graphics) {

    // Reposition the center panel on the fly
    calculatePosition();

    if (graphics instanceof Graphics2D) {
      Graphics2D g = (Graphics2D) graphics;

      // Always use black even for light themes
      g.setPaint(Color.BLACK);

      // Set the opacity
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

      // Create the darkened border rectangle (will appear beneath the panel layer)
      g.fillRect(0, 0, Panels.applicationFrame.getWidth(), Panels.applicationFrame.getHeight());
    }
  }

  /**
   * Prevent mouse events reaching through the light box border
   */
  static class ModalMouseListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      // Do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      // Do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
      // Do nothing
    }
  }

}
