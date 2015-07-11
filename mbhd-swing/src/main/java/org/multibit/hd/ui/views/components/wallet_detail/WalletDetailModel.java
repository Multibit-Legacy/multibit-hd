package org.multibit.hd.ui.views.components.wallet_detail;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.models.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>The detail of the wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class WalletDetailModel implements Model<WalletDetail> {

  private static final Logger log = LoggerFactory.getLogger(WalletDetailModel.class);

  private WalletDetail walletDetail;
  private final String panelName;

  public WalletDetailModel(String panelName) {
    this.panelName = panelName;

    walletDetail = new WalletDetail();
    update();

  }

  @Override
  public WalletDetail getValue() {
    return walletDetail;
  }

  @Override
  public void setValue(WalletDetail value) {

    this.walletDetail = value;

  }

  public void update() {

    Preconditions.checkNotNull(walletDetail, "Wallet detail must be set");

    // TODO Add this to a wallet service
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();

      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      File walletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();

      walletDetail.setApplicationDirectory(applicationDataDirectory.getAbsolutePath());
      walletDetail.setWalletDirectory(walletFile.getParentFile().getName());

      ContactService contactService = CoreServices.getOrCreateContactService(walletSummary.getWalletPassword());
      walletDetail.setNumberOfContacts(contactService.allContacts().size());

      if (CoreServices.getCurrentWalletService().isPresent()) {
        WalletService walletService = CoreServices.getCurrentWalletService().get();
        walletDetail.setNumberOfPayments(walletService.getPaymentDataSetSize());
      } else {
        walletDetail.setNumberOfPayments(0);
      }
    }
  }

  /**
   * Update the payments when a slowTransactionSeenEvent occurs
   */
  @Subscribe
  public void onSlowTransactionSeenEvent(SlowTransactionSeenEvent slowTransactionSeenEvent) {
    log.debug("Seen a slow transaction seen event in WalletDetailModel");

    update();
  }
}
