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

    Rectangle bounds = Panels.applicationFrame.getBounds();


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

    // Tidy up the layered pane - cannot remove by reference
    try {
      // The light box panel (dark border) is always here
      Panels.applicationFrame.getLayeredPane().remove(1);

      // The content panel (components) is always here after the removal
      Panels.applicationFrame.getLayeredPane().remove(0);

      log.debug("Light box panel removed from application frame");

    } catch (ArrayIndexOutOfBoundsException e) {
      // Ignore
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
