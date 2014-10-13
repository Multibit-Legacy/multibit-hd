package org.multibit.hd.ui.fest.use_cases.sidebar.tools.verify_message;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen verify message wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowThenFinishVerifyMessageUseCase extends AbstractFestUseCase {

  public ShowThenFinishVerifyMessageUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Verify message
    window
      .button(MessageKey.SHOW_VERIFY_WIZARD.getKey())
      .click();

    // Verify the "verify message" wizard appears
    assertLabelText(MessageKey.VERIFY_MESSAGE_TITLE);

    // Verify finish is present
    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

    // No address,message nor signature
    // Click verify message
    window
      .button(MessageKey.VERIFY_MESSAGE.getKey())
      .click();

    // Check report notes - should be ask for bitcoin address
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.VERIFY_MESSAGE_ENTER_ADDRESS));

    // Set the address to use with the verify
    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .setText("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    // No message nor signature
    // Click verify message
    window
      .button(MessageKey.VERIFY_MESSAGE.getKey())
      .click();

    // Check report notes - should be ask for message
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.VERIFY_MESSAGE_ENTER_MESSAGE));

    // Set the message to verify
    window
      .textBox(MessageKey.MESSAGE.getKey())
      .setText("The quick brown fox jumps over the lazy dog");

    // No signature
    // Click verify message
    window
      .button(MessageKey.VERIFY_MESSAGE.getKey())
      .click();

    // Check report notes - should be ask for signature
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.VERIFY_MESSAGE_ENTER_SIGNATURE));

    // Set the signature to verify
    window
      .textBox(MessageKey.SIGNATURE.getKey())
      .setText("HF76s9EHMl9NHAyLm38b8z7iXC0y3nJVfyXXKYjt7un1Xkau29OK27Nl7hRhS9UyE++p+R6/WyXIwmWC0blc2XY=");

    // Click verify message
    window
      .button(MessageKey.VERIFY_MESSAGE.getKey())
      .click();

    // Check report notes - successful verify
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.VERIFY_MESSAGE_VERIFY_SUCCESS));

    // Set wrong signature - 'kau' is reversed
    window
      .textBox(MessageKey.SIGNATURE.getKey())
      .setText("HF76s9EHMl9NHAyLm38b8z7iXC0y3nJVfyXXKYjt7un1XuaK29OK27Nl7hRhS9UyE++p+R6/WyXIwmWC0blc2XY=");

    // Click verify message
    window
      .button(MessageKey.VERIFY_MESSAGE.getKey())
      .click();

    // Check report notes - unsuccessful verify
    window
       .label(MessageKey.NOTES.getKey())
       .requireVisible()
       .requireEnabled()
       .requireText(Languages.safeText(CoreMessageKey.VERIFY_MESSAGE_VERIFY_FAILURE));

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
