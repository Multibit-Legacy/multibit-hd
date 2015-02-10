package org.multibit.hd.testing.hardware_wallet_fixtures;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.protobuf.Message;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.hardware.core.HardwareWalletClient;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.HardwareWalletMessage;
import org.multibit.hd.hardware.core.messages.PublicKey;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;

import java.util.List;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.multibit.hd.testing.MessageEventFixtures.*;

/**
 * <p>Hardware wallet fixture to provide the following to FEST requirements:</p>
 * <ul>
 * <li>Low level events and client handling</li>
 * </ul>
 *
 * <p>Emulates an attached wiped (factory fresh) Trezor during the create wallet use case</p>
 *
 * @since 0.0.1
 *  
 */
public class TrezorWipedFixture extends AbstractHardwareWalletFixture {

  @Override
  public void setUpClient() {

    client = mock(AbstractTrezorHardwareWalletClient.class);

    when(client.attach()).thenReturn(true);

    mockConnect(client);

    mockInitialise(client);

    mockWipeDevice(client);

    mockPinMatrixAck(client);

    mockEntropyAck(client);

    mockDeterministicHierarchy(client);

    mockGetCipherKey(client);

  }

  @Override
  public void setUpMessageQueue() {

    messageEvents.clear();

    // Wipe success
    final MessageEvent event1 = new MessageEvent(
      MessageEventType.SUCCESS,
      Optional.<HardwareWalletMessage>of(newDeviceWipedSuccess()),
      Optional.<Message>absent()
    );

    messageEvents.add(event1);

    // Request PIN (first)
    final MessageEvent event2 = new MessageEvent(
      MessageEventType.PIN_MATRIX_REQUEST,
      Optional.<HardwareWalletMessage>of(newNewFirstPinMatrix()),
      Optional.<Message>absent()
    );

    messageEvents.add(event2);

    // Overall need 23 more button presses
    for (int i = 0; i < 23; i++) {
      final MessageEvent event = new MessageEvent(
        MessageEventType.BUTTON_REQUEST,
        Optional.<HardwareWalletMessage>of(newConfirmWordButtonRequest()),
        Optional.<Message>absent()
      );

      messageEvents.add(event);
    }

    // Wallet create success
    final MessageEvent event3 = new MessageEvent(
      MessageEventType.SUCCESS,
      Optional.<HardwareWalletMessage>of(newDeviceResetSuccess()),
      Optional.<Message>absent()
    );

    messageEvents.add(event3);

    // Cipher key success
    final MessageEvent event4 = new MessageEvent(
      MessageEventType.CIPHERED_KEY_VALUE,
      Optional.<HardwareWalletMessage>of(newCipheredKeyValue()),
      Optional.<Message>absent()
    );

    messageEvents.add(event4);

  }

