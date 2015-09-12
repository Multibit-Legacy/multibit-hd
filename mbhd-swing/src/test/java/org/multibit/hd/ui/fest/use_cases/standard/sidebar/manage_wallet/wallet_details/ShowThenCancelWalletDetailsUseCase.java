package org.multibit.hd.ui.fest.use_cases.standard.sidebar.manage_wallet.wallet_details;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "manage wallet" screen wallet details wizard shows</li>
 * </ul>
 * <p>Requires the "manage wallet" screen to be showing</p>
 *
 * @since 0.0.1
 */
public class ShowThenCancelWalletDetailsUseCase extends AbstractFestUseCase {

  public ShowThenCancelWalletDetailsUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "wallet details"
    window
      .button(MessageKey.SHOW_WALLET_DETAILS_WIZARD.getKey())
      .click();

    // Verify the "wallet details" wizard appears
    assertLabelText(MessageKey.WALLET_DETAILS_TITLE);

    // Verify that various labels are present
    assertThat(
      window
        .textBox(MessageKey.CLOUD_BACKUP_LOCATION.getKey())
        .requireVisible()
        .text()).isNotEmpty();

    assertThat(
      window
        .textBox(MessageKey.APPLICATION_DIRECTORY.getKey())
        .requireVisible()
        .text()).isNotEmpty();

    assertThat(
      window
        .textBox(MessageKey.WALLET_DIRECTORY.getKey())
        .requireVisible()
        .text()).isNotEmpty();

    assertThat(
      window
        .label(MessageKey.CONTACTS.getKey())
        .requireVisible()
        .text()).isNotEmpty();

    assertThat(
      window
        .label(MessageKey.PAYMENTS.getKey())
        .requireVisible()
        .text()).isNotEmpty();

    // Verify that WalletType has loaded (see #364)
    assertThat(window
      .textBox(CoreMessageKey.WALLET_CAPABILITIES.getKey())
      .requireVisible()
      .text()).isNotEmpty();

    // Verify Finish is present
    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

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
