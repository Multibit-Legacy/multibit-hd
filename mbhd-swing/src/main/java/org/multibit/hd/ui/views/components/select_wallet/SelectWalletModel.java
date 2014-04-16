package org.multibit.hd.ui.views.components.select_wallet;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Select the wallet to use</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SelectWalletModel implements Model<WalletData> {

  private WalletData selectedBackup;
  private List<WalletData> walletList = Lists.newArrayList();

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public SelectWalletModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public WalletData getValue() {
    return selectedBackup;
  }

  @Override
  public void setValue(WalletData value) {
    this.selectedBackup = value;

    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
  }

  /**
   * @return The wallet data summaries to be presented
   */
  public List<WalletData> getWalletList() {
    return walletList;
  }

  public void setWalletList(List<WalletData> walletList) {
    this.walletList = walletList;

    // Initialise the selected value to the first walletData
    if (walletList != null && !walletList.isEmpty()) {
      selectedBackup = walletList.get(0);
    }
  }
}
