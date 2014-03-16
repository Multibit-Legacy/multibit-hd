package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardPopoverHideEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.Popovers;
import org.multibit.hd.ui.views.layouts.WizardCardLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
public abstract class AbstractWizard<M extends AbstractWizardModel> {

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

  private final boolean exiting;
  private Map<String, AbstractWizardPanelView> wizardViewMap = Maps.newHashMap();

  /**
   * @param wizardModel     The overall wizard data model containing the aggregate information of all components in the wizard
   * @param isExiting       True if the exit button should trigger an application shutdown
   * @param wizardParameter An optional parameter that can be referenced during construction
   */
  protected AbstractWizard(M wizardModel, boolean isExiting, Optional wizardParameter) {

    Preconditions.checkNotNull(wizardModel, "'model' must be present");

    this.wizardModel = wizardModel;
    this.exiting = isExiting;
    this.wizardParameter = wizardParameter;

    CoreServices.uiEventBus.register(this);

    // Bind the ESC key to a Cancel/Exit event
    wizardScreenHolder.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "quit");
    if (isExiting) {
      wizardScreenHolder.getActionMap().put("quit", getExitAction());
    } else {
      wizardScreenHolder.getActionMap().put("quit", getCancelAction());
    }

    // TODO Bind the ENTER key to a Next/Finish/Apply event to speed up data entry through keyboard
    //wizardPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "next");
    //wizardPanel.getActionMap().put("next", getNextAction(null));

    // Populate based on the current locale
    populateWizardViewMap(wizardViewMap);

    // Once all the views are created allow events to occur
    for (Map.Entry<String, AbstractWizardPanelView> entry : wizardViewMap.entrySet()) {

      // Ensure the panel is in the correct starting state
      entry.getValue().fireInitialStateViewEvents();

    }

    // Ensure the wizard panel has size
    wizardScreenHolder.setMinimumSize(new Dimension(MultiBitUI.WIZARD_MIN_WIDTH, MultiBitUI.WIZARD_MIN_HEIGHT));
    wizardScreenHolder.setPreferredSize(new Dimension(MultiBitUI.WIZARD_MIN_WIDTH, MultiBitUI.WIZARD_MIN_HEIGHT));
    wizardScreenHolder.setSize(new Dimension(MultiBitUI.WIZARD_MIN_WIDTH, MultiBitUI.WIZARD_MIN_HEIGHT));

    // Show the panel specified by the initial state
    show(wizardModel.getPanelName());

  }

  /**
   * <p>Show the named panel</p>
   *
   * @param name The panel name
   */
  public void show(String name) {

    Preconditions.checkState(wizardViewMap.containsKey(name), "'" + name + "' is not a valid panel name");

    final AbstractWizardPanelView wizardPanelView = wizardViewMap.get(name);

    if (!wizardPanelView.isInitialised()) {

      // Initialise the wizard screen panel and add it to the card layout parent
      wizardScreenHolder.add(wizardPanelView.getWizardScreenPanel(true), name);

    }

    // De-register any existing default buttons from previous panels
    wizardPanelView.deregisterDefaultButton();

    // Provide warning that the panel is about to be shown
    if (wizardPanelView.beforeShow()) {

      // No abort so show
      cardLayout.show(wizardScreenHolder, name);

      wizardPanelView.afterShow();
    }

  }

  /**
   * <p>Hide the wizard</p>
   *
   * @param name         The panel name
   * @param isExitCancel True if this hide operation comes from an exit or cancel
   */
  public void hide(String name, boolean isExitCancel) {

    Preconditions.checkState(wizardViewMap.containsKey(name), "'" + name + "' is not a valid panel name");

    final AbstractWizardPanelView wizardPanelView = wizardViewMap.get(name);

    // Provide warning that the panel is about to be shown
    if (wizardPanelView.beforeHide(isExitCancel)) {

      // De-register
      wizardPanelView.deregisterDefaultButton();

      // No abort so hide
      Panels.hideLightBox();

      // Issue the wizard hide event
      ViewEvents.fireWizardHideEvent(name, wizardModel, isExitCancel);

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

        if (getWizardModel().isDirty()) {

          // Check with the user about throwing away their data (handle the outcome with a WizardPopoverHideEvent)
          Panels.showLightBoxPopover(
            Popovers.newEnterYesNoPopoverMaV(getWizardModel().getPanelName())
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
   * @return The "recover" action based on the model state
   */
  public <P> Action getRestoreAction(final AbstractWizardPanelView<M, P> wizardView) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Ensure the panel updates its model (the button is outside of the panel itself)
        wizardView.updateFromComponentModels(Optional.absent());

        // Aggregate the panel information into the wizard model

        // Move to the recover state (equivalent to next)
        wizardModel.showNext();

        // Show the panel based on the state
        show(wizardModel.getPanelName());
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


}
