package org.multibit.hd.ui.views.screens.history;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.HistoryEntry;
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

  private final HistoryService historyService;

  public HistoryScreenModel(HistoryService historyService, Screen screen) {
    super(screen);

    Preconditions.checkNotNull(historyService, "'historyService' must be present");

    this.historyService = historyService;

  }

  public List<HistoryEntry> getHistory() {
    return historyService.allHistory();
  }

  public List<HistoryEntry> filterHistoryByContent(String query) {
    return historyService.filterHistoryByContent(query);
  }

  /**
   * <p>Provide access to the history service for the "edit history" wizard</p>
   *
   * @return The history service
   */
  public HistoryService getHistoryService() {
    return historyService;
  }

}