package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.credentials.*;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "use Trezor" wizard:</p>
 * <ol>
 * <li>Enter PIN</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class UseTrezorWizard extends AbstractWizard<UseTrezorWizardModel> {

  public UseTrezorWizard(UseTrezorWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
            UseTrezorState.ENTER_PIN.name(),
            new UseTrezorEnterPinPanelView(this, UseTrezorState.ENTER_PIN.name()));

    // TODO - no Trezor PIN panel

    // TODO - ask user to confirm 'Encrypt MultiBit HD unlock text'
  }

}
