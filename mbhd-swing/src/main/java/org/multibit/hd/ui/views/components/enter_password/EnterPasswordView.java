package org.multibit.hd.ui.views.components.enter_password;

import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of credentials entry</li>
 * <li>Support for reveal operation</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class EnterPasswordView extends AbstractComponentView<EnterPasswordModel> {

  private static final Logger log = LoggerFactory.getLogger(EnterPasswordView.class);

  // View components
  private JPasswordField password;
  private JButton showButton;
  private JLabel spinner;

  private boolean addLabel = true;

  /**
   * @param model The model backing this view
   */
  public EnterPasswordView(EnterPasswordModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout(), // Layout
        "[][][][]", // Columns
        "[]" // Rows
      ));

    // Keep track of the credentials fields
    password = TextBoxes.newPassword();

    // Provide an invisible tar pit spinner
    spinner = Labels.newSpinner(Themes.currentTheme.fadedText(), MultiBitUI.NORMAL_PLUS_ICON_SIZE);
    spinner.setVisible(false);

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Bind a document listener to allow instant update of UI to matched passwords
    password.getDocument().addDocumentListener(
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

        /**
         * Trigger any UI updates
         */
        private void updateModel() {
          // Reset the credentials background
          password.setBackground(Themes.currentTheme.dataEntryBackground());

          getModel().get().setPassword(password.getPassword());
        }

      });

    showButton = Buttons.newShowButton(toggleDisplayAction);

    // Add to the panel
    if (isAddLabel()) {
      panel.add(Labels.newEnterPassword(), "grow,push");
    }
    panel.add(password, "grow,push");
    panel.add(showButton, "shrink");

    // Ensure the icon label is a size suitable for rotation
    panel.add(spinner, "grow," + MultiBitUI.NORMAL_PLUS_ICON_SIZE_MIG + ",wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    password.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
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
          password.setEchoChar('\0');
        } else {
          // Use the platform choice
          password.setEchoChar(echoChar);
        }

      }

    };
  }

  /**
   * @param showSpinner True if the view should show the spinner and disable other components
   */
  public void setSpinnerVisibility(boolean showSpinner) {

    spinner.setVisible(showSpinner);
    password.setEnabled(!showSpinner);
    showButton.setEnabled(!showSpinner);

  }

  public boolean isAddLabel() {
    return addLabel;
  }

  public void setAddLabel(boolean addLabel) {
    this.addLabel = addLabel;
  }

  /**
   * Handles the UI feedback for an incorrect credentials
   */
  public void incorrectPassword() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must execute on EDT.");

    password.setBackground(Themes.currentTheme.invalidDataEntryBackground());

  }
}
