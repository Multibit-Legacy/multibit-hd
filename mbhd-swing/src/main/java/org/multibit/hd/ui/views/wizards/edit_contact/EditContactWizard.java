package org.multibit.hd.ui.views.wizards.edit_contact;

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

  public EditContactWizard(EditContactWizardModel model) {
    super(model, false);
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    boolean multiEdit = getWizardModel().getContacts().size() > 1;

    wizardViewMap.put(
      EditContactState.ENTER_DETAILS.name(),
      new EditContactEnterDetailsPanelView(this, EditContactState.ENTER_DETAILS.name(), multiEdit)
    );

  }

}
