package org.multibit.hd.ui.views.wizards.trezor_tools;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "Trezor tools" wizard:</p>
 * <ol>
 * <li>Select action to perform</li>
 * <li>Verify device</li>
 * <li>Wipe device</li>
 * <li>Report results to user</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class TrezorToolsWizard extends AbstractWizard<TrezorToolsWizardModel> {

  public TrezorToolsWizard(TrezorToolsWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(TrezorToolsState.SELECT_TREZOR_ACTION.name(), new TrezorToolsSelectPanelView(this, TrezorToolsState.SELECT_TREZOR_ACTION.name()));
    wizardViewMap.put(TrezorToolsState.VERIFY_DEVICE.name(), new TrezorToolsVerifyDevicePanelView(this, TrezorToolsState.VERIFY_DEVICE.name()));
    wizardViewMap.put(TrezorToolsState.WIPE_DEVICE.name(), new TrezorToolsWipeDevicePanelView(this, TrezorToolsState.WIPE_DEVICE.name()));
    wizardViewMap.put(TrezorToolsState.TREZOR_ACTION_REPORT.name(), new TrezorToolsReportPanelView(this, TrezorToolsState.WIPE_DEVICE.name()));

  }

}
