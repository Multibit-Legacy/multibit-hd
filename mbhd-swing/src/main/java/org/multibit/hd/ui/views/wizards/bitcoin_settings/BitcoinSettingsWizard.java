package org.multibit.hd.ui.views.wizards.bitcoin_settings;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "Bitcoin settings" wizard:</p>
 * <ol>
 * <li>Enter settings</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class BitcoinSettingsWizard extends AbstractWizard<BitcoinSettingsWizardModel> {

  public BitcoinSettingsWizard(BitcoinSettingsWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    // Use the wizard parameter to retrieve the appropriate mode
    wizardViewMap.put(
      BitcoinSettingsState.BITCOIN_ENTER_DETAILS.name(),
      new BitcoinSettingsPanelView(this, BitcoinSettingsState.BITCOIN_ENTER_DETAILS.name())
    );

  }

}
