package org.multibit.hd.ui.views.wizards.shape_shift;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.request_bitcoin.RequestBitcoinEnterDetailsPanelView;
import org.multibit.hd.ui.views.wizards.request_bitcoin.RequestBitcoinState;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "request bitcoin":</p>
 * <ol>
 * <li>Enter details</li>
 * <li>Present QR code popover</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class RequestBitcoinWizard extends AbstractWizard<RequestBitcoinWizardModel> {

  public RequestBitcoinWizard(RequestBitcoinWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    //wizardViewMap.put(RequestBitcoinState.REQUEST_ENTER_DETAILS.name(), new RequestBitcoinEnterDetailsPanelView(this, RequestBitcoinState.REQUEST_ENTER_DETAILS.name()));

  }

}
