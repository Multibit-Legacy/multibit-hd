package org.multibit.hd.ui.views.wizards.wallet_detail;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "wallet detail" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WalletDetailWizardModel extends AbstractWizardModel<WalletDetailState> {

  private static final Logger log = LoggerFactory.getLogger(WalletDetailWizardModel.class);

  /**
   * @param state The state object
   */
  public WalletDetailWizardModel(WalletDetailState state) {
    super(state);
  }
}
