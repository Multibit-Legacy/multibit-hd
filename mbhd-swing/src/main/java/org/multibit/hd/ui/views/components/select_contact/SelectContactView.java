package org.multibit.hd.ui.views.components.select_contact;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.views.AbstractView;
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
public class SelectContactView extends AbstractView<SelectContactModel> {

  // View components
  private JPasswordField password1;
  private JPasswordField password2;
  private JPanel verificationStatusPanel;

  /**
   * @param model The model backing this view
   */
  public SelectContactView(SelectContactModel model) {
    super(model);

  }

  @Override
  public JPanel newPanel() {

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[][][]", // Columns
      "[]10[]10[]10[]" // Rows
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

    // Create a new verification status panel
    verificationStatusPanel = Panels.newVerificationStatus();

    // Add to the panel
    panel.add(Labels.newEnterPassword());
    panel.add(password1);
    panel.add(Buttons.newShowButton(toggleDisplayAction), "spany 2,wrap");
    panel.add(Labels.newConfirmPassword());
    panel.add(password2, "wrap");
    panel.add(verificationStatusPanel, "span 3,grow,push,wrap");

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
  public void updateModel() {
  }

  @Subscribe
  public void onVerificationStatusChanged(VerificationStatusChangedEvent event) {

    if (event.getPanelName().equals(getModel().get().getPanelName())) {

      verificationStatusPanel.setVisible(event.isOK());

    }
  }

}
