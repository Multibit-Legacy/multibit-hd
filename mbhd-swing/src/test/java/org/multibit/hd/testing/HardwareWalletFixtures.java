package org.multibit.hd.testing;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.events.MessageEvents;
import org.multibit.hd.hardware.trezor.clients.AbstractTrezorHardwareWalletClient;

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
   *   <li>Attached</li>
   *   <li>Fires DEVICE_CONNECTED when connect() is called</li>
   * </ul>
   * @return The Trezor hardware wallet client
   */
  public static AbstractTrezorHardwareWalletClient createAttachedClient() {

    AbstractTrezorHardwareWalletClient mockClient = mock(AbstractTrezorHardwareWalletClient.class);

    when(mockClient.attach()).thenReturn(true);

    when(mockClient.connect()).thenAnswer(
      new Answer<Boolean>() {
        public Boolean answer(InvocationOnMock invocation) throws Throwable {
          MessageEvents.fireMessageEvent(MessageEventType.DEVICE_CONNECTED);
          return true;
        }
      });

    return mockClient;
  }
}
