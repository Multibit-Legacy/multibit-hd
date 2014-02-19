package org.multibit.hd.ui.views.components.enter_search;

import com.google.common.base.Optional;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Store the search query</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterSearchModel implements Model<String> {

  private String selectedFile = "";

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public EnterSearchModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public String getValue() {
    return selectedFile;
  }

  @Override
  public void setValue(String value) {
    this.selectedFile = value;

    // Ensure the "next" button is kept disabled
    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));

  }

  public String getPanelName() {
    return panelName;
  }
}
