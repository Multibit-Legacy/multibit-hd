package org.multibit.hd.ui.views.components.enter_password;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
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

  /**
   * @param model The model backing this view
   */
  public EnterPasswordView(EnterPasswordModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[][]", // Columns
      "[]" // Rows
    ));

    // Keep track of the password fields
    password = TextBoxes.newPassword();

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Bind a key listener to allow instant update of UI to matched passwords
    password.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        getModel().get().setPassword(password.getPassword());
      }

    });

    // Add to the panel
    panel.add(password,"grow,push");
    panel.add(Buttons.newShowButton(toggleDisplayAction));

    return panel;

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
          password.setEchoChar('\0');
        } else {
          // Use the platform choice
          password.setEchoChar(echoChar);
        }

      }

    };
  }

}
