package org.multibit.hd.testing;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.events.HardwareWalletEventType;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.hardware.core.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.Callable;

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

  private static final Logger log = LoggerFactory.getLogger(HardwareWalletEventFixtures.class);

  private static final ListeningScheduledExecutorService eventScheduler = SafeExecutors.newSingleThreadScheduledExecutor("fest-events");

  /**
   * The standard label for a hardware wallet
   */
  public static final String STANDARD_LABEL = "Example";

  private static Queue<HardwareWalletEvent> hardwareWalletEvents = Queues.newArrayBlockingQueue(100);

  /**
   * Control when the next event in the use case will be fired
   */
  public static void fireNextEvent() {

    Preconditions.checkState(!hardwareWalletEvents.isEmpty(), "Unexpected call to empty stack. The test should know when the last event has been fired.");

    final ListenableFuture<Boolean> future = eventScheduler.submit(
      new Callable<Boolean>() {
        @Override
        public Boolean call() {

          // Get the head of the queue
          HardwareWalletEvent event = hardwareWalletEvents.remove();

          if (event.getMessage().isPresent()) {
            HardwareWalletEvents.fireHardwareWalletEvent(event.getEventType(), event.getMessage().get());
          } else {
            HardwareWalletEvents.fireHardwareWalletEvent(event.getEventType());
          }

          return true;
        }
      });
    Futures.addCallback(
      future, new FutureCallback<Boolean>() {
        @Override
        public void onSuccess(Boolean result) {

          // Must have successfully fired the event to be here
        }

        @Override
        public void onFailure(Throwable t) {

          log.error("Fail to fire hardware wallet event", t);

        }
      });

  }

  /**
   * <p>Prepare a sequence of events corresponding to a device attach</p>
   */
  public static void prepareAttachUseCaseEvents() {

    hardwareWalletEvents.clear();

    HardwareWalletEvent event = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_DEVICE_READY,
      Optional.<HardwareWalletMessage>of(
        newStandardFeatures()
      ));

    hardwareWalletEvents.add(event);

  }

  /**
   * <p>Prepare a sequence of events corresponding to initialising a Trezor</p>
   */
  public static void prepareInitialiseTrezorUseCaseEvents() {

    hardwareWalletEvents.clear();

    final HardwareWalletEvent event1 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_DEVICE_READY,
      Optional.<HardwareWalletMessage>of(
        newStandardFeatures()
      ));

    hardwareWalletEvents.add(event1);

    final HardwareWalletEvent event2 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_BUTTON_PRESS,
      Optional.<HardwareWalletMessage>of(
        newConfirmWipeButtonRequest()
      ));

    hardwareWalletEvents.add(event2);

    final HardwareWalletEvent event3 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_OPERATION_SUCCEEDED,
      Optional.<HardwareWalletMessage>of(
        newDeviceWipedSuccess()
      ));

    hardwareWalletEvents.add(event3);

    final HardwareWalletEvent event4 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_PIN_ENTRY,
      Optional.<HardwareWalletMessage>of(
        newNewFirstPinMatrix()
      ));

    hardwareWalletEvents.add(event4);

    final HardwareWalletEvent event5 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_PIN_ENTRY,
      Optional.<HardwareWalletMessage>of(
        newNewSecondPinMatrix()
      ));

    hardwareWalletEvents.add(event5);

    // Next 12 words, confirm 12 words
    for (int i = 0; i < 23; i++) {
      final HardwareWalletEvent event = new HardwareWalletEvent(
        HardwareWalletEventType.PROVIDE_ENTROPY,
        Optional.<HardwareWalletMessage>of(
          newConfirmWordButtonRequest()
        ));

      hardwareWalletEvents.add(event);
    }

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

  /**
   * @return A new operation successful
   */
  private static Success newDeviceWipedSuccess() {

    return new Success(
      "Device wiped",
      new byte[]{}
    );

  }

  /**
   * @return A new confirm wipe button request
   */
  private static ButtonRequest newConfirmWipeButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.WIPE_DEVICE,
      ""
    );

  }

  /**
   * @return A new confirm word button request
   */
  private static ButtonRequest newConfirmWordButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.CONFIRM_WORD,
      ""
    );

  }

  /**
   * @return A new PIN matrix for "new first" (set)
   */
  private static PinMatrixRequest newNewFirstPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.NEW_FIRST);

  }

  /**
   * @return A new PIN matrix for "new second" (confirm)
   */
  private static PinMatrixRequest newNewSecondPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.NEW_SECOND);

  }

}
