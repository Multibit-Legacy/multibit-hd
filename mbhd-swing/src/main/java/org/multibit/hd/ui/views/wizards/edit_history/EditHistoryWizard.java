package org.multibit.hd.ui.views.wizards.edit_history;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "edit contact" wizard:</p>
 * <ol>
 * <li>Enter details</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class EditHistoryWizard extends AbstractWizard<EditHistoryWizardModel> {

  public EditHistoryWizard(EditHistoryWizardModel model, EnterHistoryDetailsMode mode) {
    super(model, false, Optional.of(mode));
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      EditHistoryState.HISTORY_ENTER_DETAILS.name(),
      new EditHistoryEnterDetailsPanelView(
        this,
        EditHistoryState.HISTORY_ENTER_DETAILS.name(),
        (EnterHistoryDetailsMode) wizardParameter.get()
      )
    );

  }

}
