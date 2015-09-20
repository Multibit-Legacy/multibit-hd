package org.multibit.hd.testing.hardware_wallet_fixtures.trezor;

import com.google.common.base.Optional;
import com.google.protobuf.Message;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.core.dto.WalletMode;
import org.multibit.hd.hardware.core.HardwareWalletClient;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.HardwareWalletMessage;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;
import org.multibit.hd.testing.hardware_wallet_fixtures.AbstractHardwareWalletFixture;
import org.multibit.hd.testing.message_event_fixtures.MessageEventFixtures;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Hardware wallet fixture to provide the following to FEST requirements:</p>
 * <ul>
 * <li>Low level events and client handling</li>
 * </ul>
 *
 * <p>Emulates an attached initialised Trezor during the unsupported configuration "passphrase" use case</p>
 * <p>Presents a PIN request on "get cipher key"</p>
 *
 * @since 0.0.8
 *  
 */
public class TrezorInitialisedUnsupportedConfigurationPassphraseFixture extends AbstractHardwareWalletFixture {

  public TrezorInitialisedUnsupportedConfigurationPassphraseFixture(String name) {
    super(name);
  }

  @Override
  public void setUpClient() {

    client = mock(AbstractTrezorHardwareWalletClient.class);

    when(client.name()).thenReturn(name);
    when(client.attach()).thenReturn(true);
    when(client.verifyFeatures(any(Features.class))).thenReturn(true);

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

    // Cipher key success (first unlock)
    final MessageEvent event1 = new MessageEvent(
      MessageEventType.CIPHERED_KEY_VALUE,
      Optional.<HardwareWalletMessage>of(MessageEventFixtures.newCipheredKeyValue()),
      Optional.<Message>absent(),
      name
    );

    messageEvents.add(event1);

    // Cipher key success (second unlock after restore)
    final MessageEvent event2 = new MessageEvent(
      MessageEventType.CIPHERED_KEY_VALUE,
      Optional.<HardwareWalletMessage>of(MessageEventFixtures.newCipheredKeyValue()),
      Optional.<Message>absent(),
      name
    );

    messageEvents.add(event2);

  }

  /**
   * <p>Configure for a DEVICE_CONNECTED</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  private void mockConnect(HardwareWalletClient client) {
    useConnectWithConnected(client);
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

          Features features = MessageEventFixtures.newUnsupportedConfigurationPassphraseFeatures(WalletMode.TREZOR);

          MessageEvent event = new MessageEvent(
            MessageEventType.FEATURES,
            Optional.<HardwareWalletMessage>of(features),
            Optional.<Message>absent(),
            name
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

    useDeterministicHierarchyNoPIN(client);
  }

  /**
   * <p>Configure for a CIPHER_KEY value</p>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  private void mockGetCipherKey(HardwareWalletClient client) {

    useGetCipherKeyWithPIN(client);

  }

  /**
   * <p>Configure for PIN matrix responses when unlocking a wallet (no previous create)</p>
   * <ol>
   * <li>"1234" is a correct PIN, "6789" will trigger FAILURE</li>
   * <li>All calls trigger a "protect call" BUTTON_REQUEST</li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  private void mockPinMatrixAck(HardwareWalletClient client) {
    usePinMatrixAckWithProtect(client);


  }

}
