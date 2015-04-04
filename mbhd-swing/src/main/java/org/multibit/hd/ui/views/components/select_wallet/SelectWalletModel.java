package org.multibit.hd.ui.views.components.select_wallet;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.WalletSummary;
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
public class SelectWalletModel implements Model<WalletSummary> {

  private WalletSummary selectedWallet;
  private List<WalletSummary> walletList = Lists.newArrayList();

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public SelectWalletModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public WalletSummary getValue() {
    return selectedWallet;
  }

  @Override
  public void setValue(WalletSummary value) {
    this.selectedWallet = value;

    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
  }

  /**
   * @return The wallet data summaries to be presented
   */
  public List<WalletSummary> getWalletList() {
    return walletList;
  }

  public void setWalletList(List<WalletSummary> walletList, Optional<WalletSummary> currentWalletSummaryOptional) {

    this.walletList = walletList;

    // Initialise the selected value to the first wallet summary
    if (walletList != null && !walletList.isEmpty()) {
      // By default select the first wallet in the list
      selectedWallet = walletList.get(0);

      if (currentWalletSummaryOptional.isPresent()) {
        // Select the current wallet summary from the list
        for (WalletSummary loopWalletSummary : walletList) {
          if (loopWalletSummary.getWalletId().equals(currentWalletSummaryOptional.get().getWalletId())) {
            selectedWallet = loopWalletSummary;
            break;
          }
        }
      }
    }
  }
}
