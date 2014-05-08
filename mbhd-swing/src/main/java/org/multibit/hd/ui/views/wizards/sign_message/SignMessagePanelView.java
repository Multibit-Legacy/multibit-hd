package org.multibit.hd.ui.views.wizards.sign_message;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.text_fields.FormattedBitcoinAddressField;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Sign message: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SignMessagePanelView extends AbstractWizardPanelView<SignMessageWizardModel, String> {

  // View components
  FormattedBitcoinAddressField signingAddress;
  JTextArea signature;
  JTextArea message;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public SignMessagePanelView(AbstractWizard<SignMessageWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SIGN_MESSAGE_TITLE, AwesomeIcon.PENCIL);

  }

  @Override
  public void newPanelModel() {

    setPanelModel("");

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][][]", // Column constraints
      "[]" // Row constraints
    ));


    signingAddress = TextBoxes.newEnterBitcoinAddress(getWizardModel(), false);
    message = TextBoxes.newEnterMessage(getWizardModel(), false);
    signature = TextBoxes.newReadOnlyTextArea(getWizardModel(), 6, 40);

    AccessibilityDecorator.apply(signature, MessageKey.SIGNATURE);

    contentPanel.add(Labels.newSignMessageNote(), "span 4,wrap");

    contentPanel.add(Labels.newBitcoinAddress());
    contentPanel.add(signingAddress, "grow,span 3,push,wrap");

    contentPanel.add(Labels.newMessage());
    contentPanel.add(message, "grow,span 3,push,wrap");

    contentPanel.add(Buttons.newSignMessageButton(getSignMessageAction()), "cell 2 3,");
    contentPanel.add(Buttons.newClearAllButton(getClearAllAction()), "cell 3 3,wrap");

    contentPanel.add(Labels.newSignature());
    contentPanel.add(signature, "grow,span 3,push,wrap");


  }

  @Override
  protected void initialiseButtons(AbstractWizard<SignMessageWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        signingAddress.requestFocusInWindow();

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Ensure the wizard model correctly reflects the contents of the components
      updateFromComponentModels(Optional.absent());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Update models from component values

  }


  /**
   * @return A new action for signing the message
   */
  private Action getSignMessageAction() {

    // Sign the message
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // TODO Implement this (use model to sign then update view from model)

      }

    };
  }

  /**
   * @return A new action for signing the message
   */
  private Action getClearAllAction() {

    // Sign the message
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        signingAddress.setText("");
        message.setText("");
        signature.setText("");

      }

    };
  }

}