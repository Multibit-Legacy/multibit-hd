package org.multibit.hd.ui.events.view;

import org.multibit.hd.ui.views.components.wallet_detail.WalletDetail;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates the wallet detail has changed</li></li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class WalletDetailChangedEvent implements ViewEvent {

  private final WalletDetail walletDetail;

  /**
   * @param walletDetail the wallet detail
   */
  public WalletDetailChangedEvent(WalletDetail walletDetail) {

    this.walletDetail = walletDetail;

  }

  /**
   * @return The wallet detail to populate the view with
   */
  public WalletDetail getWalletDetail() {
    return walletDetail;
  }
}
