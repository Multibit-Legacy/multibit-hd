package org.multibit.hd.ui.views.components.select_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;

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

  // View components
  private JComboBox<WalletSummary> selectedWalletComboBox;
  private JLabel descriptionLabel;

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
      "[][][]" // Rows
    ));

    // Provide the initial list
    selectedWalletComboBox = ComboBoxes.newSelectWalletComboBox(this, model.getWalletList());
    selectedWalletComboBox.setEnabled(false);

    // Create the labels
    descriptionLabel = Labels.newBlankLabel();

    // Ensure it is accessible
    AccessibilityDecorator.apply(descriptionLabel, MessageKey.DESCRIPTION);

    // Add to the panel
    panel.add(selectedWalletComboBox, "grow,push,w min:501:,wrap");
    panel.add(descriptionLabel, "grow,push,wrap");

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
    Optional<WalletSummary> currentWallet = WalletManager.INSTANCE.getCurrentWalletSummary();

    if (currentWallet.isPresent()) {

      // We have a current select so set that first then add more
      WalletSummary current = currentWallet.get();
      selectedWalletComboBox.addItem(currentWallet.get());
      for (WalletSummary walletSummary : walletList) {
        // Continue adding entries other than the current
        if (!walletSummary.getWalletId().equals(current.getWalletId())) {
          selectedWalletComboBox.addItem(walletSummary);
        }
      }
    } else {

      // We have no current selection so add anything that's available
      for (WalletSummary walletSummary : walletList) {
        selectedWalletComboBox.addItem(walletSummary);
      }
    }
    selectedWalletComboBox.addActionListener(this);

    // Update the description if there is a selection
    if (selectedWalletComboBox.getSelectedIndex() != -1) {

      WalletSummary selectedWallet = (WalletSummary) selectedWalletComboBox.getSelectedItem();

      if (selectedWallet != null) {

        getModel().get().setValue(selectedWallet);
        descriptionLabel.setText(selectedWallet.getNotes());

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

      descriptionLabel.setText(selectedWallet.getNotes());

    }
  }

  public void setEnabled(boolean enabled) {
    this.selectedWalletComboBox.setEnabled(enabled);
  }

}


