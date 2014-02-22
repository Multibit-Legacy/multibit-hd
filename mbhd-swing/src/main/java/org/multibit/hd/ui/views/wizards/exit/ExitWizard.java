package org.multibit.hd.ui.views.wizards.exit;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "Exit":</p>
 * <ol>
 * <li>Confirm choice</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class ExitWizard extends AbstractWizard<ExitWizardModel> {

  public ExitWizard(ExitWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(ExitState.CONFIRM_EXIT.name(), new ExitPanelView(this, ExitState.CONFIRM_EXIT.name()));

  }

}
