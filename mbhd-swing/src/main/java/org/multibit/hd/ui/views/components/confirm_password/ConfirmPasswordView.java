package org.multibit.hd.ui.views.components.confirm_password;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.View;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a password confirmation</li>
 * <li>Support for reveal operation</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ConfirmPasswordView implements View<ConfirmPasswordModel> {

  private JPasswordField password1;
  private JPasswordField password2;

  @Override
  public JPanel newPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,insets 0", // Layout
      "[][][]", // Columns
      "[]10[]10[]" // Rows
    ));

    password1 = TextBoxes.newPassword();
    password2 = TextBoxes.newPassword();

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Add to the panel
    panel.add(Labels.newEnterPassword());
    panel.add(password1);
    panel.add(Buttons.newShowButton(toggleDisplayAction), "spany 2,wrap");
    panel.add(Labels.newConfirmPassword());
    panel.add(password2,"wrap");

    return panel;


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
            AwesomeDecorator.NORMAL_ICON_SIZE
          );

        } else {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE_SLASH,
            button,
            true,
            AwesomeDecorator.NORMAL_ICON_SIZE
          );
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
  public void setModel(ConfirmPasswordModel model) {
    // Do nothing
  }

  @Override
  public void updateModel() {
    // Do nothing
  }

}
