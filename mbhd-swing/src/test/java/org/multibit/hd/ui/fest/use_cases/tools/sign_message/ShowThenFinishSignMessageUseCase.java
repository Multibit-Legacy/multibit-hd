package org.multibit.hd.ui.fest.use_cases.tools.sign_message;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.crypto.DeterministicKey;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.SignMessageResult;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.testing.WalletFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen sign message wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowThenFinishSignMessageUseCase extends AbstractFestUseCase {

  private static final String SIGNING_TEXT = "The quick brown fox jumps over the lazy dog";

  public ShowThenFinishSignMessageUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {
    // Create a new address to use for signing
    DeterministicKey signingKey = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet().freshReceiveKey();
    Address signingAddress = signingKey.toAddress(BitcoinNetwork.current().get());
    String signingAddresString = signingAddress.toString();

    SignMessageResult signMessageResult = WalletManager.INSTANCE.signMessage(signingAddress.toString(), SIGNING_TEXT, WalletFixtures.STANDARD_PASSWORD.toString());

    // Click on Sign message
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .click();

    // Verify the sign message wizard appears
    assertLabelText(MessageKey.SIGN_MESSAGE_TITLE);

    // Verify buttons
    window
      .button(MessageKey.SIGN_MESSAGE.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.CLEAR_ALL.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify text boxes
    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .requireEnabled()
      .requireEditable();

    window
      .textBox(MessageKey.MESSAGE.getKey())
      .requireEnabled()
      .requireEditable();

     window
      .textBox(MessageKey.SIGNATURE.getKey())
      .requireEnabled()
      .requireNotEditable();

    // No address, message not password
    // Click sign message
    window
      .button(MessageKey.SIGN_MESSAGE.getKey())
      .click();

    // Check report notes - should be asking for bitcoin address
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.SIGN_MESSAGE_ENTER_ADDRESS));

    // Set the address to use with the sign
    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .setText(signingAddresString);

    // No message nor password
    // Click sign message
    window
      .button(MessageKey.SIGN_MESSAGE.getKey())
      .click();

    // Check report notes - should be asking for message
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.SIGN_MESSAGE_ENTER_MESSAGE));

    // Set the message to sign
    window
      .textBox(MessageKey.MESSAGE.getKey())
      .setText(SIGNING_TEXT);

    // No password
    // Click sign message
    window
      .button(MessageKey.SIGN_MESSAGE.getKey())
      .click();

   // Check report notes - should be asking for password
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.SIGN_MESSAGE_ENTER_PASSWORD));

    window
       .textBox(MessageKey.ENTER_PASSWORD.getKey())
       .enterText(WalletFixtures.STANDARD_PASSWORD.toString());

    // Click sign message
    window
      .button(MessageKey.SIGN_MESSAGE.getKey())
      .click();

    // Check report notes - successful sign
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.SIGN_MESSAGE_SUCCESS));

    // Check signature text
    window
      .textBox(MessageKey.SIGNATURE.getKey())
      .requireText(signMessageResult.getSignature().get());

    // Click clear all
    window
      .button(MessageKey.CLEAR_ALL.getKey())
      .click();

    // All of bitcoin address, message, signature and report notes should be blank
    window
       .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
       .requireText("");
    window
       .textBox(MessageKey.MESSAGE.getKey())
       .requireText("");
    window
       .textBox(MessageKey.ENTER_PASSWORD.getKey())
       .requireText("");
    window
       .textBox(MessageKey.SIGNATURE.getKey())
       .requireText("");
    window
       .label(MessageKey.NOTES.getKey())
       .requireText("");

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();
  }
}
