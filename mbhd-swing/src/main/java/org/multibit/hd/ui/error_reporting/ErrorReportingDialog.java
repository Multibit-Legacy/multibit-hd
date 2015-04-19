package org.multibit.hd.ui.error_reporting;

import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;

/**
 * <p>Swing dialog to provide the following to application:</p>
 * <ul>
 * <li>Reporting of error messages to user</li>
 * <li>Gathering of extra information from user</li>
 * <li>Uploading of encrypted logs to ELK stack</li>
 * </ul>
 *
 * <p>Notes:</p>
 * <ol>
 * <li>We must extend JFrame to allow for exceptions occurring before the UI has rendered</li>
 * </ol>
 *
 * @since 0.1.0
 *  
 */
public class ErrorReportingDialog extends JFrame {

  private JTextArea userMessage;

  private JLabel currentLogLabel;
  private JTextArea currentLog;
  private JScrollPane currentLogScrollPane;

  private final String message;

  /**
   * @param message A shortened message extracted from the originating exception
   */
  public ErrorReportingDialog(String message) {
    this.message = message;
    initComponents();
  }

  @SuppressWarnings("unchecked")
  private void initComponents() {

    setTitle(Languages.safeText(MessageKey.ERROR_REPORTING_TITLE));

    JPanel contentPanel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout() + ",hidemode 1", // Ensure the details do not take up space
        "[][]", // Columns
        "[]10[][][][][shrink][shrink]" // Rows
      ));

    JLabel apologyLabel = Labels.newNoteLabel(
      new MessageKey[]{
        MessageKey.ERROR_REPORTING_APOLOGY_NOTE_1,
        MessageKey.ERROR_REPORTING_APOLOGY_NOTE_2,
        MessageKey.ERROR_REPORTING_APOLOGY_NOTE_3
      },
      new Object[][]{}
    );
    JLabel notesLabel = Labels.newLabel(MessageKey.ERROR_REPORTING_NOTES);

    // User message

    // Provide space for a user message
    userMessage = TextBoxes.newTextArea(3, 40);

    // The message is a wall of text so needs scroll bars in many cases
    userMessage.setBorder(null);

    // Message requires its own scroll pane
    JScrollPane userMessageScrollPane = new JScrollPane();
    userMessageScrollPane.setOpaque(true);
    userMessageScrollPane.setBackground(Themes.currentTheme.dataEntryBackground());
    userMessageScrollPane.setBorder(null);
    userMessageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    userMessageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    userMessageScrollPane.setViewportView(userMessage);
    userMessageScrollPane.getViewport().setBackground(Themes.currentTheme.dataEntryBackground());
    userMessageScrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(userMessageScrollPane, true);

    // Current log

    // Provide space for current log
    currentLogLabel = Labels.newLabel(MessageKey.ERROR_REPORTING_CONTENTS);
    currentLog = TextBoxes.newReadOnlyTextArea(15, 40);
    currentLog.setText(ExceptionHandler.readCurrentLogfile());

    // The message is a wall of text so needs scroll bars in many cases
    currentLog.setBorder(null);

    // Message requires its own scroll pane
    currentLogScrollPane = new JScrollPane();
    currentLogScrollPane.setOpaque(true);
    currentLogScrollPane.setBackground(Themes.currentTheme.dataEntryBackground());
    currentLogScrollPane.setBorder(null);
    currentLogScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    currentLogScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    currentLogScrollPane.setViewportView(currentLog);
    currentLogScrollPane.getViewport().setBackground(Themes.currentTheme.dataEntryBackground());
    currentLogScrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(currentLogScrollPane, true);

    // Set the current log details to be invisible at the start
    currentLogLabel.setVisible(false);
    currentLogScrollPane.setVisible(false);

    // Add them to the panel
    contentPanel.add(apologyLabel, "span 2,push,wrap");

    contentPanel.add(notesLabel, "span 2,wrap");
    contentPanel.add(userMessageScrollPane, "span 2,wrap");

    contentPanel.add(Buttons.newDetailsButton(getDetailsAction()), "span 2,wrap");

    contentPanel.add(currentLogLabel,"span 2,wrap");
    contentPanel.add(currentLogScrollPane,"span 2,grow,push,wrap,wmin 10"); // wmin ensures a resize

    contentPanel.add(Buttons.newCancelButton(getCancelAction()), "align left");
    contentPanel.add(Buttons.newUploadErrorReportButton(getUploadAction()), "align right,wrap");

    getContentPane().add(contentPanel);

    addWindowListener(
      new WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
          CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);
        }


      });

    setMinimumSize(new Dimension(400,200));

    setLocationRelativeTo(null);

    pack();
    setVisible(true);

  }

  private Action getDetailsAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Toggle the visibility of the current log details
        currentLogLabel.setVisible(!currentLogLabel.isVisible());
        currentLogScrollPane.setVisible(!currentLogScrollPane.isVisible());
        pack();
      }
    };

  }

  private Action getUploadAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String truncatedMessage = userMessage.getText();
        if (Strings.isNullOrEmpty(truncatedMessage)) {
          truncatedMessage = "";
        } else if (truncatedMessage.length() > 1000) {
          truncatedMessage = truncatedMessage.substring(0, 1000);
        }

        // Upload error report
        ExceptionHandler.handleErrorReportUpload(truncatedMessage);

        // Prevent further upload attempts
        ((JButton) e.getSource()).setEnabled(false);

      }
    };

  }

  private Action getCancelAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Do nothing and simply close
        dispose();
      }
    };

  }

}
