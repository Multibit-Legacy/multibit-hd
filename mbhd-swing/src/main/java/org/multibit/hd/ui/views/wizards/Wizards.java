package org.multibit.hd.ui.views.wizards;

import org.multibit.hd.ui.views.wizards.exit.ExitWizard;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizard;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;

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

  /**
   * @return A new "welcome" wizard for the initial set up
   */
  public static WelcomeWizard newExitingWelcomeWizard() {

    WelcomeWizard wizard = new WelcomeWizard();
    wizard.setExiting(true);

    return wizard;
  }

  /**
   * @return A new "welcome" wizard for recovery set up
   */
  public static WelcomeWizard newClosingWelcomeWizard() {

    WelcomeWizard wizard = new WelcomeWizard();
    wizard.setExiting(false);

    return wizard;
  }

}
