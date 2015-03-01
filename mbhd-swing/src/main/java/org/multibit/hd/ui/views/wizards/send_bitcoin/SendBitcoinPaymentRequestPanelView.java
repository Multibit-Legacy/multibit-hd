package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_payment_request.DisplayPaymentRequestModel;
import org.multibit.hd.ui.views.components.display_payment_request.DisplayPaymentRequestView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Display payment request</li>
 * </ul>
 *
 * @since 0.0.8
 *
 */

public class SendBitcoinPaymentRequestPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinPaymentRequestPanelModel> {

  // Panel specific components
  private ModelAndView<DisplayPaymentRequestModel, DisplayPaymentRequestView> displayPaymentRequestMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public SendBitcoinPaymentRequestPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SEND_BITCOIN_TITLE, AwesomeIcon.CLOUD_UPLOAD);

  }

  @Override
  public void newPanelModel() {

    displayPaymentRequestMaV = Components.newDisplayPaymentRequestMaV(getPanelName());

    // Configure the panel model
    final SendBitcoinPaymentRequestPanelModel panelModel = new SendBitcoinPaymentRequestPanelModel(
      getPanelName(),
      displayPaymentRequestMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setPaymentRequestPanelModel(panelModel);

    // Register components
    registerComponents(displayPaymentRequestMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    // Apply any payment session summary parameters
    if (getWizardModel().getPaymentSessionSummary().isPresent()) {

      getWizardModel().handlePaymentSessionSummary();

      Recipient recipient = getWizardModel().getRecipient();
      Coin amount = getWizardModel().getCoinAmount();

      displayPaymentRequestMaV.getModel().setValue(recipient);

    }

    contentPanel.add(displayPaymentRequestMaV.getView().newComponentPanel(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Next button starts off disabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        displayPaymentRequestMaV.getView().requestInitialFocus();

      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the panel model it already has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      isNextEnabled()
    );

  }

  /**
   * @return True if the "next" button should be enabled
   */
  private boolean isNextEnabled() {

    boolean bitcoinAmountOK = true;
//    boolean bitcoinAmountOK = !getPanelModel().get()
//      .getEnterAmountModel()
//      .getCoinAmount()
//      .equals(Coin.ZERO);

    boolean recipientOK = getPanelModel().get()
      .getDisplayPaymentRequestModel()
      .getRecipient()
      .isPresent();

    return bitcoinAmountOK && recipientOK;
  }
}

