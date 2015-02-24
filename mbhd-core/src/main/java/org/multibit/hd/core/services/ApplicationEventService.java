package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.events.*;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;

/**
 * <p>Service to provide the following to application:</p>
 * <ul>
 * <li>Tracking selected application events</li>
 * </ul>
 * <p>Having this service allows the UI to catch up with previous events after a locale change or slow startup</p>
 *
 * @since 0.0.1
 */
public class ApplicationEventService extends AbstractService {

  private Optional<ExchangeRateChangedEvent> latestExchangeRateChangedEvent = Optional.absent();
  private Optional<SecurityEvent> latestSecurityEvent = Optional.absent();
  private Optional<BitcoinNetworkChangedEvent> latestBitcoinNetworkChangedEvent = Optional.absent();
  private Optional<HardwareWalletEvent> latestHardwareWalletEvent = Optional.absent();

  private boolean isRegistered = false;

  @Override
  protected boolean startInternal() {

    HardwareWalletEvents.subscribe(this);
    isRegistered = true;

    return false;
  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    switch (shutdownType) {

      case HARD:
      case SOFT:
        if (isRegistered) {
          // Unsubscribe from hardware wallet events
          HardwareWalletEvents.unsubscribe(this);
          isRegistered=false;
        }

        // Allow ongoing cleanup
        return true;
      case SWITCH:
        // Clear all the events to prevent inaccurate UI
        latestBitcoinNetworkChangedEvent = Optional.absent();
        latestExchangeRateChangedEvent = Optional.absent();
        latestHardwareWalletEvent = Optional.absent();
        latestSecurityEvent = Optional.absent();

        // Avoid ongoing cleanup
        return false;
      default:
        throw new IllegalStateException("Unsupported state: " + shutdownType.name());
    }

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
   * @return The latest "hardware wallet" event
   */
  public Optional<HardwareWalletEvent> getLatestHardwareWalletEvent() {
    return latestHardwareWalletEvent;
  }

  /**
   * <p>Repeats the latest events since the UI has become out of synch due to a restart of some kind</p>
   */
  public void repeatLatestEvents() {

    // Don't replay security events - it gives a false positive

    // Don't replay hardware events - it gives a false positive and race conditions

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
    latestSecurityEvent = Optional.fromNullable(event);
  }

  /**
   * @param event The "Bitcoin network changed" event (excluding peer count notifications)
   */
  @Subscribe
  public void onBitcoinNetworkChangedEvent(BitcoinNetworkChangedEvent event) {
    // Do not remember peer count notifications (RAGStatus of empty)
    if (!RAGStatus.EMPTY.equals(event.getSummary().getSeverity())) {
      latestBitcoinNetworkChangedEvent = Optional.of(event);
    }
  }

  /**
   * @param event The "hardware wallet" event
   */
  @Subscribe
  public void onHardwareWalletEvent(HardwareWalletEvent event) {
    latestHardwareWalletEvent = Optional.of(event);
  }

}
