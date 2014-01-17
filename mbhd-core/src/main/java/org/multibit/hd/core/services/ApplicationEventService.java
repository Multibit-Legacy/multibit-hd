package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;

/**
 * <p>Utility to provide the following to application:</p>
 * <ul>
 * <li>Tracking selected application events</li>
 * </ul>
 * <p>Having this service allows the UI to catch up with previous events</p>
 *
 * @since 0.0.1
 *  
 */
public class ApplicationEventService {

  private static Optional<ExchangeRateChangedEvent> latestExchangeRateChangedEvent = Optional.absent();

  /**
   * Reduced visibility constructor to prevent accidental instance creation outside of CoreServices
   */
  ApplicationEventService() {
  }

  /**
   * @return The latest "exchange rate changed" event
   */
  public static Optional<ExchangeRateChangedEvent> getLatestExchangeRateChangedEvent() {
    return latestExchangeRateChangedEvent;
  }

  /**
   * @param event The "exchange rate changed" event
   */
  @Subscribe
  public void onExchangeRateChangedEvent(ExchangeRateChangedEvent event) {
    latestExchangeRateChangedEvent = Optional.of(event);
  }

}
