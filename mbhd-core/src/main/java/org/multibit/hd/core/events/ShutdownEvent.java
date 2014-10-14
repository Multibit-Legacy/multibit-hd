package org.multibit.hd.core.events;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a shutdown event</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ShutdownEvent implements CoreEvent {

  public enum ShutdownType {

    /**
     * A hard shutdown involves triggering a delayed System.exit() to
     * guarantee termination of the application.
     *
     * It is used in production in response to an application exit or quit.
     */
    HARD,

    /**
     * A soft shutdown performs all the same tasks as a hard shutdown but
     * does not issue the System.exit().
     *
     * It is used in FEST testing to allow garbage collection to clean up the
     * remaining references since it is not forked.
     */
    SOFT,

    /**
     * A standby shutdown performs a subset of tasks that are specific to
     * closing a wallet (stopping peer group, block store etc). It leaves other
     * services running (exchange ticker, Bitcoin URI listening etc).
     *
     * It is used in switching wallets.
     */
    STANDBY

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
