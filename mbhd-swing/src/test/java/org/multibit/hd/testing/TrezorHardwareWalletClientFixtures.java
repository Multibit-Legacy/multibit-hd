package org.multibit.hd.testing;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Message;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;
import org.mockito.Matchers;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Factory to provide the following to hardware wallet FEST tests:</p>
 * <ul>
 * <li>Various Trezor hardware wallet clients providing use case support</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TrezorHardwareWalletClientFixtures {

  private static final ListeningExecutorService messageEventServices = SafeExecutors.newSingleThreadExecutor("fest-client-fixture-events");

  /**
   * Utility classes have private constructors
   */
  private TrezorHardwareWalletClientFixtures() {
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

    mockPinMatrixAck_CurrentPin(mockClient);

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

    mockInitialise_CreateWallet(mockClient);

    mockWipeDevice(mockClient);

    mockPinMatrixAck_CreateWallet(mockClient);

    mockEntropyAck(mockClient);

    mockWordAck_12_Success(mockClient);

    mockDeterministicHierarchy(mockClient);

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
   * <p>Configure for a FEATURES based on the create wallet use case</p>
   * <ol>
   *   <li>First call triggers "wiped" FEATURES</li>
   *   <li>Subsequent calls trigger "initialised" FEATURES</li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockInitialise_CreateWallet(AbstractTrezorHardwareWalletClient mockClient) {

    when(mockClient.initialise()).thenAnswer(
      new Answer<Boolean>() {

        int count=0;

        public Boolean answer(InvocationOnMock invocation) throws Throwable {

          final Features features;
          switch (count) {
            case 0:
              features = HardwareWalletEventFixtures.newWipedFeatures();
              break;
            default:
              features = HardwareWalletEventFixtures.newStandardFeatures();
          }

          MessageEvent event = new MessageEvent(
            MessageEventType.FEATURES,
            Optional.<HardwareWalletMessage>of(features),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          count++;

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
   * <p>Configure for PIN matrix responses when creating a wallet</p>
   * <ol>
   * <li>"1234" is a correct PIN</li>
   * <li>First call triggers a PIN_MATRIX_REQUEST for "second PIN"</li>
   * <li>Second call triggers an ENTROPY_REQUEST</li>
   * <li>Subsequent calls do nothing so rely on event fixtures to provide use case context</li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockPinMatrixAck_CreateWallet(AbstractTrezorHardwareWalletClient mockClient) {

    // Failed PIN
    when(mockClient.pinMatrixAck("1234")).thenAnswer(
      new Answer<Optional<Message>>() {

        int count = 0;

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          MessageEvent event;
          switch (count) {
            case 0:
              // PIN entered (first)
              event = new MessageEvent(
                MessageEventType.PIN_MATRIX_REQUEST,
                Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newNewSecondPinMatrix()),
                Optional.<Message>absent()
              );
              fireMessageEvent(event);
              break;
            case 1:
              // PIN entered (second)
              event = new MessageEvent(
                MessageEventType.ENTROPY_REQUEST,
                Optional.<HardwareWalletMessage>absent(),
                Optional.<Message>absent()
              );
              fireMessageEvent(event);
              break;
            case 2:
              // PIN entered (current) - expect cipher key request
              event = new MessageEvent(
                MessageEventType.BUTTON_REQUEST,
                Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newProtectCallButtonRequest()),
                Optional.<Message>absent()
              );
              fireMessageEvent(event);
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
   * <p>Configure for entering a PIN (6789 will cause failure, use event fixtures for next response)</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockPinMatrixAck_CurrentPin(AbstractTrezorHardwareWalletClient mockClient) {

    // Failed PIN
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
   * <p>Configure for a CIPHER_KEY value</p>
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
   * <p>Configure for a WORD_ACK message (12 words offered, 12 confirmed, success)</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockWipeDevice(AbstractTrezorHardwareWalletClient mockClient) {

    when(mockClient.wipeDevice()).thenAnswer(
      new Answer<Optional<Message>>() {

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          MessageEvent event = new MessageEvent(
            MessageEventType.BUTTON_REQUEST,
            Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newWipeDeviceButtonRequest()),
            Optional.<Message>absent()
          );

          fireMessageEvent(event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a WORD_ACK message (12 words offered, 12 confirmed, success)</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockEntropyAck(AbstractTrezorHardwareWalletClient mockClient) {

    when(mockClient.entropyAck(Matchers.<byte[]>anyObject())).thenAnswer(
      new Answer<Optional<Message>>() {

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event;
            event = new MessageEvent(
              MessageEventType.BUTTON_REQUEST,
              Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newConfirmWordButtonRequest()),
              Optional.<Message>absent()
            );

          fireMessageEvent(event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a WORD_ACK message (12 words offered, 12 confirmed, success)</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private static void mockWordAck_12_Success(AbstractTrezorHardwareWalletClient mockClient) {

    when(mockClient.wordAck(anyString())).thenAnswer(
      new Answer<Optional<Message>>() {

        int wordCount = 0;

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          wordCount++;

          final MessageEvent event;
          if (wordCount < 24) {
            event = new MessageEvent(
              MessageEventType.BUTTON_REQUEST,
              Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newConfirmWordButtonRequest()),
              Optional.<Message>absent()
            );
          } else {
            event = new MessageEvent(
              MessageEventType.SUCCESS,
              Optional.<HardwareWalletMessage>of(HardwareWalletEventFixtures.newDeviceResetSuccess()),
              Optional.<Message>absent()
            );
          }

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
  public static void fireMessageEvent(final MessageEvent event) {

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
  public static void fireMessageEvent(final MessageEventType eventType) {

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
