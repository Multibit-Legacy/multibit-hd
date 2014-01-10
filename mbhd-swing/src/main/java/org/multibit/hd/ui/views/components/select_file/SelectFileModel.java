package org.multibit.hd.ui.views.components.select_file;

import com.google.common.base.Strings;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.wizards.WizardButton;

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

    // Ensure the "next" button is kept disabled
    ViewEvents.fireWizardEnableButton(panelName, WizardButton.NEXT, !Strings.isNullOrEmpty(selectedFile));

  }
}
