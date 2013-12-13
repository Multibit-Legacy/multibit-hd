package org.multibit.hd.ui.views.wizards;

import org.multibit.hd.ui.views.wizards.exit.ExitWizard;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizard;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of wizard panels</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Wizards {

  /**
   * @return A new "send bitcoin" wizard
   */
  public static SendBitcoinWizard newSendBitcoinWizard() {

    return new SendBitcoinWizard();

  }

  /**
   * @return A new "exit" wizard
   */
  public static ExitWizard newExitWizard() {

    return new ExitWizard();
  }
}
