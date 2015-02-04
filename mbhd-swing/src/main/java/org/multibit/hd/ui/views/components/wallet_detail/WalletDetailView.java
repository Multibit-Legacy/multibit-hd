package org.multibit.hd.ui.views.components.wallet_detail;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.WalletDetailChangedEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;

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
  JTextField cloudBackupDirectoryTextField;
  JTextField applicationDirectoryTextField;
  JTextField walletDirectoryTextField;

  JLabel numberOfContactsLabel;
  JLabel numberOfTransactionsLabel;

  JTextArea walletCapabilitiesTextArea;

  /**
   * @param model The model backing this view
   */
  public WalletDetailView(WalletDetailModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(),
      "[]10[grow]", // Columns
      "[][][][][]"  // Rows
    ));

    WalletDetailModel model = getModel().get();
    WalletDetail walletDetail = model.getValue();

    // This should be present
    WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();

    // Cloud backup location (limit width)
    panel.add(Labels.newValueLabel(Languages.safeText(MessageKey.CLOUD_BACKUP_LOCATION)));
    cloudBackupDirectoryTextField = TextBoxes.newReadOnlyTextField(40, MessageKey.CLOUD_BACKUP_LOCATION, MessageKey.CLOUD_BACKUP_LOCATION);
    cloudBackupDirectoryTextField.setText(Configurations.currentConfiguration.getAppearance().getCloudBackupLocation());
    panel.add(cloudBackupDirectoryTextField, MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    // Application directory (limit width)
    panel.add(Labels.newValueLabel(Languages.safeText(MessageKey.APPLICATION_DIRECTORY)));
    applicationDirectoryTextField = TextBoxes.newReadOnlyTextField(40, MessageKey.APPLICATION_DIRECTORY, MessageKey.APPLICATION_DIRECTORY);
    applicationDirectoryTextField.setText(walletDetail.getApplicationDirectory());
    panel.add(applicationDirectoryTextField, MultiBitUI.WIZARD_MAX_WIDTH_MIG+",wrap");

    // Wallet directory (limit width)
    panel.add(Labels.newValueLabel(Languages.safeText(MessageKey.WALLET_DIRECTORY)));
    walletDirectoryTextField = TextBoxes.newReadOnlyTextField(40, MessageKey.WALLET_DIRECTORY, MessageKey.WALLET_DIRECTORY);
    walletDirectoryTextField.setText(walletDetail.getWalletDirectory());
    panel.add(walletDirectoryTextField, MultiBitUI.WIZARD_MAX_WIDTH_MIG+",wrap");

    // Contacts
    panel.add(Labels.newLabel(MessageKey.CONTACTS));
    numberOfContactsLabel = Labels.newValueLabel(String.valueOf(walletDetail.getNumberOfContacts()));
    panel.add(numberOfContactsLabel, "wrap");

    // Transactions
    panel.add(Labels.newLabel(MessageKey.PAYMENTS));
    numberOfTransactionsLabel = Labels.newValueLabel(String.valueOf(walletDetail.getNumberOfPayments()));
    panel.add(numberOfTransactionsLabel, "wrap");

    // Capabilities (limit width)
    panel.add(Labels.newLabel(CoreMessageKey.WALLET_CAPABILITIES),"wrap");
    walletCapabilitiesTextArea = TextBoxes.newReadOnlyTextArea(4,50);
    AccessibilityDecorator.apply(walletCapabilitiesTextArea, CoreMessageKey.WALLET_CAPABILITIES);
    // Can only provide capabilities for known wallet types and we can't make assumptions
    if (walletSummary.getWalletType() != null) {
      walletCapabilitiesTextArea.setText(Languages.safeText(walletSummary.getWalletType().getKey()));
    }
    panel.add(walletCapabilitiesTextArea, "span 2,wrap");

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

    applicationDirectoryTextField.setText(walletDetail.getApplicationDirectory());
    walletDirectoryTextField.setText(walletDetail.getWalletDirectory());

    numberOfContactsLabel.setText(String.valueOf(walletDetail.getNumberOfContacts()));
    numberOfTransactionsLabel.setText(String.valueOf(walletDetail.getNumberOfPayments()));

  }
}


