package org.multibit.hd.ui.views.wizards;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.layouts.WizardCardLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * <p>Abstract base class to provide the following to UI:</p>
 * <ul>
 * <li>Provision of common methods to wizards</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractWizard {

  private static final Logger log = LoggerFactory.getLogger(AbstractWizard.class);

  private static final int WIZARD_MIN_WIDTH = 600;
  private static final int WIZARD_MIN_HEIGHT = 400;

  private final WizardCardLayout cardLayout;
  private final JPanel wizardPanel;

  private boolean exiting = false;

  protected AbstractWizard() {

    CoreServices.uiEventBus.register(this);

    cardLayout = new WizardCardLayout(0, 0);
    wizardPanel = Panels.newPanel(cardLayout);

    // Use current local for initial creation
    onLocaleChangedEvent(null);

    wizardPanel.setMinimumSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));
    wizardPanel.setPreferredSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));

    wizardPanel.setSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));

  }

  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    log.debug("Received 'locale changed' event");

    // Clear out any existing components
    wizardPanel.removeAll();

    // Re-populate based on the new locale (could involve an LTR or RTL transition)
    addWizardContent(wizardPanel);

    // Invalidate for new layout
    Panels.invalidate(wizardPanel);

  }

  /**
   * <p>Add fresh content to the wizard panel</p>
   * <p>The panel will be empty whenever this is called</p>
   */
  protected abstract void addWizardContent(JPanel wizardPanel);

  /**
   * <p>Close the wizard</p>
   */
  public void close() {

    Panels.hideLightBox();

  }

  /**
   * <p>Show the previous panel</p>
   */
  public void previous() {

    if (cardLayout.hasPrevious()) {
      cardLayout.previous(wizardPanel);
    }
  }

  /**
   * <p>Show the next panel</p>
   */
  public void next() {

    if (cardLayout.hasNext()) {
      cardLayout.next(wizardPanel);
    }
  }

  /**
   * <p>Show the named panel</p>
   */
  public void show(String name) {

    cardLayout.show(wizardPanel, name);

  }

  /**
   * @return The wizard panel
   */
  public JPanel getWizardPanel() {
    return wizardPanel;
  }

  /**
   * @param exiting True if the wizard should trigger an "exit" event rather than a "close"
   */
  public void setExiting(boolean exiting) {
    this.exiting = exiting;
  }

  /**
   * @return True if the wizard should trigger an "exit" event rather than a "close"
   */
  public boolean isExiting() {
    return exiting;
  }

  /**
   * @return The standard "exit" action to trigger application shutdown
   */
  public Action getExitAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CoreEvents.fireShutdownEvent();
      }
    };

  }

  /**
   * @return The standard "cancel" action to trigger the removal of the lightbox
   */
  public Action getCancelAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Panels.hideLightBox();
      }
    };

  }


}
