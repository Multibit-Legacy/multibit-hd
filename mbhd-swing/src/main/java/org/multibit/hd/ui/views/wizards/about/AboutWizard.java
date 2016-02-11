package org.multibit.hd.ui.views.wizards.about;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "About":</p>
 * <ol>
 * <li>Display details</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class AboutWizard extends AbstractWizard<AboutWizardModel> {

  public AboutWizard(AboutWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      AboutState.ABOUT_DETAILS.name(),
      new AboutPanelView(this, AboutState.ABOUT_DETAILS.name())
    );

  }

}
