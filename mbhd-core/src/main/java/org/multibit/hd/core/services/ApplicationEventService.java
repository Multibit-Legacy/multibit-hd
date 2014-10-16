package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.events.*;

/**
 * <p>Service to provide the following to application:</p>
 * <ul>
 * <li>Tracking selected application events</li>
 * </ul>
 * <p>Having this service allows the UI to catch up with previous events after a locale change or slow startup</p>
 *
 * @since 0.0.1
 *
 */
public class ApplicationEventService {

  private Optional<ExchangeRateChangedEvent> latestExchangeRateChangedEvent = Optional.absent();
  private Optional<SecurityEvent> latestSecurityEvent = Optional.absent();
  private Optional<BitcoinNetworkChangedEvent> latestBitcoinNetworkChangedEvent = Optional.absent();

  /**
   * Reduced visibility constructor to prevent accidental instance creation outside of CoreServices
   */
  ApplicationEventService() {
    CoreServices.uiEventBus.register(this);
  }

  /**
   * @return The latest "exchange rate changed" event
   */
  public Optional<ExchangeRateChangedEvent> getLatestExchangeRateChangedEvent() {
    return latestExchangeRateChangedEvent;
  }

  /**
   * @return The latest "security" event
   */
  public Optional<SecurityEvent> getLatestSecurityEvent() {
    return latestSecurityEvent;
  }

  /**
   * @return The latest "Bitcoin network changed" event
   */
  public Optional<BitcoinNetworkChangedEvent> getLatestBitcoinNetworkChangedEvent() {
    return latestBitcoinNetworkChangedEvent;
  }

  /**
   * <p>Repeats the latest events since the UI has become out of synch due to a restart of some kind</p>
   */
  public void repeatLatestEvents() {

    if (latestBitcoinNetworkChangedEvent.isPresent()) {
      CoreEvents.fireBitcoinNetworkChangedEvent(latestBitcoinNetworkChangedEvent.get().getSummary());
    }

    if (latestExchangeRateChangedEvent.isPresent()) {
      CoreEvents.fireExchangeRateChangedEvent(
        latestExchangeRateChangedEvent.get().getRate(),
        latestExchangeRateChangedEvent.get().getCurrency(),
        latestExchangeRateChangedEvent.get().getRateProvider(),
        latestExchangeRateChangedEvent.get().getExpires()
      );
    }

  }

  /**
   * @param event The "shutdown" event
   */
  @Subscribe
  public void onShutdownEvent(ShutdownEvent event) {

    // Clear the non-essential events
    if (ShutdownEvent.ShutdownType.SOFT.equals(event.getShutdownType())) {
      latestBitcoinNetworkChangedEvent = Optional.absent();
      latestExchangeRateChangedEvent = Optional.absent();
    }

  }

  /**
   * @param event The "exchange rate changed" event
   */
  @Subscribe
  public void onExchangeRateChangedEvent(ExchangeRateChangedEvent event) {
    latestExchangeRateChangedEvent = Optional.of(event);
  }

  /**
   * @param event The "security" event
   */
  @Subscribe
  public void onSecurityEvent(SecurityEvent event) {
    latestSecurityEvent = Optional.of(event);
  }

  /**
   * @param event The "Bitcoin network changed" event
   */
  @Subscribe
  public void onBitcoinNetworkChangedEvent(BitcoinNetworkChangedEvent event) {
    latestBitcoinNetworkChangedEvent = Optional.of(event);
  }

}
