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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public class WalletDetailModel implements Model<WalletDetail>, DocumentListener {

  private static final Logger log = LoggerFactory.getLogger(WalletDetailModel.class);

  private WalletDetail walletDetail;
  private final String panelName;

  /**
   * Flags if the model data has changed from when the panel was shown
   */
  private boolean isDirty;

  public WalletDetailModel(String panelName) {
    this.panelName = panelName;

    walletDetail = new WalletDetail();
    update();

    // Register for slow transaction seen events so it can update.
    CoreServices.uiEventBus.register(this);
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

    WalletService walletService = CoreServices.getCurrentWalletService();

    // TODO Add this to a wallet service
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
      walletDetail.setApplicationDirectory(InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath());

      File walletFile = WalletManager.INSTANCE.getCurrentWalletFile().get();
      walletDetail.setWalletDirectory(walletFile.getParentFile().getName());

      ContactService contactService = CoreServices.getOrCreateContactService(walletSummary.getWalletId());
      walletDetail.setNumberOfContacts(contactService.allContacts().size());

      walletDetail.setNumberOfPayments(walletService.getPaymentDataList().size());
    }
  }

  /**
   * @return True if the model has changed from when the panel was shown
   */
  public boolean isDirty() {
    return isDirty;
  }

  /**
   * @param isDirty True if the model has changed from when the panel was shown
   */
  public void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    setDirty(true);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    setDirty(true);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    setDirty(true);
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
