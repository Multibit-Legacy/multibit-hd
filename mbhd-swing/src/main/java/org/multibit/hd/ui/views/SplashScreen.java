package org.multibit.hd.ui.views;

import org.multibit.hd.ui.views.components.Images;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>Java AWT Frame to provide the following to startup sequence:</p>
 * <ul>
 * <li>A quick rendering splash screen image</li>
 * </ul>
 *
 * @since 0.0.8
 *  
 */
public class SplashScreen extends Frame {

  private final transient Image image;

  public SplashScreen() throws HeadlessException {

    setLayout(new FlowLayout());
    setUndecorated(true);

    image = Images.newSplashScreenIconImage();
    setSize(image.getWidth(null), image.getHeight(null));

    setLocationRelativeTo(null);

    addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    setVisible(true);

  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.drawImage(image, 0, 0, this);
  }
}
