package org.multibit.hd.brit.services;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to configured instances of BRIT services</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class BRITServices {

  private static final Logger log = LoggerFactory.getLogger(BRITServices.class);

  /**
   * Send or register events to the BRIT subscribers
   */
  public static EventBus britEventBus = new EventBus();

  /**
   * Keep track of fees due to the BRIT redeemers
   */
  private static FeeService feeService;


  /**
   * Utilities have a private constructor
   */
  private BRITServices() {
  }

  /**
   * @return A new FeeService
   */
  public static FeeService newFeeService() {
    log.debug("Creating new fee service");
    return new FeeService();

  }
}
