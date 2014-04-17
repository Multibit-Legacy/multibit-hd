package org.multibit.hd.ui.views.wizards.edit_wallet;

import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "edit wallet" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EditWalletWizardModel extends AbstractWizardModel<EditWalletState> {

  private final WalletSummary walletSummary;

  /**
   * @param state         The state object
   * @param walletSummary The wallet summary
   */
  public EditWalletWizardModel(EditWalletState state, WalletSummary walletSummary) {
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
