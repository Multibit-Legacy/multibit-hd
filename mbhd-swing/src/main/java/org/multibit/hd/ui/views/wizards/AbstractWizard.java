package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
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
import java.util.Map;

/**
 * <p>Abstract base class to provide the following to UI:</p>
 * <ul>
 * <li>Provision of common methods to wizards</li>
 * </ul>
 *
 * @param <M> the wizard model
 *
 * @since 0.0.1
 */
public abstract class AbstractWizard<M extends WizardModel> {

  private static final Logger log = LoggerFactory.getLogger(AbstractWizard.class);

  private static final int WIZARD_MIN_WIDTH = 600;
  private static final int WIZARD_MIN_HEIGHT = 400;

  private final WizardCardLayout cardLayout;
  private final JPanel wizardPanel;
  private final M wizardModel;

  private final boolean exiting;
  private Map<String, AbstractWizardView> wizardViewMap = Maps.newHashMap();

  /**
   * @param wizardModel The overall wizard data model containing the aggregate information of all components in the wizard
   * @param isExiting   True if the exit button should trigger an application shutdown
   */
  protected AbstractWizard(M wizardModel, boolean isExiting) {

    Preconditions.checkNotNull(wizardModel, "'model' must be present");

    this.wizardModel = wizardModel;
    this.exiting = isExiting;

    CoreServices.uiEventBus.register(this);

    cardLayout = new WizardCardLayout(0, 0);
    wizardPanel = Panels.newPanel(cardLayout);

    // Use current locale for initial creation
    onLocaleChangedEvent(new LocaleChangedEvent());

    wizardPanel.setMinimumSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));
    wizardPanel.setPreferredSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));

    wizardPanel.setSize(new Dimension(WIZARD_MIN_WIDTH, WIZARD_MIN_HEIGHT));

  }

  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    Preconditions.checkNotNull(event, "'event' must be present");

    // Clear out any existing components
    wizardPanel.removeAll();

    // Clear out any existing views
    wizardViewMap.clear();

    // Re-populate based on the new locale (could involve an LTR or RTL transition)
    populateWizardViewMap(wizardViewMap);

    // Bind the views into the wizard panel, and share their panel names
    for (Map.Entry<String, AbstractWizardView> entry : wizardViewMap.entrySet()) {

      // Add it to the panel
      wizardPanel.add(entry.getValue().getWizardPanel(), entry.getKey());

    }

    // Once all the views are initialised allow events to occur
    for (Map.Entry<String, AbstractWizardView> entry : wizardViewMap.entrySet()) {

      // Ensure the panel is in the correct starting state
      entry.getValue().fireInitialStateViewEvents();

    }

    // Invalidate for new layout
    Panels.invalidate(wizardPanel);

  }

  /**
   * <p>Add fresh content to the wizard view map</p>
   * <p>The map will be empty whenever this is called</p>
   */
  protected abstract void populateWizardViewMap(Map<String, AbstractWizardView> wizardViewMap);

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

  /**
   * @param wizardView The wizard view (providing a reference to its underlying panel model)
   *
   * @return The "finish" action based on the model state
   */
  public <P> Action getFinishAction(final AbstractWizardView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.hideLightBox();
      }
    };
  }

  /**
   * @return The wizard model
   */
  public M getWizardModel() {
    return wizardModel;
  }

  /**
   * @param wizardView The wizard view (providing a reference to its underlying panel model)
   *
   * @return The "next" action based on the model state
   */
  public <P> Action getNextAction(final AbstractWizardView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        if (!wizardView.updatePanelModel()) {

          // Aggregate the panel information into the wizard model
          wizardModel.update(wizardView.getPanelModel());

        }

        // Move to the next state
        wizardModel.next();

        // Show the panel based on the state
        show(wizardModel.getPanelName());
      }
    };
  }

  /**
   * @param wizardView The wizard view (providing a reference to its underlying panel model)
   *
   * @return The "previous" action based on the model state
   */
  public <P> Action getPreviousAction(final AbstractWizardView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        wizardView.updatePanelModel();

        // Aggregate the panel information into the wizard model

        // Move to the previous state
        wizardModel.previous();

        // Show the panel based on the state
        show(wizardModel.getPanelName());
      }
    };
  }

}
