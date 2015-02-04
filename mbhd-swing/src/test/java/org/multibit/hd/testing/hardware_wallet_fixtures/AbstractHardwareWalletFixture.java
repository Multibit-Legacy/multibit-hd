package org.multibit.hd.testing.hardware_wallet_fixtures;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.hardware.core.HardwareWalletClient;
import org.multibit.hd.hardware.core.events.MessageEvent;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.events.MessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * <p>Abstract base class to provide the following to hardware wallet fixtures:</p>
 * <ul>
 * <li>Support code common to all hardware wallet fixtures</li>
 * </ul>
 *
 * @since 0.0.1
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

}
