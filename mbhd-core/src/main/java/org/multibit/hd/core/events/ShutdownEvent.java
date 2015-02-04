package org.multibit.hd.core.events;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a shutdown event</li>
 * </ul>
 *
 * <p>This is an infrequent event</p>
 *
 * @since 0.0.1
 */
public class ShutdownEvent implements CoreEvent {

  public enum ShutdownType {

    /**
     * <p>A hard shutdown involves triggering a delayed System.exit() to
     * guarantee termination of the application.</p>
     *
     * <p>It is used in production in response to an application exit or quit.</p>
     */
    HARD,

    /**
     * <p>A soft shutdown performs all the same tasks as a hard shutdown but
     * does not issue the System.exit().</p>
     *
     * <p>It is used in FEST testing to allow garbage collection to clean up the
     * remaining references since it is not forked.</p>

     * <p>Do not confuse a soft shutdown with a configuration UI reset.</p>
     */
    SOFT,

    /**
     * <p>A switch shutdown performs a subset of tasks that are specific to
     * closing a wallet (stopping peer group, block store etc). It leaves other
     * services running (exchange ticker, Bitcoin URI listening etc).</p>
     *
     * <p>It is used when switching wallets.</p>
     */
    SWITCH

  }

  final ShutdownType shutdownType;

  /**
   */
  public ShutdownEvent(ShutdownType shutdownType) {

    this.shutdownType = shutdownType;

  }

  /**
   * @return The shutdown type
   */
  public ShutdownType getShutdownType() {
    return shutdownType;
  }

}
