package org.multibit.hd.testing.hardware_wallet_fixtures;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Message;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.hardware.core.HardwareWalletClient;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.events.MessageEvents;
import org.multibit.hd.hardware.core.messages.*;
import org.multibit.hd.testing.MessageEventFixtures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

/**
 * <p>Abstract base class to provide the following to hardware wallet fixtures:</p>
 * <ul>
 * <li>Support code common to all hardware wallet fixtures</li>
 * </ul>
 *
 * @since 0.0.5
 *  
 */
public abstract class AbstractHardwareWalletFixture implements HardwareWalletFixture {

  private static final Logger log = LoggerFactory.getLogger(AbstractHardwareWalletFixture.class);

  protected final Queue<MessageEvent> messageEvents = Queues.newArrayBlockingQueue(100);

  /**
   * The hardware wallet client
   */
  protected HardwareWalletClient client;

  public AbstractHardwareWalletFixture() {

    setUpClient();

    setUpMessageQueue();

  }

  @Override
  public HardwareWalletClient getClient() {
    return client;
  }

  @Override
  public void fireNextEvent(String description) {

    Preconditions.checkState(!messageEvents.isEmpty(), "Unexpected call to empty queue. The test should know when the last event has been fired.");

    // Get the head of the queue
    MessageEvent event = messageEvents.remove();

    log.info("'{}' requires event {}", description, event.getEventType());

    MessageEvents.fireMessageEvent(event);

    // Allow time for the event to be picked up and propagated
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

  }

  /**
   * <p>Fire a new low level message event on its own thread</p>
   *
   * @param event The event
   */
  public void fireMessageEvent(String description, final MessageEvent event) {

    log.info("'{}' requires event {}", description, event.getEventType());

    MessageEvents.fireMessageEvent(event);

    // Allow time for the event to propagate
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

  }

  /**
   * <p>Fire a new low level message event on its own thread</p>
   *
   * @param eventType The event type (no payload)
   */
  public void fireMessageEvent(String description, final MessageEventType eventType) {

    log.info("'{}' requires event type {}", description, eventType);

    MessageEvents.fireMessageEvent(eventType);

    // Allow time for the event to propagate
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

  }

  /**
   * <p>Set up the mock client responses to API calls</p>
   * <p>Please read the Mockito documentation: http://docs.mockito.googlecode.com/hg/org/mockito/Mockito.html</p>
   */
  public abstract void setUpClient();

  /**
   * <p>Set up the low level message queue for user and device responses</p>
   */
  public abstract void setUpMessageQueue();

