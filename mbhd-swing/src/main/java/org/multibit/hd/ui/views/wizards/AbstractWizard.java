package org.multibit.hd.ui.views.wizards;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

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

  private CardLayout cardLayout = new CardLayout();
  private final JPanel wizardPanel = Panels.newPanel(cardLayout);

  protected AbstractWizard() {

    CoreServices.uiEventBus.register(this);

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

    // Added new content so validate/repaint
    wizardPanel.validate();
    wizardPanel.repaint();

  }

  /**
   * <p>Add fresh content to the wizard panel. The panel will be empty whenever this is called.</p>
   */
  protected abstract void addWizardContent(JPanel wizardPanel);

  /**
   * <p>Close the wizard</p>
   */
  public void close() {

    Panels.hideLightBox();

  }

  /**
   * Show the previous panel
   */
  public void previous() {

    // TODO Limit to first page
    cardLayout.previous(wizardPanel);
  }

  /**
   * Show the next panel
   */
  public void next() {

    // TODO Limit to last page
    cardLayout.next(wizardPanel);
  }

  /**
   * @return The wizard panel
   */
  public JPanel getWizardPanel() {
    return wizardPanel;
  }
}