  /**
   * <p>Configure for a DEVICE_CONNECTED</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  private void mockConnect(HardwareWalletClient client) {
    when(client.connect()).thenAnswer(
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {
          fireMessageEvent("Device connected", MessageEventType.DEVICE_CONNECTED);
          return true;
        }
      });
  }

  /**
   * <p>Configure for a FEATURES based on the create wallet use case</p>
   * <ol>
   * <li>First call triggers "wiped" FEATURES</li>
   * <li>Subsequent calls trigger "initialised" FEATURES</li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private void mockInitialise(HardwareWalletClient mockClient) {

    when(mockClient.initialise()).thenAnswer(
      new Answer<Optional<MessageEvent>>() {

        int count = 0;

        public Optional<MessageEvent> answer(InvocationOnMock invocation) throws Throwable {

          final Features features;
          switch (count) {
            case 0:
              features = newWipedFeatures();
              break;
            default:
              features = newStandardFeatures();
          }

          MessageEvent event = new MessageEvent(
            MessageEventType.FEATURES,
            Optional.<HardwareWalletMessage>of(features),
            Optional.<Message>absent()
          );

          fireMessageEvent("Features", event);

          count++;

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
  private void mockWipeDevice(HardwareWalletClient mockClient) {

    when(mockClient.wipeDevice()).thenAnswer(
      new Answer<Optional<Message>>() {

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          MessageEvent event = new MessageEvent(
            MessageEventType.BUTTON_REQUEST,
            Optional.<HardwareWalletMessage>of(newWipeDeviceButtonRequest()),
            Optional.<Message>absent()
          );

          fireMessageEvent("Confirm wipe", event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for PIN matrix responses when unlocking a wallet (no previous create)</p>
   * <ol>
   * <li>"1234" is a correct PIN</li>
   * <li>First call triggers PIN_REQUEST for second</li>
   * <li>Second call triggers </li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  private void mockPinMatrixAck(HardwareWalletClient client) {

    // Failed PIN
    when(client.pinMatrixAck("1234")).thenAnswer(
      new Answer<Optional<Message>>() {

        int count = 0;

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          MessageEvent event;
          switch (count) {
            case 0:
              // New PIN entered so request second
              event = new MessageEvent(
                MessageEventType.PIN_MATRIX_REQUEST,
                Optional.<HardwareWalletMessage>of(newNewSecondPinMatrix()),
                Optional.<Message>absent()
              );
              fireMessageEvent("Correct new PIN, request second", event);
              break;
            case 1:
              // Second PIN entered so request entropy
              event = new MessageEvent(
                MessageEventType.ENTROPY_REQUEST,
                Optional.<HardwareWalletMessage>absent(),
                Optional.<Message>absent()
              );
              fireMessageEvent("Correct second PIN, request entropy", event);
              break;
            case 2:
              // Current PIN entered so request confirm cipher key
              event = new MessageEvent(
                MessageEventType.BUTTON_REQUEST,
                Optional.<HardwareWalletMessage>of(newOtherButtonRequest()),
                Optional.<Message>absent()
              );
              fireMessageEvent("Correct current PIN, confirm cipher key", event);
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
   * <p>Configure for a ENTROPY_ACK message </p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param mockClient The mock client
   */
  private void mockEntropyAck(HardwareWalletClient mockClient) {

    when(mockClient.entropyAck(Matchers.<byte[]>anyObject())).thenAnswer(
      new Answer<Optional<Message>>() {

        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event;
          event = new MessageEvent(
            MessageEventType.BUTTON_REQUEST,
            Optional.<HardwareWalletMessage>of(newConfirmWordButtonRequest()),
            Optional.<Message>absent()
          );

          fireMessageEvent("Entropy ack, confirm 1st word", event);

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
  private void mockGetCipherKey(HardwareWalletClient client) {

    byte[] key = "MultiBit HD     Unlock".getBytes(Charsets.UTF_8);
    byte[] keyValue = "0123456789abcdef".getBytes(Charsets.UTF_8);

    when(client.cipherKeyValue(0, KeyChain.KeyPurpose.RECEIVE_FUNDS, 0, key, keyValue, true, true, true)).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          final MessageEvent event = new MessageEvent(
            MessageEventType.PIN_MATRIX_REQUEST,
            Optional.<HardwareWalletMessage>of(newCurrentPinMatrix()),
            Optional.<Message>absent()
          );

          fireMessageEvent("Cipher key protected. Provide PIN.", event);

          return Optional.absent();
        }
      });
  }

  /**
   * <p>Configure for a PUBLIC_KEY message for M</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  @SuppressWarnings("unchecked")
  private void mockDeterministicHierarchy(HardwareWalletClient client) {

    when(client.getDeterministicHierarchy(anyListOf(ChildNumber.class))).thenAnswer(
      new Answer<Optional<Message>>() {
        public Optional<Message> answer(InvocationOnMock invocation) throws Throwable {

          // This unchecked cast is known to be OK
          List<ChildNumber> childNumberList = (List<ChildNumber>) invocation.getArguments()[0];

          final PublicKey publicKey;

          switch (childNumberList.size()) {
            case 0:
              // M
              publicKey = newStandardPublicKey_M();
              break;
            case 1:
              // M/44H
              publicKey = newStandardPublicKey_M_44H();
              break;
            case 2:
              // M/44H/0H
              publicKey = newStandardPublicKey_M_44H_0H();
              break;
            case 3:
              // M/44H/0H/0H
              publicKey = newStandardPublicKey_M_44H_0H_0H();
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


}
