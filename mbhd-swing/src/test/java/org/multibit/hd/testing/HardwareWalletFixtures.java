package org.multibit.hd.testing;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Message;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.events.MessageEvents;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.HardwareWalletMessage;
import org.multibit.hd.hardware.core.messages.PublicKey;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *  
 */
public class HardwareWalletFixtures {

  private static final ListeningExecutorService messageEventServices = SafeExecutors.newSingleThreadExecutor("fest-fixture-events");

  /**
   * Utility classes have private constructors
   */
  private HardwareWalletFixtures() {
  }

  /**
   * <p>Create a hardware wallet with the following characteristics:</p>
   * <ul>
   * <li>Attached</li>
   * <li>Is initialised with the standard Trezor hardware wallet</li>
   * </ul>
   *
   * @return A new Trezor hardware wallet client
   */
  public static AbstractTrezorHardwareWalletClient newClient_Initialised() {

    // This section makes extensive use of Mockito
    // Please read http://docs.mockito.googlecode.com/hg/org/mockito/Mockito.html

    AbstractTrezorHardwareWalletClient mockClient = mock(AbstractTrezorHardwareWalletClient.class);

    when(mockClient.attach()).thenReturn(true);

    mockConnect(mockClient);

    mockInitialise_Initialised(mockClient);

    mockDeterministicHierarchy(mockClient);

    mockPinMatrixAck(mockClient);

    mockGetCipherKey(mockClient);

    return mockClient;
  }

  /**
   * <p>Create a hardware wallet with the following characteristics:</p>
   * <ul>
   * <li>Attached</li>
   * <li>Wiped (factory fresh)</li>
   * </ul>
   *
   * @return A new Trezor hardware wallet client
   */
  public static AbstractTrezorHardwareWalletClient newClient_Wiped() {

    // This section makes extensive use of Mockito
    // Please read http://docs.mockito.googlecode.com/hg/org/mockito/Mockito.html

    AbstractTrezorHardwareWalletClient mockClient = mock(AbstractTrezorHardwareWalletClient.class);

    when(mockClient.attach()).thenReturn(true);

    mockConnect(mockClient);

    mockInitialise_Wiped(mockClient);

    mockDeterministicHierarchy(mockClient);

    mockPinMatrixAck(mockClient);

    mockGetCipherKey(mockClient);

    return mockClient;
  }

  /**
   * <p>Configure for a DEVICE_CONNECTED</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockConnect(AbstractTrezorHardwareWalletClient mockClient) {
    when(mockClient.connect()).thenAnswer(
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {
          fireMessageEvent(MessageEventType.DEVICE_CONNECTED);
          return true;
        }
      });
  }

  /**
   * <p>Configure for a FEATURES based on the standard features</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockInitialise_Initialised(AbstractTrezorHardwareWalletClient mockClient) {
    when(mockClient.initialise()).thenAnswer(
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {

          Features features = HardwareWalletEventFixtures.newStandardFeatures();

          MessageEvent event = new MessageEvent(
            MessageEventType.FEATURES,
            Optional.<HardwareWalletMessage>of(features),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          return true;
        }
      });
  }

  /**
   * <p>Configure for a FEATURES based on the standard features</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockInitialise_Wiped(AbstractTrezorHardwareWalletClient mockClient) {
    when(mockClient.initialise()).thenAnswer(
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {

          Features features = HardwareWalletEventFixtures.newWipedFeatures();

          MessageEvent event = new MessageEvent(
            MessageEventType.FEATURES,
            Optional.<HardwareWalletMessage>of(features),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          return true;
        }
      });
  }

  /**
   * <p>Configure for a PUBLIC_KEY message for M</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockDeterministicHierarchy(AbstractTrezorHardwareWalletClient mockClient) {

    when(mockClient.getDeterministicHierarchy(anyListOf(ChildNumber.class))).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          List<ChildNumber> childNumberList = (List<ChildNumber>) invocation.getArguments()[0];

          final PublicKey publicKey;

          switch (childNumberList.size()) {
            case 0:
              // M
              publicKey = HardwareWalletEventFixtures.newStandardPublicKey_M();
              break;
            case 1:
              // M/44H
              publicKey = HardwareWalletEventFixtures.newStandardPublicKey_M_44H();
              break;
            case 2:
              // M/44H/0H
              publicKey = HardwareWalletEventFixtures.newStandardPublicKey_M_44H_0H();
              break;
            case 3:
              // M/44H/0H/0H
              publicKey = HardwareWalletEventFixtures.newStandardPublicKey_M_44H_0H_0H();
              break;
            default:
              throw new IllegalStateException("Unexpected child number count: " + childNumberList.size());
          }

          final MessageEvent event = new MessageEvent(
            MessageEventType.PUBLIC_KEY,
            Optional.<HardwareWalletMessage>of(publicKey),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a PUBLIC_KEY message for M</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockPinMatrixAck(AbstractTrezorHardwareWalletClient mockClient) {

    // Successful PIN
    when(mockClient.pinMatrixAck("1234")).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event = new MessageEvent(
            MessageEventType.BUTTON_REQUEST,
            Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newOtherButtonRequest()),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          return Optional.absent();
        }
      });

    // Deliberate failed PIN
    when(mockClient.pinMatrixAck("6789")).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event = new MessageEvent(
            MessageEventType.FAILURE,
            Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newPinFailure()),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          return Optional.absent();
        }
      });

  }

  /**
   * <p>Configure for a PUBLIC_KEY message for M</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockGetCipherKey(AbstractTrezorHardwareWalletClient mockClient) {

    byte[] key = "MultiBit HD     Unlock".getBytes(Charsets.UTF_8);
    byte[] keyValue = "0123456789abcdef".getBytes(Charsets.UTF_8);

    when(mockClient.cipherKeyValue(0, KeyChain.KeyPurpose.RECEIVE_FUNDS, 0, key, keyValue, true, true, true)).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event = new MessageEvent(
            MessageEventType.PIN_MATRIX_REQUEST,
            Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newCurrentPinMatrix()),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Fire a new low level message event on its own thread</p>
   *
   * @param event The event
   */
  private static void fireMessageEvent(final MessageEvent event) {

    messageEventServices.submit(
      new Runnable() {
        @Override
        public void run() {
          MessageEvents.fireMessageEvent(event);
        }
      });

    // Allow time for the event to propagate
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

  }

  /**
   * <p>Fire a new low level message event on its own thread</p>
   *
   * @param eventType The event type (no payload)
   */
  private static void fireMessageEvent(final MessageEventType eventType) {

    messageEventServices.submit(
      new Runnable() {
        @Override
        public void run() {
          MessageEvents.fireMessageEvent(eventType);
        }
      });

    // Allow time for the event to propagate
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

  }

}
