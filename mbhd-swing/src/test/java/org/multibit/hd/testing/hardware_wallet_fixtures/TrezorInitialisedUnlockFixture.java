package org.multibit.hd.testing.hardware_wallet_fixtures;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.protobuf.Message;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.hardware.core.HardwareWalletClient;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.HardwareWalletMessage;
import org.multibit.hd.hardware.core.messages.PublicKey;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;
import org.multibit.hd.testing.MessageEventFixtures;

import java.util.List;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Hardware wallet fixture to provide the following to FEST requirements:</p>
 * <ul>
 * <li>Low level events and client handling</li>
 * </ul>
 *
 * <p>Emulates an attached initialised Trezor during the Unlock use case</p>
 *
 * @since 0.0.1
 *  
 */
public class TrezorInitialisedUnlockFixture extends AbstractHardwareWalletFixture {

  @Override
  public void setUpClient() {

    client = mock(AbstractTrezorHardwareWalletClient.class);

    when(client.attach()).thenReturn(true);

    mockConnect(client);

    mockInitialise(client);

    mockDeterministicHierarchy(client);

    mockPinMatrixAck(client);

    mockGetCipherKey(client);

  }

  @Override
  public void setUpMessageQueue() {

    messageEvents.clear();

    // Standard client will start as attached

    // Cipher key success
    final MessageEvent event1 = new MessageEvent(
      MessageEventType.CIPHERED_KEY_VALUE,
      Optional.<HardwareWalletMessage>of(MessageEventFixtures.newCipheredKeyValue()),
      Optional.<Message>absent()
    );

    messageEvents.add(event1);
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
   * <p>Configure for a FEATURES based on the standard features</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  private void mockInitialise(HardwareWalletClient client) {
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
  private void mockGetCipherKey(HardwareWalletClient client) {

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
   * <li>First call triggers a "protect call" BUTTON_REQUEST</li>
   * <li>Subsequent calls do nothing so rely on event fixtures to provide use case context</li>
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
              // PIN entered (current)
              event = new MessageEvent(
                MessageEventType.BUTTON_REQUEST,
                Optional.<HardwareWalletMessage>of(MessageEventFixtures.newProtectCallButtonRequest()),
                Optional.<Message>absent()
              );
              fireMessageEvent("Correct current PIN. Confirm encrypt.", event);
              break;
            default:
              // Do nothing
          }

          count++;

          return Optional.absent();
        }
      });

  }

}
