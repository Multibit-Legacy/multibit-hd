package org.multibit.hd.testing;

import com.google.common.base.Optional;
import com.google.protobuf.Message;
import org.bitcoinj.crypto.ChildNumber;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.events.MessageEvents;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.HardwareWalletMessage;
import org.multibit.hd.hardware.core.messages.PublicKey;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;

import java.util.List;

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
   * @return The Trezor hardware wallet client
   */
  public static AbstractTrezorHardwareWalletClient createAttachedInitialisedClient() {

    // This section makes extensive use of Mockito
    // Please read http://docs.mockito.googlecode.com/hg/org/mockito/Mockito.html

    AbstractTrezorHardwareWalletClient mockClient = mock(AbstractTrezorHardwareWalletClient.class);

    when(mockClient.attach()).thenReturn(true);

    mockConnect(mockClient);

    mockInitialise(mockClient);

    mockDeterministicHierarchy(mockClient);

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
          MessageEvents.fireMessageEvent(MessageEventType.DEVICE_CONNECTED);
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
  private static void mockInitialise(AbstractTrezorHardwareWalletClient mockClient) {
    when(mockClient.initialise()).thenAnswer(
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {

          Features features = HardwareWalletEventFixtures.newStandardFeatures();

          MessageEvent event = new MessageEvent(
            MessageEventType.FEATURES,
            Optional.<HardwareWalletMessage>of(features),
            Optional.<Message>absent()
          );

          MessageEvents.fireMessageEvent(event);
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
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {

          List<ChildNumber> childNumberList = (List<ChildNumber>) invocation.getArguments()[0];

          PublicKey publicKeyM = HardwareWalletEventFixtures.newStandardPublicKey_M();

          MessageEvent event = new MessageEvent(
            MessageEventType.PUBLIC_KEY,
            Optional.<HardwareWalletMessage>of(publicKeyM),
            Optional.<Message>absent()
          );

          MessageEvents.fireMessageEvent(event);

          return true;
        }
      });
  }

}
