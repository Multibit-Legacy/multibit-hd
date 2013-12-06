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

  /**
   * @param panel The panel containing the light box components (e.g. a wizard)
   */
  public LightBoxPanel(JPanel panel) {

    Preconditions.checkNotNull(panel,"'panel' must be present");
    Preconditions.checkState(panel.getWidth() > 0,"'width' must be greater than zero");
    Preconditions.checkState(panel.getHeight() > 0,"'height' must be greater than zero");

    this.panel = panel;

    // Ensure we set the opacity (platform dependent)
    this.setOpaque(false);

    // Ensure this panel covers all the available screen area and force a repaint
    this.setSize(Toolkit.getDefaultToolkit().getScreenSize());

    // Prevent mouse events reaching through the darkened border
    this.addMouseListener(new ModalMouseListener());

    // Add this panel to the frame's layered panel as the palette layer (directly above the default)
    Panels.frame.getLayeredPane().add(this, JLayeredPane.PALETTE_LAYER);

    // Center the light box panel in the frame
    int x = (Panels.frame.getWidth() - panel.getWidth()) / 2;
    int y = (Panels.frame.getHeight() - panel.getHeight()) / 2;
    panel.setLocation(x, y);

    // Add the light box panel to the frame as the popup layer (over everything except a drag/drop layer)
    Panels.frame.getLayeredPane().add(panel, JLayeredPane.POPUP_LAYER);

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

    Graphics2D g = (Graphics2D) graphics;

    // Always use black even for light themes
    g.setPaint(Color.BLACK);

    // Set the opacity
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

    // Create the darkened border rectangle (will appear beneath the panel layer)
    g.fillRect(0, 0, getWidth(), getHeight());

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
