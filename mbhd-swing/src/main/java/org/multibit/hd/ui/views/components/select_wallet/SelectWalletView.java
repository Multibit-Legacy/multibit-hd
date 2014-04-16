package org.multibit.hd.ui.views.components.select_wallet;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;

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
  private JComboBox<WalletData> selectedWalletComboBox;
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

    // Create the labels
    descriptionLabel = Labels.newBlankLabel();

    // Add to the panel
    panel.add(Labels.newSelectWallet(), "grow,push, w min:90");
    panel.add(selectedWalletComboBox, "grow,push,w min:465:,wrap");
    panel.add(descriptionLabel, "grow,push,span 2,wrap");

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

    List<WalletData> walletList = getModel().get().getWalletList();

    // TODO the sort order should be defined better or a comparator used
    if (walletList != null) {
      for (int i = walletList.size() - 1; i >= 0; i--) {
        selectedWalletComboBox.addItem(walletList.get(i));
      }
    }
    selectedWalletComboBox.addActionListener(this);

    // Update the description if there is a selection
    if (selectedWalletComboBox.getSelectedIndex() != -1) {

      WalletData selectedWallet = (WalletData) selectedWalletComboBox.getSelectedItem();

      if (selectedWallet != null) {

        getModel().get().setValue(selectedWallet);
        descriptionLabel.setText(selectedWallet.getDescription());

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
    WalletData selectedWallet = (WalletData) source.getSelectedItem();

    if (selectedWallet != null) {

      getModel().get().setValue(selectedWallet);

      descriptionLabel.setText(selectedWallet.getDescription());

    }
  }

  public void setEnabled(boolean enabled) {
    this.selectedWalletComboBox.setEnabled(enabled);
  }

}


