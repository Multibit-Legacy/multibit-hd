package org.multibit.hd.ui.fest.use_cases.standard.sidebar.manage_wallet.payment_settings;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.blockexplorer.BlockExplorer;
import org.multibit.hd.core.blockexplorer.BlockExplorers;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "block explorer" behaviour</li>
 * </ul>
 * <p>Requires the "manage wallet" screen to be showing</p>
 *
 * @since 0.0.1
 */
public class VerifyPaymentSettingsBlockExplorerUseCase extends AbstractFestUseCase {

  public VerifyPaymentSettingsBlockExplorerUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Select each block explorer in turn, checking it sets the configuration correctly
    List<BlockExplorer> allBlockExplorers = BlockExplorers.getAll();

    for (BlockExplorer loopBlockExplorer : allBlockExplorers) {
      // Click on "payment settings"
      window
        .button(MessageKey.SHOW_PAYMENT_SETTINGS_WIZARD.getKey())
        .click();

      // Allow time for component to change
      pauseForComponentReset();

      // Verify the "payment settings" wizard appears
      assertLabelText(MessageKey.PAYMENT_SETTINGS_TITLE);

      // Verify Apply is present
      window
        .button(MessageKey.APPLY.getKey())
        .requireVisible()
        .requireEnabled();

      // Verify Cancel is present
      window
        .button(MessageKey.CANCEL.getKey())
        .requireVisible()
        .requireEnabled();

      // Select the loop block explorer
      window
        .comboBox(MessageKey.BLOCK_EXPLORER.getKey())
        .selectItem(loopBlockExplorer.getName());

      // Click Apply
      window
        .button(MessageKey.APPLY.getKey())
        .click();

      pauseForViewReset();

      // Verify the underlying screen is back
      window
        .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
        .requireVisible()
        .requireEnabled();

      // Verify block explorer selected has changed the configuration
      assertThat(Configurations.currentConfiguration.getAppearance().getBlockExplorerId()).isEqualTo(loopBlockExplorer.getId());
    }
  }
}
