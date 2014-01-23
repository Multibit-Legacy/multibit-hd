package org.multibit.hd.ui.views.components;

import com.google.common.base.Preconditions;

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

  private final JPanel panel;
  private final Integer layer;

  /**
   * @param panel The panel containing the light box components (e.g. a wizard)
   * @param layer The layer which to place the panel (JLayeredPane.POPUP_LAYER is normal, DRAG_LAYER for popovers)
   */
  public LightBoxPanel(JPanel panel, Integer layer) {

    Preconditions.checkNotNull(panel, "'panel' must be present");
    Preconditions.checkState(panel.getWidth() > 0, "'width' must be greater than zero");
    Preconditions.checkState(panel.getHeight() > 0, "'height' must be greater than zero");

    this.panel = panel;
    this.layer = layer;

    // Ensure we set the opacity (platform dependent)
    setOpaque(false);

    // Ensure this panel covers all the available frame area
    setSize(Panels.frame.getWidth() + 100, Panels.frame.getHeight() + 100);

    // Prevent mouse events reaching through the darkened border
    addMouseListener(new ModalMouseListener());

    // TODO Prevent focus events reaching through the darkened border

    // Add this panel to the frame's layered panel as the palette layer (directly above the default)
    if (JLayeredPane.MODAL_LAYER.equals(layer)) {
      Panels.frame.getLayeredPane().add(this, JLayeredPane.PALETTE_LAYER);
    } else {
      Panels.frame.getLayeredPane().add(this, JLayeredPane.POPUP_LAYER);
    }

    // Provide a starting position
    calculatePosition();

    // Add the light box panel to the frame as the popup layer (over everything except a drag/drop layer)
    Panels.frame.getLayeredPane().add(panel, layer);

  }

  /**
   * <p>Calculate the position of the center panel</p>
   */
  private void calculatePosition() {

    int currentFrameWidth = Panels.frame.getWidth();
    int currentFrameHeight = Panels.frame.getHeight();

    int minPanelWidth = (int) panel.getMinimumSize().getWidth();
    int minPanelHeight = (int) panel.getMinimumSize().getHeight();

    // Use the panel's minimum size to prevent further resizing
    int frameWidth = currentFrameWidth < minPanelWidth ? minPanelWidth : currentFrameWidth;
    int frameHeight = currentFrameHeight < minPanelHeight ? minPanelHeight : currentFrameHeight;

    // Lock in the calculated height
    Panels.frame.setSize(frameWidth, frameHeight);

    // Ensure this panel covers all the available frame area allowing for fast dragging
    setSize(frameWidth * 2, frameHeight * 2);

    // Center the light box panel in the frame
    int x = (frameWidth - panel.getWidth()) / 2;
    int y = (frameHeight - panel.getHeight()) / 2;

    // Avoid any negative values if resizing gets cramped
    x = x < 0 ? 0 : x;
    y = y < 0 ? 0 : y;

    panel.setLocation(x, y);
  }

  /**
   * <p>Close the light box</p>
   */
  public void close() {

    // Tidy up the layered pane
    Panels.frame.getLayeredPane().remove(1);
    Panels.frame.getLayeredPane().remove(panel);

    // Repaint
    Panels.frame.validate();
    Panels.frame.repaint();

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
    g.fillRect(0, 0, Panels.frame.getWidth(), Panels.frame.getHeight());

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
