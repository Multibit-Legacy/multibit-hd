package org.multibit.hd.ui.views.components.confirm_password;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
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
 * <li>Presentation of a password confirmation</li>
 * <li>Support for reveal operation</li>
 * </ul>
 *
 * @since 0.0.1
 *  
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

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(), // Layout
      "[][][][]", // Columns (require 4 columns for alignment with EnterPasswordView)
      "[][][]" // Rows
    ));

    // Keep track of the password fields
    password1 = TextBoxes.newPassword();
    password2 = TextBoxes.newPassword();

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Bind a key listener to allow instant update of UI to matched passwords
    password1.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        getModel().get().setPassword1(password1.getPassword());
        getModel().get().comparePasswords();
      }

    });
    password2.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        getModel().get().setPassword2(password2.getPassword());
        getModel().get().comparePasswords();
      }

    });

    // Create a new verification status panel (initially hidden)
    verificationStatusLabel = Labels.newVerificationStatus(true);
    verificationStatusLabel.setVisible(false);

    JLabel spinner = Labels.newSpinner();
    spinner.setVisible(false);

    // Add to the panel
    // Cannot affect the focus traversal to be p1 -> p2 -> eye reliably
    // Tried using cell positioning, custom traversal policy etc but
    // nothing is reliable enough
    //
    // Also the labels must be part of the component to ensure correct layout
    panel.add(Labels.newEnterNewPassword());
    panel.add(password1);
    panel.add(Buttons.newShowButton(toggleDisplayAction), "spany 2");
    panel.add(spinner, "spany 2,wrap");
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
  public void onVerificationStatusChanged(VerificationStatusChangedEvent event) {

    if (event.getPanelName().equals(getModel().get().getPanelName())) {

      verificationStatusLabel.setVisible(event.isOK());

    }
  }

}
