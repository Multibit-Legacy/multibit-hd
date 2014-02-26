package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.events.SecurityEvent;

/**
 * <p>Service to provide the following to application:</p>
 * <ul>
 * <li>Tracking selected application events</li>
 * </ul>
 * <p>Having this service allows the UI to catch up with previous events</p>
 *
 * @since 0.0.1
 *  
 */
public class ApplicationEventService {

  private Optional<ExchangeRateChangedEvent> latestExchangeRateChangedEvent = Optional.absent();
  private Optional<SecurityEvent> latestSecurityEvent = Optional.absent();

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

}
