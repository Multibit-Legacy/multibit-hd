package org.multibit.hd.ui.views.screens.history;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.HistoryService;
import org.multibit.hd.ui.views.screens.AbstractScreenModel;
import org.multibit.hd.ui.views.screens.Screen;

import java.util.List;


/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the history screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HistoryScreenModel extends AbstractScreenModel {

  // TODO Have this injected through a WalletServices.getOrCreateHistory() method
  private Optional<HistoryService> historyService = Optional.absent();

  public HistoryScreenModel(Screen screen) {
    super(screen);

  }


  public List<HistoryEntry> getHistory() {

    // TODO This construct is to disappear
    if (!historyService.isPresent()) {
      initialiseHistory();
    }

    return historyService.get().allHistory();
  }

  public List<HistoryEntry> filterHistoryByContent(String query) {

    // TODO This construct is to disappear
    if (!historyService.isPresent()) {
      initialiseHistory();
    }

    return historyService.get().filterHistoryByContent(query);
  }

  /**
   * <p>Provide access to the history service for the "edit history" wizard</p>
   *
   * @return The history service
   */
  public HistoryService getHistoryService() {
    return historyService.get();
  }

  /**
   * <p>Defer the initialisation of the history service until a wallet ID is available</p>
   */
  private void initialiseHistory() {

    if (!WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {
      throw new IllegalStateException("History should not be accessible without a wallet ID");
    }

    this.historyService = Optional.of(CoreServices.getOrCreateHistoryService(WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId()));
  }

}