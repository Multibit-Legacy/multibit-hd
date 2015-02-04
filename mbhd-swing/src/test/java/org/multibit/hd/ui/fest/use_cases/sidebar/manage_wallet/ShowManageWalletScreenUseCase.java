package org.multibit.hd.ui.fest.use_cases.sidebar.manage_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "manage wallet" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ShowManageWalletScreenUseCase extends AbstractFestUseCase {

  public ShowManageWalletScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(MANAGE_WALLET_ROW)
      .click();

    // Expect the Manage Wallet screen to show

    // Row 1
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_WALLET_DETAILS_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    if (CoreServices.getOrCreateBitcoinNetworkService().isStartedOk()) {
      window
        .button(MessageKey.SHOW_EMPTY_WALLET_WIZARD.getKey())
        .requireVisible();
    }

    // Row 2

    window
      .button(MessageKey.HISTORY.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_CHANGE_PASSWORD_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SHOW_REPAIR_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
