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
   */
  public void close() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on the EDT");

    // NOTE: We cannot remove by reference reliably so this approach is used
    // The light box panels are stacked in visual order therefore the usual index positions are
    // 0: Wizard content panel
    // 1: Dark panel
    // 2: Main content
    //
    // If a popover is present it is stacked over the light box leading to
    // 0: Popover content panel
    // 1: Popover dark panel
    // 2: Wizard content panel
    // 3: Dark panel
    // 4: Main content

    try {

      // Remove the dark panel
      Panels.applicationFrame.getLayeredPane().remove(1);

    } catch (ArrayIndexOutOfBoundsException e) {
      log.warn("Light box failed to remove at position [0]");
      // Ignore so that we can remove the content at position 0
    }

    try {

      // Remove the content panel (components will have shuffled)
      Panels.applicationFrame.getLayeredPane().remove(0);

      // Log on success
      log.debug("Light box panel removed from application frame");

    } catch (ArrayIndexOutOfBoundsException e) {
      log.warn("Light box failed to remove at position [0]. Indicates a changed stacking order.");
    }

    // Repaint
    Panels.applicationFrame.validate();
    Panels.applicationFrame.repaint();

  }

  @Override
  protected void paintComponent(Graphics graphics) {

    // Reposition the center panel on the fly
    calculatePosition();

    Graphics2D g = (Graphics2D) graphics;

    // Always use black even for light themes
    g.setPaint(Color.BLACK);

    // Set the opacity
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

    // Create the darkened border rectangle (will appear beneath the panel layer)
    g.fillRect(0, 0, Panels.applicationFrame.getWidth(), Panels.applicationFrame.getHeight());

  }

  /**
   * Prevent mouse events reaching through the light box border
   */
  private class ModalMouseListener implements MouseListener {

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
