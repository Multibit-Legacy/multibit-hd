package org.multibit.hd.ui.views.detail_views;

import org.multibit.hd.ui.views.wizards.exit.ExitState;
import org.multibit.hd.ui.views.wizards.exit.ExitWizard;
import org.multibit.hd.ui.views.wizards.exit.ExitWizardModel;
import org.multibit.hd.ui.views.wizards.receive_bitcoin.ReceiveBitcoinState;
import org.multibit.hd.ui.views.wizards.receive_bitcoin.ReceiveBitcoinWizard;
import org.multibit.hd.ui.views.wizards.receive_bitcoin.ReceiveBitcoinWizardModel;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizard;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of different wizards targeting various use cases</li>
 * </ul>
 *
 * <h3>Overview of the Wizard architecture</h3>
 *
 * <p>A wizard presents a series of panels enclosed in a light box. This is in contrast to the
 * standard modal dialog approach offered by Swing which is more limited and offers less customisation
 * opportunities.</p>
 *
 * <p>From a data perspective each wizard consists of one "wizard model" which has many "panel models"
 * each of which have many "component models". Components are reused across panels and so do not maintain
 * a back reference to a parent panels but instead use a <code>WizardComponentModelChangedEvent</code> to
 * inform all interested panels that their data has changed. Events are filtered by the panel name to prevent
 * collisions.</p>
 *
 * <p>A "wizard view" has a consistent layout: a title and description (top), some components (middle) and a row of
 * buttons (bottom). The top and bottom rows are handled mainly by boilerplate code leaving just the presentation
 * and management of the middle section to the developer.</p>
 *
 * <h3>Quickly assembling a wizard</h3>
 *
 * <p>The quickest way to get a wizard up and running is to take an existing one and modify it accordingly. If
 * your requirement is straightforward (no MaV components or reliance on previous panels) then the boilerplate
 * will handle all the work for you.</p>
 *
 * @since 0.0.1
 *  
 */
public class DetailViews {

  /**
   * @return A new "exit" wizard
   */
  public static ExitWizard newExitWizard() {

    return new ExitWizard(new ExitWizardModel(ExitState.CONFIRM_EXIT), true);
  }

  /**
   * @return A new "send bitcoin" wizard
   */
  public static SendBitcoinWizard newSendBitcoinWizard() {

    return new SendBitcoinWizard(new SendBitcoinWizardModel(SendBitcoinState.ENTER_AMOUNT), false);

  }

  /**
   * @return A new "receive bitcoin" wizard
   */
  public static ReceiveBitcoinWizard newReceiveBitcoinWizard() {

    return new ReceiveBitcoinWizard(new ReceiveBitcoinWizardModel(ReceiveBitcoinState.ENTER_AMOUNT), false);

  }

  /**
   * @return A new "welcome" wizard for the initial set up
   */
  public static WelcomeWizard newExitingWelcomeWizard(WelcomeWizardState initialState) {

    return new WelcomeWizard(new WelcomeWizardModel(initialState), true);
  }

  /**
   * @return A new "welcome" wizard for recovery set up
   */
  public static WelcomeWizard newClosingWelcomeWizard(WelcomeWizardState initialState) {

    return new WelcomeWizard(new WelcomeWizardModel(initialState), false);
  }

}