  /**
   * <p>Configure for a PUBLIC_KEY message for M</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  @SuppressWarnings("unchecked")
  protected void useDeterministicHierarchyNoPIN(HardwareWalletClient client) {

    when(client.getDeterministicHierarchy(anyListOf(ChildNumber.class))).thenAnswer(
      new Answer<Optional<Message>>() {

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          // This unchecked cast is known to be OK
          List<ChildNumber> childNumberList = (List<ChildNumber>) invocation.getArguments()[0];

          final PublicKey publicKey;

          switch (childNumberList.size()) {
            case 0:
              // M
              publicKey = MessageEventFixtures.newStandardPublicKey_M();
              break;
            case 1:
              // M/44H
              publicKey = MessageEventFixtures.newStandardPublicKey_M_44H();
              break;
            case 2:
              // M/44H/0H
              publicKey = MessageEventFixtures.newStandardPublicKey_M_44H_0H();
              break;
            case 3:
              // M/44H/0H/0H
              publicKey = MessageEventFixtures.newStandardPublicKey_M_44H_0H_0H();
              break;
            default:
              throw new IllegalStateException("Unexpected child number count: " + childNumberList.size());
          }

          final MessageEvent event = new MessageEvent(
            MessageEventType.PUBLIC_KEY,
            Optional.<HardwareWalletMessage>of(publicKey),
            Optional.<Message>absent()
          );

          fireMessageEvent("Provide Public Key", event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a CIPHER_KEY value</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  protected void useGetCipherKeyWithPIN(HardwareWalletClient client) {

    byte[] key = "MultiBit HD     Unlock".getBytes(Charsets.UTF_8);
    byte[] keyValue = "0123456789abcdef".getBytes(Charsets.UTF_8);

    when(client.cipherKeyValue(0, KeyChain.KeyPurpose.RECEIVE_FUNDS, 0, key, keyValue, true, true, true)).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event = new MessageEvent(
            MessageEventType.PIN_MATRIX_REQUEST,
            Optional.<HardwareWalletMessage>of(MessageEventFixtures.newCurrentPinMatrix()),
            Optional.<Message>absent()
          );

          fireMessageEvent("Cipher key protected. Provide PIN.", event);

          return Optional.absent();
        }
      });

  }

  /**
   * <p>Configure for PIN matrix responses when unlocking a wallet (no previous create)</p>
   * <ol>
   * <li>"1234" is a correct PIN, "6789" will trigger FAILURE</li>
   * <li>Each call provides a standard BUTTON_REQUEST.Protect</li>
   * <li>Subsequent calls do nothing so rely on event fixtures to provide use case context</li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  protected void usePinMatrixAckWithProtect(HardwareWalletClient client) {

    when(client.pinMatrixAck("1234")).thenAnswer(
      new Answer<Optional<Message>>() {

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          MessageEvent event = new MessageEvent(
            MessageEventType.BUTTON_REQUEST,
            Optional.<HardwareWalletMessage>of(MessageEventFixtures.newProtectCallButtonRequest()),
            Optional.<Message>absent()
          );
          fireMessageEvent("Correct current PIN. Confirm encrypt.", event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for PIN matrix responses when unlocking a wallet (no previous create)</p>
   * <ol>
   * <li>"1234" is a correct PIN, "6789" will trigger FAILURE</li>
   * <li>First call provides a standard PUBLIC_KEY of M</li>
   * <li>Subsequent calls do nothing so rely on event fixtures to provide use case context</li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  protected void usePinMatrixAckWithPublicKey(HardwareWalletClient client) {
    // Failed PIN
    when(client.pinMatrixAck("1234")).thenAnswer(
      new Answer<Optional<Message>>() {

        int count = 0;

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          MessageEvent event;
          switch (count) {
            case 0:
              // PIN entered (current)
              event = new MessageEvent(
                MessageEventType.PUBLIC_KEY,
                Optional.<HardwareWalletMessage>of(MessageEventFixtures.newStandardPublicKey_M()),
                Optional.<Message>absent()
              );
              fireMessageEvent("Correct current PIN. Provide public key.", event);
              break;
            default:
              // Do nothing
          }

          count++;

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a CIPHER_KEY value</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  protected void useGetCipherKeyNoPIN(HardwareWalletClient client) {
    byte[] key = "MultiBit HD     Unlock".getBytes(Charsets.UTF_8);
    byte[] keyValue = "0123456789abcdef".getBytes(Charsets.UTF_8);

    when(client.cipherKeyValue(0, KeyChain.KeyPurpose.RECEIVE_FUNDS, 0, key, keyValue, true, true, true)).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event = new MessageEvent(
            MessageEventType.BUTTON_REQUEST,
            Optional.<HardwareWalletMessage>of(MessageEventFixtures.newOtherButtonRequest()),
            Optional.<Message>absent()
          );

          fireMessageEvent("Cipher key requires button press", event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for series of PUBLIC_KEY messages for PIN, M, M/44H, M/44H/0H, M/44H/0H/0H</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  @SuppressWarnings("unchecked")
  protected void useDeterministicHierarchyPIN(HardwareWalletClient client) {
    when(client.getDeterministicHierarchy(anyListOf(ChildNumber.class))).thenAnswer(
      new Answer<Optional<Message>>() {

        private int count = 0;

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          if (count == 0) {

            count++;

            final MessageEvent event = new MessageEvent(
              MessageEventType.PIN_MATRIX_REQUEST,
              Optional.<HardwareWalletMessage>of(MessageEventFixtures.newCurrentPinMatrix()),
              Optional.<Message>absent()
            );

            fireMessageEvent("Deterministic hierarchy is protected (1.3.3+ firmware). Provide PIN.", event);

            return Optional.absent();

          }

          // This unchecked cast is known to be OK
          List<ChildNumber> childNumberList = (List<ChildNumber>) invocation.getArguments()[0];

          final PublicKey publicKey;

          switch (childNumberList.size()) {
            case 0:
              // M
              publicKey = MessageEventFixtures.newStandardPublicKey_M();
              break;
            case 1:
              // M/44H
              publicKey = MessageEventFixtures.newStandardPublicKey_M_44H();
              break;
            case 2:
              // M/44H/0H
              publicKey = MessageEventFixtures.newStandardPublicKey_M_44H_0H();
              break;
            case 3:
              // M/44H/0H/0H
              publicKey = MessageEventFixtures.newStandardPublicKey_M_44H_0H_0H();
              break;
            default:
              throw new IllegalStateException("Unexpected child number count: " + childNumberList.size());
          }

          final MessageEvent event = new MessageEvent(
            MessageEventType.PUBLIC_KEY,
            Optional.<HardwareWalletMessage>of(publicKey),
            Optional.<Message>absent()
          );

          fireMessageEvent("Provide Public Key", event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a FEATURES based on the standard features</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  protected void useInitialiseWithStandardFeatures(HardwareWalletClient client) {
    when(client.initialise()).thenAnswer(
      new Answer<Optional<MessageEvent>>() {
        public Optional<MessageEvent> answer(InvocationOnMock invocation) throws Throwable {

          Features features = MessageEventFixtures.newStandardFeatures();

          MessageEvent event = new MessageEvent(
            MessageEventType.FEATURES,
            Optional.<HardwareWalletMessage>of(features),
            Optional.<Message>absent()
          );

          fireMessageEvent("Provide Features", event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a DEVICE_CONNECTED</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  protected void useConnectWithConnected(HardwareWalletClient client) {
    when(client.connect()).thenAnswer(
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {
          fireMessageEvent("Device connected", MessageEventType.DEVICE_CONNECTED);
          return true;
        }
      });
  }

  /**
   * <p>Configure for series of SIGN_TX messages for PIN, TX_INPUT, TX_OUTPUT, TX_META</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   * <p>See <a href="https://github.com/bitcoin-solutions/multibit-hardware/wiki/Trezor-SignTx-Messages">MultiBit Hardware wiki</a> for more information</p>
   *
   * @param client The mock client
   */
  protected void useSignTxPIN(HardwareWalletClient client) {
    when(client.signTx(any(Transaction.class))).thenAnswer(
      new Answer<Optional<Message>>() {

        private int count = 0;

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          if (count == 0) {

            count++;

            final MessageEvent event = new MessageEvent(
              MessageEventType.PIN_MATRIX_REQUEST,
              Optional.<HardwareWalletMessage>of(MessageEventFixtures.newCurrentPinMatrix()),
              Optional.<Message>absent()
            );

            fireMessageEvent("Deterministic hierarchy is protected (1.3.3+ firmware). Provide PIN.", event);

            return Optional.absent();

          }

          // Treat as a finished signed transaction

          byte[] signedTx = Utils.HEX.decode(
            "01000000010e7ef28d101c87a83a32aa78b9887eda3eb741dfc9840a" +
              "2f50c361b4deba4287000000006a473044022010d8e5b0b3800bca7a" +
              "047bd08dce18d66b8f2930f8c99d666203ef5be7608b0f02201ed659" +
              "4ce1bc03b6416b8b4411909082f20b70ac6e71a2cb60b097d5286cce" +
              "9e012102bc8398d00c6ca116c8ce18ee0a4be7c004d679e880a865b7" +
              "5db866a4e23481dfffffffff02a0860100000000001976a9141ee9f7" +
              "6e2d8d536ec035601c2b8ef4e28cf50b9888aca08601000000000019" +
              "76a9141ee9f76e2d8d536ec035601c2b8ef4e28cf50b9888ac000000" +
              "00"
          );

          // Build the serialized type
          TxRequestSerializedType txRequestSerializedType = new TxRequestSerializedType(
            true,
            signedTx,
            true,
            0,
            false,
            new byte[]{}
          );

          // Build the request details type
          TxRequestDetailsType txRequestDetailsType = new TxRequestDetailsType(
            false,
            0,
            false,
            new byte[]{}
          );

          // Skip to the end
          HardwareWalletMessage txRequest = new TxRequest(
            TxRequestType.TX_FINISHED,
            txRequestDetailsType,
            txRequestSerializedType
          );

          final MessageEvent event = new MessageEvent(
            MessageEventType.TX_REQUEST,
            Optional.of(txRequest),
            Optional.<Message>absent()
          );

          fireMessageEvent("Provide Public Key", event);

          return Optional.absent();
        }
      });

  }

}
