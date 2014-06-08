package org.multibit.hd.ui.views.wizards.sign_message;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.KeyCrypterException;
import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.WhitespaceTrimmer;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.text_fields.FormattedBitcoinAddressField;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.spongycastle.crypto.params.KeyParameter;

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

    signature = TextBoxes.newReadOnlyLengthLimitedTextArea(getWizardModel(), 6, 40);
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
  public boolean beforeHide(boolean isExitCancel, ModelAndView... mavs) {

    // Always call super() before hiding
    super.beforeHide(isExitCancel);

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

        signMessage();

      }

    };
  }

  /**
   * @return A new action for clearinging the signing address, message text and signature
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

  /**
   * Sign the message text with the address specified
   */
  public void signMessage() {
    String addressText = WhitespaceTrimmer.trim(signingAddress.getText());
    String messageText = message.getText();
    String walletPassword = "jimburton"; // TODO get from password entry on screen

    // TODO Check password is present and valid
    // TODO If not set error text appropriately

//      CharSequence walletPassword = null;
//      if (signMessagePanel.getWalletPasswordField() != null) {
//          walletPassword = CharBuffer.wrap(signMessagePanel.getWalletPasswordField().getPassword());
//
//          if (bitcoinController.getModel().getActiveWallet().isEncrypted()) {
//              if (walletPassword.length() == 0) {
//                  signMessagePanel.setMessageText1(controller.getLocaliser().getString(
//                          "showExportPrivateKeysAction.youMustEnterTheWalletPassword"));
//                  return;
//              }
//
//              if (!bitcoinController.getModel().getActiveWallet().checkPassword(walletPassword)) {
//                  // The password supplied is incorrect.
//                  signMessagePanel.setMessageText1(controller.getLocaliser().getString(
//                          "createNewReceivingAddressSubmitAction.passwordIsIncorrect"));
//                  return;
//              }
//          }
//      }


    // TODO Check address and message is present

//      if (addressText == null || "".equals(addressText)) {
//          signMessagePanel.setMessageText1(controller.getLocaliser().getString("signMessageAction.noAddress"));
//          return;
//      }
//
//      if (messageText == null || "".equals(messageText.trim())) {
//          signMessagePanel.setMessageText1(controller.getLocaliser().getString("signMessageAction.noMessage"));
//          return;
//      }

    try {
      Address signingAddress = new Address(BitcoinNetwork.current().get(), addressText);

      Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

      if (walletSummaryOptional.isPresent()) {
        WalletSummary walletSummary = walletSummaryOptional.get();

        Wallet wallet = walletSummary.getWallet();
        ECKey signingKey = wallet.findKeyFromPubHash(signingAddress.getHash160());

        if (signingKey == null) {
          // No signing key found.
          // TODO report no signing key to user
          //signMessagePanel.setMessageText1(controller.getLocaliser().getString("signMessageAction.noSigningKey", new String[]{addressText}));
        } else {
          if (signingKey.getKeyCrypter() != null) {
            KeyParameter aesKey = signingKey.getKeyCrypter().deriveKey(walletPassword);

            String signatureBase64 = signingKey.signMessage(messageText, aesKey);
            signature.setText(signatureBase64);

            // TODO report success
            //signMessagePanel.setMessageText1(controller.getLocaliser().getString("signMessageAction.success"));
          } else {
            // The signing key is not encrypted but it should be
            // TODO report to user
          }
        }

      } else {
        // TODO Report no wallet
      }

    } catch (KeyCrypterException | AddressFormatException e) {
      e.printStackTrace();
    }
  }
}