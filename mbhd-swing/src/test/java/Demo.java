/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *  
 */
import org.multibit.hd.ui.views.components.combo_boxes.ThemeAwareComboBox;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.Color;
import java.awt.Graphics;

public class Demo {
  public static void main(String[] args) throws Exception {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
  public static void createAndShowGUI(){
    try {
      for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(laf.getName())) {
          UIManager.setLookAndFeel(laf.getClassName());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      if(!("Nimbus".equals(UIManager.getLookAndFeel().getName()))) {
        System.err.println("Could not find/install Nimbus LAF!");
        System.exit(-1);
      }
    }

    ThemeAwareComboBox<String> specialBox = new ThemeAwareComboBox<String>(new String[] {
      "One","Two","Three"
    });
//    specialBox.boxColor  = Color.yellow;
//    specialBox.arrowBoxColor = Color.red;

    JComboBox regularBox = new JComboBox(new String[] {
      "One","Two","Three"
    });

    JFrame frame = new JFrame();
    frame.setLayout(new java.awt.FlowLayout());
    frame.add(specialBox);
    frame.add(regularBox);
    frame.setSize(250,150);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  public static class SpecialNimbusComboBox extends JComboBox{
    public Color boxColor;
    public Color arrowBoxColor;

    private boolean ignoreRepaint;

    public SpecialNimbusComboBox() {}
    public SpecialNimbusComboBox(ComboBoxModel aModel) {super(aModel);}
    public SpecialNimbusComboBox(Object[] items) {super(items);}

    @Override
    public void paintComponent(Graphics g) {
      ignoreRepaint = true;
      try {
        java.awt.Rectangle b = getComponent(0).getBounds();
        g.setClip(0, 0, getWidth() - b.width, getHeight());
        setBackground(boxColor);
        super.paintComponent(g);
        g.setClip(b.x, b.y, b.width, b.height);
        setBackground(arrowBoxColor);
        super.paintComponent(g);
      } finally {
        ignoreRepaint = false;
      }
    }
    @Override
    public void repaint() {
      if(!ignoreRepaint)
        super.repaint();
    }
  }
}