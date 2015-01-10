package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardDeferredHideEvent;
import org.multibit.hd.ui.events.view.WizardPopoverHideEvent;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.Popovers;
import org.multibit.hd.ui.views.layouts.WizardCardLayout;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


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
public abstract class AbstractWizard<M extends AbstractWizardModel> {

  private static final Logger log = LoggerFactory.getLogger(AbstractWizard.class);

  /**
   * The wizard screen holder card layout to which each wizard screen panel is added
   */
  private final WizardCardLayout cardLayout = new WizardCardLayout(0, 0);
  /**
   * Keeps all of the wizard screen panels in a card layout
   */
  private final JPanel wizardScreenHolder = Panels.newPanel(cardLayout);

  private final M wizardModel;
  protected Optional wizardParameter = Optional.absent();

  /**
   * True if the wizard supports the Exit button
   */
  private final boolean exiting;

  /**
   * Maps the panel name to the panel views
   */
  private Map<String, AbstractWizardPanelView> wizardViewMap = Maps.newHashMap();

  /**
   * Ensures we only have a single thread managing the wizard hide operation
   */
  private final static ListeningExecutorService wizardHideExecutorService = SafeExecutors.newSingleThreadExecutor("wizard-hide");

  /**
   * @param wizardModel     The overall wizard data model containing the aggregate information of all components in the wizard
   * @param isExiting       True if the exit button should trigger an application shutdown
   * @param wizardParameter An optional parameter that can be referenced during construction
   */
  protected AbstractWizard(M wizardModel, boolean isExiting, Optional wizardParameter) {

    Preconditions.checkNotNull(wizardModel, "'model' must be present");

    log.debug("Building wizard...");

    this.wizardModel = wizardModel;
    this.exiting = isExiting;
    this.wizardParameter = wizardParameter;

    CoreServices.uiEventBus.register(this);

    // Always bind the ESC key to a Cancel event (escape to safety)
    wizardScreenHolder.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "quit");
    wizardScreenHolder.getActionMap().put("quit", getCancelAction());

    // TODO Bind the ENTER key to a Next/Finish/Apply event to speed up data entry through keyboard
    //wizardPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "next");
    //wizardPanel.getActionMap().put("next", getNextAction(null));

    log.debug("Populating view map and firing initial state view events...");

    // Populate based on the current locale
    populateWizardViewMap(wizardViewMap);

    // Once all the views are created allow events to occur
    for (Map.Entry<String, AbstractWizardPanelView> entry : wizardViewMap.entrySet()) {

      // Ensure the panel is in the correct starting state
      entry.getValue().fireInitialStateViewEvents();

    }

    wizardScreenHolder.setMinimumSize(new Dimension(MultiBitUI.WIZARD_MIN_WIDTH, MultiBitUI.WIZARD_MIN_HEIGHT));
    wizardScreenHolder.setPreferredSize(new Dimension(MultiBitUI.WIZARD_MIN_WIDTH, MultiBitUI.WIZARD_MIN_HEIGHT));
    wizardScreenHolder.setSize(new Dimension(MultiBitUI.WIZARD_MIN_WIDTH, MultiBitUI.WIZARD_MIN_HEIGHT));

