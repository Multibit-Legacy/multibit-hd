package org.multibit.hd.testing.hardware_wallet_fixtures;

import com.google.common.base.Optional;
import com.google.protobuf.Message;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.hardware.core.HardwareWalletClient;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.messages.HardwareWalletMessage;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;
import org.multibit.hd.testing.MessageEventFixtures;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Hardware wallet fixture to provide the following to FEST requirements:</p>
 * <ul>
 * <li>Low level events and client handling</li>
 * </ul>
 *
 * <p>Emulates an attached initialised Trezor during the re-attach use case</p>
 *
 * <p>Presents a PIN request on "get cipher key"</p>

 * @since 0.0.1
 *  
 */
public class TrezorInitialisedReattachedFixture extends AbstractHardwareWalletFixture {

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

    // Trezor has been detached
    final MessageEvent event1 = new MessageEvent(
      MessageEventType.DEVICE_DETACHED,
      Optional.<HardwareWalletMessage>absent(),
      Optional.<Message>absent()
    );

    messageEvents.add(event1);

    // Trezor has been attached
    final MessageEvent event2 = new MessageEvent(
      MessageEventType.DEVICE_ATTACHED,
      Optional.<HardwareWalletMessage>absent(),
      Optional.<Message>absent()
    );

    messageEvents.add(event2);

    // ButtonAck -> Cipher key success
    final MessageEvent event3 = new MessageEvent(
      MessageEventType.CIPHERED_KEY_VALUE,
      Optional.<HardwareWalletMessage>of(MessageEventFixtures.newCipheredKeyValue()),
      Optional.<Message>absent()
    );

    messageEvents.add(event3);
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
    useInitialiseWithStandardFeatures(client);
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
   * <li>First call triggers a "protect call" BUTTON_REQUEST</li>
   * <li>Subsequent calls do nothing so rely on event fixtures to provide use case context</li>
   * </ol>
   * <p>Fires low level messages that trigger state changes in the MultiBit Hardware FSM</p>
   *
   * @param client The mock client
   */
  private void mockPinMatrixAck(HardwareWalletClient client) {

    // Failed PIN
    usePinMatrixAckWithProtect(client);
  }

}
