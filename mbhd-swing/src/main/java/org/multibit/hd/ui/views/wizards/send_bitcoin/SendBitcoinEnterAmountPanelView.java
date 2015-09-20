package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 */

public class SendBitcoinEnterAmountPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinEnterAmountPanelModel> {

  // Panel specific components
  private ModelAndView<EnterRecipientModel, EnterRecipientView> enterRecipientMaV;
  private ModelAndView<EnterAmountModel, EnterAmountView> enterAmountMaV;

  private boolean networkOk = false;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public SendBitcoinEnterAmountPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {
    super(wizard, panelName, AwesomeIcon.CLOUD_UPLOAD, MessageKey.SEND_BITCOIN_TITLE, null);
  }

  @Override
  public void newPanelModel() {
    enterRecipientMaV = Components.newEnterRecipientMaV(getPanelName());
    enterAmountMaV = Components.newEnterAmountMaV(getPanelName());

    // Configure the panel model
    final SendBitcoinEnterAmountPanelModel panelModel = new SendBitcoinEnterAmountPanelModel(
      getPanelName(),
      enterRecipientMaV.getModel(),
      enterAmountMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterAmountPanelModel(panelModel);

    // Register components
    registerComponents(enterAmountMaV, enterRecipientMaV);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]10[]" // Row constraints
      ));

    // Apply any Bitcoin URI parameters
    if (getWizardModel().getBitcoinURI().isPresent()) {

      getWizardModel().handleBitcoinURI();

      Recipient recipient = getWizardModel().getRecipient();
      Coin amount = getWizardModel().getCoinAmount().or(Coin.ZERO);

      enterRecipientMaV.getModel().setValue(recipient);
      enterAmountMaV.getModel().setCoinAmount(amount);

    }

    contentPanel.add(enterRecipientMaV.getView().newComponentPanel(), "wrap");
    contentPanel.add(enterAmountMaV.getView().newComponentPanel(), "wrap");

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
  public boolean beforeShow() {
    // Ensure the pay request button is kept up to date
    Optional<BitcoinNetworkChangedEvent> changedEvent = CoreServices.getApplicationEventService().getLatestBitcoinNetworkChangedEvent();
    if (changedEvent.isPresent()) {
      updateNextButton(changedEvent.get());
    }
    return true;
  }

  @Override
  public void afterShow() {
    enterRecipientMaV.getView().requestInitialFocus();
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
    Optional<Coin> coinAmount = getPanelModel().get()
            .getEnterAmountModel()
            .getCoinAmount();
    boolean bitcoinAmountOK = coinAmount.isPresent() && !coinAmount.get().equals(Coin.ZERO);

    boolean recipientOK = getPanelModel().get()
      .getEnterRecipientModel()
      .getRecipient()
      .isPresent();

    return bitcoinAmountOK && recipientOK && networkOk;
  }

  /**
   * @param event The "Bitcoin network changed" event - one per block downloaded during synchronization
   */
  @Subscribe
  public void onBitcoinNetworkChangeEvent(final BitcoinNetworkChangedEvent event) {
    if (!isInitialised()) {
      return;
    }

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    BitcoinNetworkSummary summary = event.getSummary();

    Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");

    // Keep the UI response to a minimum due to the volume of these events
    updateNextButton(event);
  }

  private void updateNextButton(BitcoinNetworkChangedEvent event) {
    boolean newEnabled;
    boolean canChange = true;

    // Cannot pay a request until synced as you don't know how much is in the wallet
    switch (event.getSummary().getSeverity()) {
      case RED:
      case AMBER:
        // Enable on RED or AMBER only if unrestricted (allows FEST tests without a network)
        newEnabled = InstallationManager.unrestricted;
        networkOk = newEnabled;
        break;
      case GREEN:
        // Always enable on GREEN if data is valid
        newEnabled = isNextEnabled();
        networkOk = true;
        break;
      case PINK:
      case EMPTY:
        // Maintain the status quo
        newEnabled = getNextButton().isEnabled();
        canChange = false;
        break;
      default:
        // Unknown status
        throw new IllegalStateException("Unknown event severity " + event.getSummary().getSeverity());
    }

    if (canChange) {
      final boolean finalNewEnabled = newEnabled;

      // If button is not enabled and the newEnabled is false don't do anything
      // This cuts down the number of events
      if (getNextButton().isEnabled() || newEnabled) {
        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {
              ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, finalNewEnabled);
            }
          });
      }
    }
  }
}

