package org.multibit.hd.testing;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.hardware.core.events.HardwareWalletEventType;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.hardware.core.messages.Features;

import java.util.concurrent.TimeUnit;

/**
 * <p>Test hardware wallet event fixtures to provide the following to FEST tests:</p>
 * <ul>
 * <li>Various scripts to match use cases involving hardware wallets</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HardwareWalletEventFixtures {

  private static final ListeningScheduledExecutorService eventScheduler = SafeExecutors.newSingleThreadScheduledExecutor("fest-events");

  /**
   *
   * @param features The features describing the device
   */
  public static void newAttachUseCase(final Features features) {

    eventScheduler.schedule(
      new Runnable() {
        @Override
        public void run() {

          HardwareWalletEvents.fireHardwareWalletEvent(
            HardwareWalletEventType.SHOW_DEVICE_READY,
            features
          );
        }
      },100, TimeUnit.MILLISECONDS);

  }

}
