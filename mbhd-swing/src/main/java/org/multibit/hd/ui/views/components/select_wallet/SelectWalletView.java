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
  private JLabel createdLabel;
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
      "[]", // Columns
      "[][][]" // Rows
    ));

    // Provide the initial list
    selectedWalletComboBox = ComboBoxes.newSelectWalletComboBox(this, model.getWalletList());

    // Create the labels
    createdLabel = Labels.newBlankLabel();
    descriptionLabel = Labels.newBlankLabel();

    // Add to the panel
    panel.add(selectedWalletComboBox, "grow,push,w min:350:,wrap");
    panel.add(createdLabel, "grow,push,wrap");
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

    List<WalletData> walletList = getModel().get().getWalletList();

    // TODO the sort order should be defined better or a comparator used
    if (walletList != null) {
      for (int i = walletList.size() - 1; i >= 0; i--) {
        selectedWalletComboBox.addItem(walletList.get(i));
      }
    }
    selectedWalletComboBox.addActionListener(this);

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
    }
  }

  public void setEnabled(boolean enabled) {
    this.selectedWalletComboBox.setEnabled(enabled);
  }

}


