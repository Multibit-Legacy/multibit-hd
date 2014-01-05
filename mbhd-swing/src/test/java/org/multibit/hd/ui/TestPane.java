package org.multibit.hd.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TestPane extends JPanel {

  private JTextArea textArea;
  private JButton doneButton;

  public TestPane() {

    textArea = new JTextArea(10, 50);
    doneButton = new JButton("Done");

    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(4, 4, 4, 4);

    add(new JScrollPane(textArea), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets(4, 4, 4, 4);

    add(doneButton, gbc);

    InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
    ActionMap actionMap = textArea.getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
    actionMap.put("enter", new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        System.out.println("I'm done here");

      }
    });

    doneButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("All the way out...");
      }
    });

  }

  @Override
  public void addNotify() {

    super.addNotify();

    // This is the button that will be activate by "default", depending on
    // what that means for the individual platforms...
    SwingUtilities.getRootPane(this).setDefaultButton(doneButton);

  }

  public static void main(String[] args) {

    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {

        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exp) {
        }

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(new TestPane());
        frame.setVisible(true);

      }
    });

  }

}