package org.multibit.hd.testing;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.*;
import org.bitcoinj.core.Utils;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.events.HardwareWalletEventType;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.hardware.core.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Test hardware wallet event fixtures to provide the following to FEST tests:</p>
 * <ul>
 * <li>Various scripts to match use cases involving hardware wallets</li>
 * <li>Various standard objects to act as payloads for the events</li>
 * </ul>
 *
 * @since 0.0.5
 *  
 */
public class HardwareWalletEventFixtures {

  public static final Logger log = LoggerFactory.getLogger(HardwareWalletEventFixtures.class);

  public static final ListeningScheduledExecutorService eventScheduler = SafeExecutors.newSingleThreadScheduledExecutor("fest-events");

  /**
   * The standard label for a hardware wallet
   */
  public static final String STANDARD_LABEL = "Example";

  public static Queue<HardwareWalletEvent> hardwareWalletEvents = Queues.newArrayBlockingQueue(100);

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

    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

  }

  /**
   * <p>Prepare a sequence of events corresponding to unlocking an initialised Trezor</p>
   */
  public static void prepareUnlockTrezorUseCaseEvents() {

    hardwareWalletEvents.clear();

    // Attach device
    final HardwareWalletEvent event1 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_DEVICE_READY,
      Optional.<HardwareWalletMessage>of(
        newStandardFeatures()
      ));

    hardwareWalletEvents.add(event1);

    // Deterministic hierarchy (indirectly from mock client via PUBLIC_KEY messages)

    // PIN matrix request (from mock client)

    // Button request (cipher key confirm from client)

    // Cipher key success
    final HardwareWalletEvent event2 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_OPERATION_SUCCEEDED,
      Optional.<HardwareWalletMessage>of(
        newCipherKeySuccess()
      ));

    hardwareWalletEvents.add(event2);

  }

  /**
   * <p>Prepare a sequence of events corresponding to initialising a Trezor</p>
   */
  public static void prepareInitialiseTrezorUseCaseEvents() {

    hardwareWalletEvents.clear();

    // Attach device
    final HardwareWalletEvent event1 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_DEVICE_READY,
      Optional.<HardwareWalletMessage>of(
        newStandardFeatures()
      ));

    hardwareWalletEvents.add(event1);

    // Confirm wipe
    final HardwareWalletEvent event2 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_BUTTON_PRESS,
      Optional.<HardwareWalletMessage>of(
        newWipeDeviceButtonRequest()
      ));

    hardwareWalletEvents.add(event2);

    // Request PIN (first)
    final HardwareWalletEvent event3 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_PIN_ENTRY,
      Optional.<HardwareWalletMessage>of(
        newNewFirstPinMatrix()
      ));

    hardwareWalletEvents.add(event3);

    // Request PIN (second)
    final HardwareWalletEvent event4 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_PIN_ENTRY,
      Optional.<HardwareWalletMessage>of(
        newNewSecondPinMatrix()
      ));

    hardwareWalletEvents.add(event4);

    // Request entropy
    final HardwareWalletEvent event5 = new HardwareWalletEvent(
      HardwareWalletEventType.PROVIDE_ENTROPY,
      Optional.<HardwareWalletMessage>absent()
    );

    hardwareWalletEvents.add(event5);

    // Next 12 words, confirm 12 words
    for (int i = 0; i < 24; i++) {
      final HardwareWalletEvent event = new HardwareWalletEvent(
        HardwareWalletEventType.SHOW_BUTTON_PRESS,
        Optional.<HardwareWalletMessage>of(
          newConfirmWordButtonRequest()
        ));

      hardwareWalletEvents.add(event);
    }

    // Operation successful
    final HardwareWalletEvent event6 = new HardwareWalletEvent(
      HardwareWalletEventType.SHOW_OPERATION_SUCCEEDED,
      Optional.<HardwareWalletMessage>of(
        newCipherKeySuccess()
      ));

    hardwareWalletEvents.add(event6);

  }

  /**
   * @return A default Features for use with FEST testing (abandon wallet)
   */
  public static Features newStandardFeatures() {

    Features features = new Features();
    features.setVendor("bitcointrezor.com");
    features.setVersion("1.2.1");
    features.setBootloaderMode(false);
    features.setDeviceId("D18894FA25FA90CD589EDE57");
    features.setPinProtection(true);
    features.setPassphraseProtection(false);
    features.setLanguage("english");
    features.setLabel(STANDARD_LABEL);
    features.setCoins(Lists.newArrayList("Bitcoin", "Testnet", "Namecoin", "Litecoin"));
    features.setInitialized(true);
    features.setRevision(Utils.HEX.decode("524f2a957afb66e6a869384aceaca1cb7f9cba60"));
    features.setBootloaderHash(Utils.HEX.decode("c4c32539b4a025a8e753a4c46264285911a45fcb14f4718179e711b1ce990524"));
    features.setImported(false);

    return features;

  }

  /**
   * @return A new device reset success (wallet created)
   */
  public static Success newDeviceResetSuccess() {
    return new Success(
      "Device reset",
      new byte[]{}
    );
  }

  /**
   * @return A new device wiped success
   */
  public static Success newDeviceWipedSuccess() {

    return new Success(
      "Device wiped",
      new byte[]{}
    );

  }

  /**
   * @return A new cipher key success (abandon wallet)
   */
  public static Success newCipherKeySuccess() {
    return new Success(
      "",
      Utils.HEX.decode("ec406a3c796099050400f65ab311363e")
    );
  }

  /**
   * @return A new PIN entry failure
   */
  public static Failure newPinFailure() {

    return new Failure(
      FailureType.PIN_INVALID,
      ""
    );
  }

  /**
   * @return A new confirm wipe button request
   */
  public static ButtonRequest newWipeDeviceButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.WIPE_DEVICE,
      ""
    );

  }

  /**
   * @return A new confirm word button request
   */
  public static ButtonRequest newConfirmWordButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.CONFIRM_WORD,
      ""
    );

  }

  /**
   * @return A new "other" button request (cipher key etc)
   */
  public static ButtonRequest newOtherButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.OTHER,
      ""
    );

  }

  /**
   * @return A new PIN matrix for "current" (unlock)
   */
  public static PinMatrixRequest newCurrentPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.CURRENT);

  }

  /**
   * @return A new PIN matrix for "new first" (set)
   */
  public static PinMatrixRequest newNewFirstPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.NEW_FIRST);

  }

  /**
   * @return A new PIN matrix for "new second" (confirm)
   */
  public static PinMatrixRequest newNewSecondPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.NEW_SECOND);

  }

  /**
   * @return A new standard public key for M (abandon)
   */
  public static PublicKey newStandardPublicKey_M() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03d902f35f560e0470c63313c7369168d9d7df2d49bf295fd9fb7cb109ccee0494"),
      false,
      null,
      true,
      Utils.HEX.decode("7923408dadd3c7b56eed15567707ae5e5dca089de972e07f3b860450e2a3b70e"),
      true,
      0,
      true,
      0,
      true,
      0
    );

    return new PublicKey(
      true,
      "xpub661MyMwAqRbcFkPHucMnrGNzDwb6teAX1RbKQmqtEF8kK3Z7LZ59qafCjB9eCRLiTVG3uxBxgKvRgbubRhqSKXnGGb1aoaqLrpMBDrVxga8",
      Utils.HEX.decode("0488b21e0000000000000000007923408dadd3c7b56eed15567707ae5e5dca089de972e07f3b860450e2a3b70e03d902f35f560e0470c63313c7369168d9d7df2d49bf295fd9fb7cb109ccee0494c7fe61f5"),
      true,
      hdNodeType
    );
  }

  /**
   * @return A new standard public key for M/44H (abandon)
   */
  public static PublicKey newStandardPublicKey_M_44H() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03428a2da3e76291667a67a38ed45468ceb0d156bc8beda6e86fbc4cf295087c2b"),
      false,
      null,
      true,
      Utils.HEX.decode("45d3b0e8206db10a08d555317c7e245c5bbd12254ce968f3c79a959d4e6af98a"),
      true,
      0x8000002c,
      true,
      1,
      true,
      0x73c5da0a
    );

    return new PublicKey(
      true,
      "xpub68jrRzQopSUSfYDVF7r6KMbite5ge2zei1y94YhzTbJvt9wUC9DXaEPfvmcz7E5XdgQYTvUqehtjSM3Zvc4MadbTzabTNZvWq12kjzkKA3b",
      Utils.HEX.decode("0488b21e0173c5da0a8000002c45d3b0e8206db10a08d555317c7e245c5bbd12254ce968f3c79a959d4e6af98a03428a2da3e76291667a67a38ed45468ceb0d156bc8beda6e86fbc4cf295087c2b1a4472fa"),
      true,
      hdNodeType
    );
  }

  /**
   * @return A new standard public key for M/44H/0H (abandon)
   */
  public static PublicKey newStandardPublicKey_M_44H_0H() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03f72f0e3684b0d7295f391616f12a469070bfcd175c55366239047495a2c1c410"),
      false,
      null,
      true,
      Utils.HEX.decode("af0894dc5f2d5bed0dc85b2fd2053a98575765c144e4e64126ee1009b38860b2"),
      true,
      0x80000000,
      true,
      2,
      true,
      0x88b3582b
    );

    return new PublicKey(
      true,
      "xpub6AmukNpN4yyLgyzSysjU6JqqoYA1mVUvtinHYdBGPDppatJXHxT8CcDsmBo9n3yLBgrcw9z62ygt1siT9xai4UaJ2w4FPmY6kPCF96YN2cF",
      Utils.HEX.decode("0488b21e0288b3582b80000000af0894dc5f2d5bed0dc85b2fd2053a98575765c144e4e64126ee1009b38860b203f72f0e3684b0d7295f391616f12a469070bfcd175c55366239047495a2c1c4101d4fcb78"),
      true,
      hdNodeType
    );
  }

  /**
   * @return A new standard public key for M/44H/0H/0H (abandon)
   */
  public static PublicKey newStandardPublicKey_M_44H_0H_0H() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03774c910fcf07fa96886ea794f0d5caed9afe30b44b83f7e213bb92930e7df4bd"),
      false,
      null,
      true,
      Utils.HEX.decode("3da4bc190a2680111d31fadfdc905f2a7f6ce77c6f109919116f253d43445219"),
      true,
      0x80000000,
      true,
      3,
      true,
      0x155bca59
    );

    return new PublicKey(
      true,
      "xpub6BosfCnifzxcFwrSzQiqu2DBVTshkCXacvNsWGYJVVhhawA7d4R5WSWGFNbi8Aw6ZRc1brxMyWMzG3DSSSSoekkudhUd9yLb6qx39T9nMdj",
      Utils.HEX.decode("0488b21e03155bca59800000003da4bc190a2680111d31fadfdc905f2a7f6ce77c6f109919116f253d4344521903774c910fcf07fa96886ea794f0d5caed9afe30b44b83f7e213bb92930e7df4bdc84b94ea"),
      true,
      hdNodeType
    );
  }
}
