package org.multibit.hd.ui.views.wizards.sign_message;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "sign message":</p>
 * <ol>
 * <li>Enter details</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class SignMessageWizard extends AbstractWizard<SignMessageWizardModel> {

  public SignMessageWizard(SignMessageWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(SignMessageState.EDIT_MESSAGE.name(), new SignMessagePanelView(this, SignMessageState.EDIT_MESSAGE.name()));

  }

}
