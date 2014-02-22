package org.multibit.hd.ui.views.components.wallet_detail;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.WalletDetailChangedEvent;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Details of a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WalletDetailView extends AbstractComponentView<WalletDetailModel> {

  // View components
  JLabel applicationDirectoryLabel;
  JLabel walletDirectoryLabel;
  JLabel numberOfContactsLabel;
  JLabel numberOfTransactionsLabel;

  /**
   * @param model The model backing this view
   */
  public WalletDetailView(WalletDetailModel model) {
    super(model);

    CoreServices.uiEventBus.register(this);
  }

  @Override
  public JPanel newComponentPanel() {

    WalletDetailModel model = getModel().get();
    WalletDetail walletDetail = model.getValue();

    panel = Panels.newPanel(new MigLayout(
            "fillx", // Layout
            "[]10[grow]", // Columns
            "[][][][]"  // Rows
    ));

    panel.add(new JLabel(Languages.safeText(MessageKey.SUMMARY)), "wrap");

    panel.add(new JLabel(Languages.safeText(MessageKey.APPLICATION_DIRECTORY) + ":"));
    applicationDirectoryLabel = new JLabel(walletDetail.getApplicationDirectory());
    panel.add(applicationDirectoryLabel, "push,wrap");

    panel.add(new JLabel(Languages.safeText(MessageKey.WALLET_DIRECTORY) + ":"));
    walletDirectoryLabel = new JLabel(walletDetail.getWalletDirectory());
    panel.add(walletDirectoryLabel, "push,wrap");

    panel.add(new JLabel(Languages.safeText(MessageKey.CONTACTS) + ":"));
    numberOfContactsLabel = new JLabel(String.valueOf(walletDetail.getNumberOfContacts()));
    panel.add(numberOfContactsLabel, "push,wrap");

    panel.add(new JLabel(Languages.safeText(MessageKey.TRANSACTIONS) + ":"));
    numberOfTransactionsLabel = new JLabel(String.valueOf(walletDetail.getNumberOfPayments()));
    panel.add(numberOfTransactionsLabel, "push,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
  }

  @Override
  public void updateModelFromView() {

  }

  @Override
  public void updateViewFromModel() {
  }

  @Subscribe
  public void onWalletDetailChangedEvent(WalletDetailChangedEvent walletDetailChangedEvent) {
    WalletDetail walletDetail = walletDetailChangedEvent.getWalletDetail();
    applicationDirectoryLabel.setText(walletDetail.getApplicationDirectory());
    walletDirectoryLabel.setText(walletDetail.getWalletDirectory());
    numberOfContactsLabel.setText(String.valueOf(walletDetail.getNumberOfContacts()));
    numberOfTransactionsLabel.setText(String.valueOf(walletDetail.getNumberOfPayments()));
  }

}


