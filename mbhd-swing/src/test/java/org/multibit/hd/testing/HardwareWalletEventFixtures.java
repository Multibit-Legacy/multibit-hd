package org.multibit.hd.testing;

import com.google.common.collect.Lists;
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
   * The standard label for a hardware wallet
   */
  public static final String STANDARD_LABEL = "Example";

  /**
   * <p>Perform a sequence of events corresponding to a device attach</p>
   */
  public static void newAttachUseCase() {

    eventScheduler.schedule(
      new Runnable() {
        @Override
        public void run() {

          HardwareWalletEvents.fireHardwareWalletEvent(
            HardwareWalletEventType.SHOW_DEVICE_READY,
            newStandardFeatures()
          );
        }
      }, 100, TimeUnit.MILLISECONDS);

  }

  /**
   * <p>Perform a sequence of events corresponding to initialising a Trezor</p>
   */
  public static void newInitialiseTrezorUseCase() {

    eventScheduler.schedule(
      new Runnable() {
        @Override
        public void run() {

          HardwareWalletEvents.fireHardwareWalletEvent(
            HardwareWalletEventType.SHOW_DEVICE_READY,
            newStandardFeatures()
          );
        }
      }, 100, TimeUnit.MILLISECONDS);

  }

  /**
   * @return A default Features for use with FEST testing
   */
  public static Features newStandardFeatures() {

    Features features = new Features();
    features.setVendor("bitcointrezor.com");
    features.setVersion("1.2.1");
    features.setBootloaderMode(false);
    features.setDeviceId("5DE10270051613895EEB68ED");
    features.setPinProtection(true);
    features.setPassphraseProtection(false);
    features.setLanguage("english");
    features.setLabel(STANDARD_LABEL);
    features.setCoins(Lists.newArrayList("Bitcoin", "Testnet", "Namecoin", "Litecoin"));
    features.setInitialized(true);
    features.setRevision(new byte[]{0x52, 0x4f, 0x2a, (byte) 0x95, 0x7a, (byte) 0xfb, 0x66, (byte) 0xe6, (byte) 0xa8, 0x69, 0x38, 0x4a, (byte) 0xce, (byte) 0xac, (byte) 0xa1, (byte) 0xcb, 0x7f, (byte) 0x9c, (byte) 0xba, 0x60});
    features.setBootloaderHash(new byte[]{(byte) 0xa0, 0x3e, 0x7e, (byte) 0x8e, (byte) 0x9d, (byte) 0xa0, (byte) 0xb9, 0x2d, 0x3d, 0x4b, 0x39, (byte) 0xef, (byte) 0xff, 0x38, 0x67, 0x35, 0x12, (byte) 0xec, (byte) 0xec, (byte) 0xc5, 0x6d, (byte) 0xb6, 0x02, 0x30, 0x30, 0x22, (byte) 0xe5, (byte) 0xe8, 0x7b, (byte) 0xe1, 0x22, 0x61});
    features.setImported(false);

    return features;

  }

}
