package org.multibit.hd.ui.views.wizards.verify_message;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.WhitespaceTrimmer;
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
 * <li>Verify message: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class VerifyMessagePanelView extends AbstractWizardPanelView<VerifyMessageWizardModel, String> {

  // View components
  FormattedBitcoinAddressField verifyingAddress;
  JTextArea signature;
  JTextArea message;

  JLabel reportLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public VerifyMessagePanelView(AbstractWizard<VerifyMessageWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.VERIFY_MESSAGE_TITLE, AwesomeIcon.CHECK);

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
      "[]5[][][][30:n:]" // Row constraints
    ));


    verifyingAddress = TextBoxes.newEnterBitcoinAddress(getWizardModel(), false);
    message = TextBoxes.newEnterMessage(getWizardModel(), false);

    signature = TextBoxes.newTextArea(5, 40);
    AccessibilityDecorator.apply(signature, MessageKey.SIGNATURE);

    contentPanel.add(Labels.newVerifyMessageNote(), "span 4,wrap");

    contentPanel.add(Labels.newBitcoinAddress());
    contentPanel.add(verifyingAddress, "grow,span 3,push,wrap");

    contentPanel.add(Labels.newMessage());
    contentPanel.add(message, "grow,span 3,push,wrap");

    contentPanel.add(Labels.newSignature());
    contentPanel.add(signature, "grow,span 3,push,wrap");

    contentPanel.add(Buttons.newVerifyMessageButton(getSignMessageAction()), "cell 2 4,");
    contentPanel.add(Buttons.newClearAllButton(getClearAllAction()), "cell 3 4,wrap");

    reportLabel = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    contentPanel.add(reportLabel, "span 4,wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<VerifyMessageWizardModel> wizard) {

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

        verifyingAddress.requestFocusInWindow();

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

        verifyMessage();

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

        verifyingAddress.setText("");
        message.setText("");
        signature.setText("");
        setReportText(Optional.<Boolean>absent(), null, null);

      }

    };
  }

  /**
    * Verify the message text with the address specified
    */
   public void verifyMessage() {
     String addressText = WhitespaceTrimmer.trim(verifyingAddress.getText());
     String messageText = message.getText();
     String signatureText = signature.getText();

     if (Strings.isNullOrEmpty(addressText)) {
       setReportText(Optional.of(Boolean.FALSE), MessageKey.VERIFY_MESSAGE_ENTER_ADDRESS, null);
       return;
     }

     if (Strings.isNullOrEmpty(messageText)) {
       setReportText(Optional.of(Boolean.FALSE), MessageKey.VERIFY_MESSAGE_ENTER_MESSAGE, null);
       return;
     }

     if (Strings.isNullOrEmpty(signatureText)) {
       setReportText(Optional.of(Boolean.FALSE), MessageKey.VERIFY_MESSAGE_ENTER_SIGNATURE, null);
       return;
     }

     try {
       Address signingAddress = new Address(BitcoinNetwork.current().get(), addressText);

       Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

       if (walletSummaryOptional.isPresent()) {
         WalletSummary walletSummary = walletSummaryOptional.get();

         Wallet wallet = walletSummary.getWallet();

         ECKey key = ECKey.signedMessageToKey(messageText, signatureText);
         Address gotAddress = key.toAddress(BitcoinNetwork.current().get());
         if (signingAddress.equals(gotAddress)) {
           setReportText(Optional.of(Boolean.TRUE), MessageKey.VERIFY_MESSAGE_VERIFY_SUCCESS, null);
         } else {
           setReportText(Optional.of(Boolean.FALSE), MessageKey.VERIFY_MESSAGE_VERIFY_FAILURE, null);
         }
       } else {
         // No wallet
         setReportText(Optional.of(Boolean.FALSE), MessageKey.SIGN_MESSAGE_NO_WALLET, null);
       }

     } catch (Exception e) {
       setReportText(Optional.of(Boolean.FALSE), MessageKey.VERIFY_MESSAGE_FAILURE, null);

       e.printStackTrace();
     }
   }

   private void setReportText(Optional<Boolean> status, MessageKey messageKey, Object[] messageData) {
     if (reportLabel != null) {
       if (messageKey == null) {
         reportLabel.setText("");
       } else {
         reportLabel.setText(Languages.safeText(messageKey, messageData));
       }
       if (status.isPresent()) {
         Labels.decorateStatusLabel(reportLabel, status);
       } else {
         reportLabel.setIcon(null);
       }
     }
   }
}