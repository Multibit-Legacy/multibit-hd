package org.multibit.hd.ui.controllers;

import com.google.bitcoin.core.Coin;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Coins;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.AddAlertEvent;
import org.multibit.hd.ui.events.controller.RemoveAlertEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.AlertModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Controller for the header view </p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 */
public class HeaderController {

  private static final Logger log = LoggerFactory.getLogger(HeaderController.class);

  private final List<AlertModel> alertModels = Lists.newArrayList();

  public HeaderController() {

    CoreServices.uiEventBus.register(this);

  }

  /**
   * <p>Trigger a refresh of the header view to ensure alert panels are shown</p>
   */
  public void refresh() {

    if (!alertModels.isEmpty()) {

      // The alert structure has changed so inform the view
      ViewEvents.fireAlertAddedEvent(alertModels.get(0));

    }

  }

  /**
   * <p>Called when the exchange rate changes</p>
   *
   * @param event The exchange rate change event
   */
  @Subscribe
  public void onExchangeRateChangedEvent(ExchangeRateChangedEvent event) {

    // Build the exchange string
    Coin coin;

    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    if (currentWalletSummary.isPresent()) {
      // Use the real wallet data
      coin = currentWalletSummary.get().getWallet().getBalance();
    } else {
      // Unknown at this time
      coin = Coin.ZERO;
    }
    BigDecimal localBalance;

    if (event.getRate() != null) {
      localBalance = Coins.toLocalAmount(coin, event.getRate());
    } else {
      localBalance = null;
    }
    // Post the event
    ViewEvents.fireBalanceChangedEvent(
      coin,
      localBalance,
      event.getRateProvider()
    );

  }

  /**
   * <p>Called when there are payments seen that may change the balance</p>
   *
   * @param event The slow transaction seen event
   */
  @Subscribe
  public void onSlowTransactionSeenEvent(SlowTransactionSeenEvent event) {

    Optional<ExchangeRateChangedEvent> exchangeRateChangedEventOptional = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();

    if (exchangeRateChangedEventOptional.isPresent()) {
      onExchangeRateChangedEvent(exchangeRateChangedEventOptional.get());
    } else {
      // No exchange rate available but fire an event anyhow to force a balance change event
      onExchangeRateChangedEvent(new ExchangeRateChangedEvent(null, null, Optional.<String>absent(), null));
    }
  }

  /**
   * <p>Handles the presentation of a new alert</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public synchronized void onAddAlertEvent(AddAlertEvent event) {

    // Add this to the list
    alertModels.add(event.getAlertModel());

    // Play a beep on the first alert
    switch (event.getAlertModel().getSeverity()) {
      case RED:
      case AMBER:
        Sounds.playBeep();
        break;
    }

    // Adjust the models to reflect the new M of N values
    updateRemaining();

    // The alert structure has changed so inform the view
    ViewEvents.fireAlertAddedEvent(alertModels.get(0));

  }

  /**
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public synchronized void onRemoveAlertEvent(RemoveAlertEvent event) {

    // Remove the topmost alert model
    alertModels.remove(0);

    if (!alertModels.isEmpty()) {

      // Adjust the models to reflect the new M of N values
      updateRemaining();

      // The alert structure has changed so inform the view
      ViewEvents.fireAlertAddedEvent(alertModels.get(0));

    } else {

      // Use an empty event to signal that the event should be hidden
      ViewEvents.fireAlertRemovedEvent();
    }

  }

  /**
   * <p>Updates the "remaining" values for alert models</p>
   */
  private void updateRemaining() {

    Preconditions.checkNotNull(alertModels, "'alertModels' must be present");

    // Update the "remaining" based on the position in the list
    for (int i = 0; i < alertModels.size(); i++) {
      AlertModel alertModel = alertModels.get(i);

      Preconditions.checkNotNull(alertModel, "'alertModel' must be present");

      alertModel.setRemaining(alertModels.size() - 1);
    }

  }
}
