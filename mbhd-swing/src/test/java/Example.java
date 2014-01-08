import javax.swing.*;
import java.awt.*;

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
public class Example {

  static public void main(String[] s) throws Exception {
    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
      if ("Nimbus".equals(info.getName())) {
        UIManager.setLookAndFeel(info.getClassName());
        break;
      }
    }

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setBounds(50, 50, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Lorem", new JLabel("Lorem"));
        pane.addTab("Ipsum", new JLabel("Ipsum"));
        pane.addTab("Dolor", new JLabel("Dolor"));
        pane.addTab("Sit", new JLabel("Sit"));
        pane.addTab("Amet", new JLabel("Amet"));

        UIDefaults tabTheme = new UIDefaults();
        tabTheme.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new Painter(Painter.BACKGROUND_ENABLED));
        tabTheme.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new Painter(Painter.BACKGROUND_DISABLED));
        tabTheme.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new Painter(Painter.BACKGROUND_ENABLED_MOUSEOVER));
        tabTheme.put("TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new Painter(Painter.BACKGROUND_ENABLED_PRESSED));
        tabTheme.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new Painter(Painter.BACKGROUND_SELECTED));
        tabTheme.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new Painter(Painter.BACKGROUND_SELECTED_DISABLED));
        tabTheme.put("TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new Painter(Painter.BACKGROUND_SELECTED_FOCUSED));
        tabTheme.put("TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new Painter(Painter.BACKGROUND_SELECTED_MOUSEOVER));
        tabTheme.put("TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new Painter(Painter.BACKGROUND_SELECTED_MOUSEOVER_FOCUSED));
        tabTheme.put("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new Painter(Painter.BACKGROUND_SELECTED_PRESSED));
        tabTheme.put("TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new Painter(Painter.BACKGROUND_SELECTED_PRESSED_FOCUSED));
        tabTheme.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new AreaPainter(AreaPainter.BACKGROUND_DISABLED));
        tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new AreaPainter(AreaPainter.BACKGROUND_ENABLED_MOUSEOVER));
        tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new AreaPainter(AreaPainter.BACKGROUND_ENABLED_PRESSED));
        tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new AreaPainter(AreaPainter.BACKGROUND_ENABLED));
        pane.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
        pane.putClientProperty("Nimbus.Overrides", tabTheme);

        JTabbedPane secondPane = new JTabbedPane();
        secondPane.addTab("Lorem", new JLabel("Lorem"));
        secondPane.addTab("Ipsum", new JLabel("Ipsum"));
        secondPane.addTab("Dolor", new JLabel("Dolor"));
        secondPane.addTab("Sit", new JLabel("Sit"));
        secondPane.addTab("Amet", new JLabel("Amet"));

        JButton button = new JButton("Hello!");

        UIDefaults buttonTheme = new UIDefaults();
        buttonTheme.put("Button[Disabled].backgroundPainter", new NamedButtonPainter(NamedButtonPainter.BACKGROUND_DISABLED));
        buttonTheme.put("Button[Enabled].backgroundPainter", new NamedButtonPainter(NamedButtonPainter.BACKGROUND_ENABLED));
        buttonTheme.put("Button[Focused].backgroundPainter", new NamedButtonPainter(NamedButtonPainter.BACKGROUND_FOCUSED));
        buttonTheme.put("Button[MouseOver].backgroundPainter", new NamedButtonPainter(NamedButtonPainter.BACKGROUND_MOUSEOVER));
        buttonTheme.put("Button[Focused+MouseOver].backgroundPainter", new NamedButtonPainter(NamedButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED));
        buttonTheme.put("Button[Pressed].backgroundPainter", new NamedButtonPainter(NamedButtonPainter.BACKGROUND_PRESSED));
        buttonTheme.put("Button[Focused+Pressed].backgroundPainter", new NamedButtonPainter(NamedButtonPainter.BACKGROUND_PRESSED_FOCUSED));


        UIManager.put( "aardvark", Color.red);
        SwingUtilities.updateComponentTreeUI(button);

        button.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
        button.putClientProperty("Nimbus.Overrides", buttonTheme);


        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pane, BorderLayout.NORTH);
        frame.getContentPane().add(secondPane, BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.setVisible(true);
      }

    });
  }
}