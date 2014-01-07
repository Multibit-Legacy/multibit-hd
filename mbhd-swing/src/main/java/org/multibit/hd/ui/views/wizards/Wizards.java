package org.multibit.hd.ui.views.wizards;

import org.multibit.hd.ui.views.wizards.exit.ExitState;
import org.multibit.hd.ui.views.wizards.exit.ExitWizard;
import org.multibit.hd.ui.views.wizards.exit.ExitWizardModel;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizard;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

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

    return new SendBitcoinWizard(new SendBitcoinWizardModel(SendBitcoinState.ENTER_AMOUNT));

  }

  /**
   * @return A new "exit" wizard
   */
  public static ExitWizard newExitWizard() {

    // TODO Implement this
    return new ExitWizard(new ExitWizardModel(ExitState.CONFIRM_EXIT));
  }

  /**
   * @return A new "welcome" wizard for recovery set up
   */
  public static WelcomeWizard newClosingWelcomeWizard() {

    WelcomeWizard wizard = newExitingWelcomeWizard();
    wizard.setExiting(false);

    return wizard;
  }

  /**
   * @return A new "welcome" wizard for the initial set up
   */
  public static WelcomeWizard newExitingWelcomeWizard() {

    WelcomeWizard wizard = new WelcomeWizard(new WelcomeWizardModel(WelcomeWizardState.WELCOME));
    wizard.setExiting(true);

    return wizard;
  }

}
