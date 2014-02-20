package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardButtonEnabledEvent;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;

import javax.swing.*;

/**
 * <p>Abstract base class providing the following to wizard panel views:</p>
 * <ul>
 * <li>Standard methods common to wizard panel views</li>
 * </ul>
 * <p>A wizard panel view contains the title, components and buttons. It relies on
 * its implementers to provide the panel containing the specific components for the
 * user interaction.</p>
 *
 * @param <W> the wizard model
 * @param <P> the panel model
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractWizardPanelView<W extends WizardModel, P> {

  private final W wizardModel;

  private Optional<P> panelModel;

  private JPanel wizardPanel;

  // Buttons
  private Optional<JButton> exitButton = Optional.absent();
  private Optional<JButton> cancelButton = Optional.absent();
  private Optional<JButton> nextButton = Optional.absent();
  private Optional<JButton> previousButton = Optional.absent();
  private Optional<JButton> finishButton = Optional.absent();

  private final String panelName;

  /**
   * @param wizardModel The wizard model managing the states
   * @param panelName   The panel name to filter events from components
   * @param title       The key to the main title of the wizard panel
   */
  public AbstractWizardPanelView(W wizardModel, String panelName, MessageKey title) {

    Preconditions.checkNotNull(wizardModel, "'wizardModel' must be present");
    Preconditions.checkNotNull(title, "'title' must be present");

    this.wizardModel = wizardModel;
    this.panelName = panelName;

    // All wizard views can receive events
    CoreServices.uiEventBus.register(this);

    // All wizard panels are decorated with the same theme and layout at creation
    // so just need a vanilla panel to begin with
    wizardPanel = Panels.newRoundedPanel();

    // All wizard panels require a backing model
    newPanelModel();

    // Create a new wizard panel and apply the wizard theme
    PanelDecorator.applyWizardTheme(wizardPanel, newWizardViewPanel(), title);

  }

  /**
   * @return The wizard model providing aggregated state information
   */
  public W getWizardModel() {
    return wizardModel;
  }

  /**
   * @return The panel model specific to this view
   */
  public Optional<P> getPanelModel() {
    return panelModel;
  }

  /**
   * @return The panel name associated with this view
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The wizard panel (title, wizard components, buttons)
   */
  public JPanel getWizardPanel() {
    return wizardPanel;
  }

  /**
   * <p>Called when the wizard is first created to initialise the panel model and subsequently on a locale change event.</p>
   *
   * <p>This is called before {@link AbstractWizardPanelView#newWizardViewPanel()}</p>
   *
   * <p>Implementers must create a new panel model and bind it to the overall wizard</p>
   */
  public abstract void newPanelModel();

  /**
   * <p>Called when the wizard is first created to initialise the panel and subsequently on a locale change event.</p>
   *
   * <p>This is called after {@link AbstractWizardPanelView#newPanelModel()}</p>
   *
   * <p>Implementers must create a new panel</p>
   *
   * @return A new panel containing the data components specific to this wizard view (e.g. language selector or seed phrase display)
   */
  public abstract JPanel newWizardViewPanel();

  /**
   * @param panelModel The panel model
   */
  public void setPanelModel(P panelModel) {
    this.panelModel = Optional.fromNullable(panelModel);
  }

  /**
   * <p>Update the view with any required view events to create a clean initial state (all initialisation will have completed)</p>
   *
   * <p>Default implementation is to disable the "next" button</p>
   */
  public void fireInitialStateViewEvents() {

    // Default is to disable the Next button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);

  }

  /**
   * @return The "exit" button for this view
   */
  public JButton getExitButton() {
    return exitButton.get();
  }

  public void setExitButton(JButton exitButton) {
    this.exitButton = Optional.fromNullable(exitButton);
  }

  /**
   * @return The "cancel" button for this view
   */
  public JButton getCancelButton() {
    return cancelButton.get();
  }

  public void setCancelButton(JButton cancelButton) {
    this.cancelButton = Optional.fromNullable(cancelButton);
  }

  /**
   * @return The "next" button for this view
   */
  public JButton getNextButton() {
    return nextButton.get();
  }

  public void setNextButton(JButton nextButton) {
    this.nextButton = Optional.fromNullable(nextButton);
  }

  /**
   * @return The "previous" button for this view
   */
  public JButton getPreviousButton() {
    return previousButton.get();
  }

  public void setPreviousButton(JButton previousButton) {
    this.previousButton = Optional.fromNullable(previousButton);
  }

  /**
   * @return The "finish" button for this view
   */
  public JButton getFinishButton() {
    return finishButton.get();
  }

  public void setFinishButton(JButton finishButton) {
    this.finishButton = Optional.fromNullable(finishButton);
  }

  /**
   * <p>Called before this wizard panel is about to be shown</p>
   *
   * <p>Typically this is where a panel view would reference the wizard model to obtain earlier values for display</p>
   *
   * @return True if the panel can be shown, false if the show operation should be aborted
   */
  public boolean beforeShow() {

    // Default is to return OK
    return true;

  }

  /**
   * <p>Called after this wizard panel has been shown</p>
   *
   * <p>Typically this is where a panel view would attempt to set the focus for its primary component using
   * the Swing thread as follows:</p>
   *
   * <pre>
   * SwingUtilities.invokeLater(new Runnable() {
   *
   * {@literal @}Override public void run() {
   *   getCancelButton().requestFocusInWindow();
   * }
   *
   * });
   *
   * </pre>
   */
  public void afterShow() {

    // Do nothing

  }

  /**
   * <p>Called when a wizard state transition occurs (e.g. "next" button click) and in response to a {@link org.multibit.hd.ui.events.view.ComponentChangedEvent}</p>
   *
   * <p>Implementers must:</p>
   * <ol>
   * <li>Update their panel model to reflect the component models (unless there is a direct reference)</li>
   * <li>Update the wizard model if the panel model data is valid</li>
   * </ol>
   *
   * @param componentModel The component model (
   */
  public abstract void updateFromComponentModels(Optional componentModel);

  /**
   * <p>React to a "wizard button enable" event</p>
   *
   * @param event The wizard button enable event
   */
  @Subscribe
  public void onWizardButtonEnabled(WizardButtonEnabledEvent event) {

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(panelName, "'panelName' must be present");

    // Is the event applicable?
    if (!event.getPanelName().equals(panelName)) {
      return;
    }

    // Enable the button if present
    switch (event.getWizardButton()) {
      case CANCEL:
        if (cancelButton.isPresent()) {
          cancelButton.get().setEnabled(event.isEnabled());
        }
        break;
      case EXIT:
        if (exitButton.isPresent()) {
          exitButton.get().setEnabled(event.isEnabled());
        }
        break;
      case NEXT:
        if (nextButton.isPresent()) {
          nextButton.get().setEnabled(event.isEnabled());
        }
        break;
      case PREVIOUS:
        if (previousButton.isPresent()) {
          previousButton.get().setEnabled(event.isEnabled());
        }
        break;
      case FINISH:
        if (finishButton.isPresent()) {
          finishButton.get().setEnabled(event.isEnabled());
        }
        break;
    }

  }

  /**
   * <p>React to a "component model changed" event</p>
   *
   * @param event The wizard button enable event
   */
  @Subscribe
  public void onWizardComponentModelChangedEvent(ComponentChangedEvent event) {

    if (panelName.equals(event.getPanelName())) {

      // Default behaviour is to update
      updateFromComponentModels(event.getComponentModel());

    }

  }
}
