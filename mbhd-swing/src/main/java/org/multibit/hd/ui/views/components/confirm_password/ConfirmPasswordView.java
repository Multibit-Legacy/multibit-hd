package org.multibit.hd.ui.views.components.confirm_password;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a credentials confirmation</li>
 * <li>Support for reveal operation</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ConfirmPasswordView extends AbstractComponentView<ConfirmPasswordModel> {

  // View components
  private JPasswordField password1;
  private JPasswordField password2;
  private JLabel verificationStatusLabel;

  /**
   * @param model The model backing this view
   */
  public ConfirmPasswordView(ConfirmPasswordModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    // Required to support FEST testing
    final String panelName = getModel().get().getPanelName();

    panel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout(), // Layout
        "[][][][]", // Columns (require 4 columns for alignment with EnterPasswordView)
        "[][][]" // Rows
      ));

    // Keep track of the credentials fields
    password1 = TextBoxes.newPassword();
    password1.setName(MessageKey.ENTER_NEW_PASSWORD.getKey());

    password2 = TextBoxes.newPassword();
    password2.setName(MessageKey.RETYPE_NEW_PASSWORD.getKey());

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Bind a document listener to allow instant update of UI to matched passwords
    password1.getDocument().addDocumentListener(
      new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          updateModel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          updateModel();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          updateModel();
        }

        private void updateModel() {

          getModel().get().setPassword1(password1.getPassword());
          final boolean isPasswordValid = getModel().get().comparePasswords();

          // Fire the UI event for "credentials verification status" message
          ViewEvents.fireVerificationStatusChangedEvent(panelName + ".credentials", isPasswordValid);

          // Fire the UI event for "component changed" message
          ViewEvents.fireComponentChangedEvent(panelName, Optional.of(getModel()));
        }
      }
    );

    // Bind a document listener to allow instant update of UI to matched passwords
    password2.getDocument().addDocumentListener(
      new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
          updateModel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          updateModel();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          updateModel();
        }

        private void updateModel() {
          getModel().get().setPassword2(password2.getPassword());
          final boolean isPasswordValid = getModel().get().comparePasswords();

          // Fire the UI event for "credentials verification status" message
          ViewEvents.fireVerificationStatusChangedEvent(panelName + ".credentials", isPasswordValid);

          // Fire the UI event for "component changed" message
          ViewEvents.fireComponentChangedEvent(panelName, Optional.of(getModel()));
        }

      });

    // Create a new verification status panel (initially hidden)
    verificationStatusLabel = Labels.newVerificationStatus(panelName + ".credentials", true);
    verificationStatusLabel.setVisible(false);

    // Add to the panel
    // Cannot affect the focus traversal to be p1 -> p2 -> show reliably
    // Tried using cell positioning, custom traversal policy etc but
    // nothing is reliable enough and still maintain the relative locations
    // of the components
    //
    // Also the labels must be part of the component to ensure correct layout
    panel.add(Labels.newEnterNewPassword());
    panel.add(password1);
    panel.add(Buttons.newShowButton(toggleDisplayAction), "spany 2, wrap");
    panel.add(Labels.newRetypeNewPassword());
    panel.add(password2, "wrap");
    panel.add(verificationStatusLabel, "span 4,grow,push");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    password1.requestFocusInWindow();
  }

  /**
   * @return A new action for toggling the display of the seed phrase
   */
  private Action getToggleDisplayAction() {
    // Show or hide the seed phrase
    return new AbstractAction() {

      private boolean asClearText = false;
      private char echoChar = TextBoxes.getPasswordEchoChar();

      @Override
      public void actionPerformed(ActionEvent e) {

        JButton button = (JButton) e.getSource();

        if (asClearText) {
          ButtonDecorator.applyShow(button);
        } else {
          ButtonDecorator.applyHide(button);
        }
        asClearText = !asClearText;

        if (asClearText) {
          // Reveal
          password1.setEchoChar('\0');
          password2.setEchoChar('\0');
        } else {
          // Use the platform choice
          password1.setEchoChar(echoChar);
          password2.setEchoChar(echoChar);
        }

      }

    };
  }

  @Override
  public void updateModelFromView() {
  }

  @Subscribe
  public void onVerificationStatusChanged(final VerificationStatusChangedEvent event) {

    if (event.getPanelName().equals(getModel().get().getPanelName() + ".credentials") && verificationStatusLabel != null) {

      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            verificationStatusLabel.setVisible(event.isOK());
          }
        });

    }
  }

}
