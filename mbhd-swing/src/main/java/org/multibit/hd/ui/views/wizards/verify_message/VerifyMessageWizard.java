package org.multibit.hd.ui.views.wizards.verify_message;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "verify message":</p>
 * <ol>
 * <li>Enter details</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class VerifyMessageWizard extends AbstractWizard<VerifyMessageWizardModel> {

  public VerifyMessageWizard(VerifyMessageWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(VerifyMessageState.EDIT_MESSAGE.name(), new VerifyMessagePanelView(this, VerifyMessageState.EDIT_MESSAGE.name()));

  }

}
