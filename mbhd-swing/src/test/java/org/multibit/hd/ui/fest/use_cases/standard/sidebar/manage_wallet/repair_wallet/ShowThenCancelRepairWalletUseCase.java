package org.multibit.hd.ui.fest.use_cases.standard.sidebar.manage_wallet.repair_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen repair wallet wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowThenCancelRepairWalletUseCase extends AbstractFestUseCase {

  public ShowThenCancelRepairWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "repair wallet"
    window
      .button(MessageKey.SHOW_REPAIR_WALLET_WIZARD.getKey())
      .click();

    // Verify the "repair wallet" wizard appears
    assertLabelText(MessageKey.REPAIR_WALLET_TITLE);

    // Verify cancel is present
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify next is present
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
