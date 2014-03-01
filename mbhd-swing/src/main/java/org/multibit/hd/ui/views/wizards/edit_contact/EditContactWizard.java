package org.multibit.hd.ui.views.wizards.edit_contact;

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
public class EditContactWizard extends AbstractWizard<EditContactWizardModel> {

  public EditContactWizard(EditContactWizardModel model, EnterContactDetailsMode mode) {
    super(model, false, Optional.of(mode));
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      EditContactState.EDIT_CONTACT_ENTER_DETAILS.name(),
      new EditContactEnterDetailsPanelView(
        this,
        EditContactState.EDIT_CONTACT_ENTER_DETAILS.name(),
        (EnterContactDetailsMode) wizardParameter.get()
      )
    );

  }

}