    // Show the panel specified by the initial state
    show(wizardModel.getPanelName());

  }

  /**
   * <p>Show the named panel</p>
   *
   * @param panelName The panel name
   */
  public void show(String panelName) {

    if (!wizardViewMap.containsKey(panelName)) {
      log.error("'{}' is not a valid panel name. Check the panel has been registered in the view map. Registered panels are\n{}", wizardViewMap.keySet());
      return;
    }

    final AbstractWizardPanelView wizardPanelView = wizardViewMap.get(panelName);

    if (!wizardPanelView.isInitialised()) {

      // Initialise the wizard screen panel and add it to the card layout parent
      wizardScreenHolder.add(wizardPanelView.getWizardScreenPanel(true), panelName);

    }

    // De-register any existing default buttons from previous panels
    wizardPanelView.deregisterDefaultButton();

    // Provide warning that the panel is about to be shown
    if (wizardPanelView.beforeShow()) {

      // No abort so show
      log.debug("Showing wizard panel: {}", panelName);
      cardLayout.show(wizardScreenHolder, panelName);

      wizardPanelView.afterShow();
    }

  }

  /**
   * <p>Hide the wizard if <code>beforeHide</code> returns true</p>
   *
   * @param panelName    The panel name
   * @param isExitCancel True if this hide operation comes from an exit or cancel
   */
  public void hide(final String panelName, final boolean isExitCancel) {

    if (!wizardViewMap.containsKey(panelName)) {
      log.error("'{}' is not a valid panel name. Check the panel has been registered in the view map. Registered panels are\n{}", wizardViewMap.keySet());
      return;
    }

    final AbstractWizardPanelView wizardPanelView = wizardViewMap.get(panelName);

    // Provide warning that the panel is about to be hidden
    if (wizardPanelView.beforeHide(isExitCancel)) {

      // No cancellation so go ahead with the hide
      handleHide(panelName, isExitCancel, wizardPanelView);

    }

  }

  /**
   * <p>Add fresh content to the wizard view map</p>
   * <p>The map will be empty whenever this is called</p>
   */
  protected abstract void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap);

  /**
   * @return The wizard panel
   */
  public JPanel getWizardScreenHolder() {
    return wizardScreenHolder;
  }

  /**
   * @return The wizard panel view associated with the given panel name
   */
  public AbstractWizardPanelView getWizardPanelView(String panelName) {
    return wizardViewMap.get(panelName);
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

        // Can immediately close since no data will be lost
        hide(wizardModel.getPanelName(), true);

        // After panel has hidden we can initiate the shutdown so that MainController
        // will gracefully close the application
        CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);
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

        if (getWizardModel().isDirty()) {

          // Check with the user about throwing away their data (handle the outcome with a WizardPopoverHideEvent)
          Panels.showLightBoxPopover(
            Popovers.newDiscardYesNoPopoverMaV(getWizardModel().getPanelName())
              .getView()
              .newComponentPanel()
          );

        } else {

          // Can immediately close since no data will be lost
          hide(wizardModel.getPanelName(), true);

        }

      }
    };

  }

  /**
   * @param wizardView The wizard view (providing a reference to its underlying panel model)
   *
   * @return The "finish" action based on the model state
   */
  public <P> Action getFinishAction(final AbstractWizardPanelView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        hide(wizardModel.getPanelName(), false);

      }
    };
  }

  /**
   * @param wizardView The wizard view (providing a reference to its underlying panel model)
   *
   * @return The "apply" action based on the model state
   */
  public <P> Action getApplyAction(final AbstractWizardPanelView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        hide(wizardModel.getPanelName(), false);

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
   * @param wizardPanelView The wizard panel view (providing a reference to its underlying panel model)
   *
   * @return The "next" action based on the model state
   */
  public <P> Action getNextAction(final AbstractWizardPanelView<M, P> wizardPanelView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        wizardPanelView.updateFromComponentModels(Optional.absent());

        // Move to the next state
        wizardModel.showNext();

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
  public <P> Action getPreviousAction(final AbstractWizardPanelView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        wizardView.updateFromComponentModels(Optional.absent());

        // Aggregate the panel information into the wizard model

        // Move to the previous state
        wizardModel.showPrevious();

        // Show the panel based on the state
        show(wizardModel.getPanelName());
      }
    };
  }

  /**
   * @param wizardView The wizard view (providing a reference to its underlying panel model)
   *
   * @return The "restore" action based on the model state
   */
  public <P> Action getRestoreAction(final AbstractWizardPanelView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // The UI will lock up during handover so prevent further events
        JButton source = (JButton) e.getSource();
        source.setEnabled(false);

        // Since #17 all restore work is done by the welcome wizard
        // See MainController for the hand over code
        hide(CredentialsState.CREDENTIALS_RESTORE.name(), false);

      }
    };
  }

  @Subscribe
  public void onWizardPopoverHideEvent(WizardPopoverHideEvent event) {

    if (getWizardModel().getPanelName().equals(event.getPanelName())) {

      if (getWizardModel().isDirty() && !event.isExitCancel()) {

        // User has authorised the underlying panel to be closed
        hide(wizardModel.getPanelName(), true);

      }

    }

  }

  @Subscribe
  public void onWizardDeferredHideEvent(WizardDeferredHideEvent event) {

    // Fail fast
    if (wizardViewMap.isEmpty()) {
      log.trace("Wizard panel view {} is still finalising.", event.getPanelName());
      return;
    }

    String panelName = event.getPanelName();

    if (getWizardModel().getPanelName().equals(panelName)) {

      final AbstractWizardPanelView wizardPanelView = wizardViewMap.get(panelName);

      // This is a deferred hide so don't call hide() again
      handleHide(panelName, event.isExitCancel(), wizardPanelView);

    }

  }

  /**
   * <p>Hide the wizard</p>
   *
   * @param panelName       The panel name
   * @param isExitCancel    True if this hide operation comes from an exit or cancel
   * @param wizardPanelView The wizard panel view from the wizard view map
   */
  private void handleHide(final String panelName, boolean isExitCancel, AbstractWizardPanelView wizardPanelView) {

    log.debug("Handle hide starting: '{}' ExitCancel: {}", panelName, isExitCancel);

    // De-register
    wizardPanelView.deregisterDefaultButton();

    // Ensure we de-register for all hardware wallet events
    if (this instanceof AbstractHardwareWalletWizard) {
      try {
        HardwareWalletService.hardwareWalletEventBus.unregister(this);
      } catch (IllegalArgumentException e) {
        // Do nothing
      }
    }

    // Issue the wizard hide event before the hide takes place to give UI time to update
    ViewEvents.fireWizardHideEvent(panelName, wizardModel, isExitCancel);

    // Required to run on a new thread since this may take some time to complete
    wizardHideExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {

          // Require some extra time to get the rest of the UI started for credentials wizard
          // There is no chance of the system showing a light box during this time so this
          // operation is safe
          if (CredentialsState.CREDENTIALS_ENTER_PASSWORD.name().equals(panelName)) {

            log.trace("Blocking to allow UI startup to complete");
            Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
          }

          // Work through the view map ensuring all components are deregistered from UI events
          log.trace("Deregister {} views and their component(s)", wizardViewMap.size());
          for (Map.Entry<String, AbstractWizardPanelView> entry : wizardViewMap.entrySet()) {

            AbstractWizardPanelView panelView = entry.getValue();

            // Ensure we deregister the wizard panel view (and model if present) for events
            try {

              CoreServices.uiEventBus.unregister(panelView);
              log.trace("Deregistered wizard panel view '{}' from UI events", panelView.getPanelName());

              if (panelView.getPanelModel().isPresent()) {
                Object panelModel = panelView.getPanelModel().get();
                CoreServices.uiEventBus.unregister(panelModel);
                log.trace("Deregistered wizard panel model '{}' from UI events", panelView.getPanelName());
              }

            } catch (NullPointerException | IllegalArgumentException e) {
              log.warn("Wizard panel model/view '{}' was not registered", panelView.getPanelName(), e);
            }

            // Deregister all components
            @SuppressWarnings("unchecked")
            List<ModelAndView> mavs = panelView.getComponents();
            for (ModelAndView mav : mavs) {
              mav.close();
            }
            log.trace("Closed {} registered component(s) from wizard panel view '{}'", mavs.size(), panelView.getPanelName());

            // Remove the references
            mavs.clear();

          }

          // Depopulate the map to ensure non-AWT references are removed
          wizardViewMap.clear();

          // Hiding the light box must be on the EDT
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                log.trace("Handle hide remove light box: '{}'", panelName);

                // This removes the reference to the wizard allowing for garbage collection
                Panels.hideLightBoxIfPresent();

              }
            });

        }
      });

  }

}
