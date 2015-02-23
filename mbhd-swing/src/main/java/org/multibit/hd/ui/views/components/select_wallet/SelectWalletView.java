package org.multibit.hd.ui.views.components.select_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>User entry of a wallet selection</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SelectWalletView extends AbstractComponentView<SelectWalletModel> implements ActionListener {

  private static final Logger log = LoggerFactory.getLogger(SelectWalletView.class);

  // View components
  private JComboBox<WalletSummary> selectedWalletComboBox;
  private JTextArea descriptionTextArea;

  /**
   * @param model The model backing this view
   */
  public SelectWalletView(SelectWalletModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    SelectWalletModel model = getModel().get();

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(),
      "[][]", // Columns
      "[][]" // Rows
    ));

    // Provide the initial list
    selectedWalletComboBox = ComboBoxes.newSelectWalletComboBox(this, model.getWalletList());
    selectedWalletComboBox.setEnabled(false);

    // Create the labels
    descriptionTextArea = TextBoxes.newTextArea(3, 30);
    descriptionTextArea.setEditable(false);

    // Ensure it is accessible
    AccessibilityDecorator.apply(descriptionTextArea, MessageKey.DESCRIPTION);

    // Ensure we maintain the overall theme
    JScrollPane scrollPane = ScrollPanes.newDataEntryScrollPane(descriptionTextArea);

    // Add to the panel
    panel.add(selectedWalletComboBox, "grow,push,w min:501:,wrap");
    panel.add(scrollPane, "grow," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    return panel;
  }

  @Override
  public void requestInitialFocus() {
    selectedWalletComboBox.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {

    // See the action listener

  }

  @Override
  public void updateViewFromModel() {

    selectedWalletComboBox.removeActionListener(this);
    selectedWalletComboBox.removeAllItems();

    List<WalletSummary> walletList = getModel().get().getWalletList();
    Optional<String> currentWalletRootFromConfiguration = WalletManager.INSTANCE.getCurrentWalletRoot();

    if (currentWalletRootFromConfiguration.isPresent()) {
      // We have a current select so select that when we add it
      String currentWalletRoot  = currentWalletRootFromConfiguration.get();
      int index = 0;
      for (WalletSummary walletSummary : walletList) {
        // Add all the wallet summaries
        selectedWalletComboBox.addItem(walletSummary);

        // Select this entry if the formatted wallet id matches the last seen soft wallet root
        // (this is persisted in the WalletConfiguration)
        if (currentWalletRoot.endsWith(walletSummary.getWalletId().toFormattedString())) {
            selectedWalletComboBox.setSelectedIndex(index);
        }
        index++;
      }
    } else {
      // We have no current selection so add anything that's available
      // so long as it isn't a Trezor hard wallet
      for (WalletSummary walletSummary : walletList) {
        if (!WalletType.TREZOR_HARD_WALLET.equals(walletSummary.getWalletType())) {
          selectedWalletComboBox.addItem(walletSummary);
        } else {
          log.debug("Ignoring Trezor hard wallet: {}", walletSummary.getName());
        }
      }
    }
    selectedWalletComboBox.addActionListener(this);

    // Update the description if there is a selection
    if (selectedWalletComboBox.getSelectedIndex() != -1) {

      WalletSummary selectedWallet = (WalletSummary) selectedWalletComboBox.getSelectedItem();

      if (selectedWallet != null) {
        getModel().get().setValue(selectedWallet);
        descriptionTextArea.setText(selectedWallet.getNotes());
        descriptionTextArea.setCaretPosition(0);
      }
    }
  }

  /**
   * <p>Handle the change locale action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    WalletSummary selectedWallet = (WalletSummary) source.getSelectedItem();

    if (selectedWallet != null) {
      getModel().get().setValue(selectedWallet);

      descriptionTextArea.setText(selectedWallet.getNotes());
    }
  }

  public void setEnabled(boolean enabled) {
    this.selectedWalletComboBox.setEnabled(enabled);
  }

}


