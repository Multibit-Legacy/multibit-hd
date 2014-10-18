package org.multibit.hd.ui.views.wizards.wallet_details;

import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "wallet details" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class WalletDetailsWizardModel extends AbstractWizardModel<WalletDetailsState> {

  private final WalletSummary walletSummary;

  /**
   * @param state         The state object
   * @param walletSummary The wallet summary
   */
  public WalletDetailsWizardModel(WalletDetailsState state, WalletSummary walletSummary) {
    super(state);

    this.walletSummary = walletSummary;
  }

  /**
   * @return The edited wallet summary
   */
  public WalletSummary getWalletSummary() {
    return walletSummary;
  }
}
