package org.multibit.hd.ui.views.components.select_file;

import com.google.common.base.Optional;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

import javax.swing.*;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show/hide the seed phrase (initially hidden)</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SelectFileModel implements Model<String> {

  private String selectedFile = "";

  /**
   * A file was selected (= true) or no file was selected/ cancel (= false)
   */
  private boolean selected = false;

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public SelectFileModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public String getValue() {
    return selectedFile;
  }

  @Override
  public void setValue(String value) {
    this.selectedFile = value;

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // Ensure the "next" button is kept disabled
        ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
      }
    });

  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }


}
