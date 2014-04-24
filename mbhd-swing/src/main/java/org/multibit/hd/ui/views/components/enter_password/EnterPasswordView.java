package org.multibit.hd.ui.views.components.enter_password;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of password entry</li>
 * <li>Support for reveal operation</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterPasswordView extends AbstractComponentView<EnterPasswordModel> {

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

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(), // Layout
      "[][][][]", // Columns
      "[]" // Rows
    ));

    // Keep track of the password fields
    password = TextBoxes.newPassword();

    // Provide an invisible tar pit spinner
    spinner = Labels.newSpinner();
    spinner.setVisible(false);

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Bind a key listener to allow instant update of UI to matched passwords
    password.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
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
    panel.add(spinner, "shrink,wrap");

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
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE,
            button,
            true,
            MultiBitUI.NORMAL_ICON_SIZE
          );

        } else {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE_SLASH,
            button,
            true,
            MultiBitUI.NORMAL_ICON_SIZE
          );
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
}
