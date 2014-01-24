package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardButtonEnabledEvent;
import org.multibit.hd.ui.events.view.WizardModelChangedEvent;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.CONFIRM_WALLET_SEED_PHRASE;

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
  private JButton exitButton;
  private JButton cancelButton;
  private JButton nextButton;
  private JButton previousButton;
  private JButton finishButton;

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
    wizardPanel = Panels.newPanel();

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
   * <p>Called when the wizard is first created to initialise the panel and subsequently on a locale change event.</p>
   *
   * <p>Implementers must create a new panel</p>
   *
   * @return A new panel containing the data components specific to this wizard view (e.g. language selector or seed phrase display)
   */
  public abstract JPanel newWizardViewPanel();

  /**
   * <p>Called when the Next and Previous buttons are clicked and in response to a ComponentModelChangedEvent</p>
   *
   * <p>Implementers must:</p>
   * <ol>
   * <li>update the panel model to reflect the component models (unless there is a direct reference)</li>
   * <li>update the wizard model if the panel model data is valid</li>
   * </ol>
   */
  public abstract void updateFromComponentModels();

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
    ViewEvents.fireWizardButtonEnabledEvent(CONFIRM_WALLET_SEED_PHRASE.name(), WizardButton.NEXT, false);

  }

  /**
   * @return The "exit" button for this view
   */
  public JButton getExitButton() {
    return exitButton;
  }

  public void setExitButton(JButton exitButton) {
    this.exitButton = exitButton;
  }

  /**
   * @return The "cancel" button for this view
   */
  public JButton getCancelButton() {
    return cancelButton;
  }

  public void setCancelButton(JButton cancelButton) {
    this.cancelButton = cancelButton;
  }

  /**
   * @return The "next" button for this view
   */
  public JButton getNextButton() {
    return nextButton;
  }

  public void setNextButton(JButton nextButton) {
    this.nextButton = nextButton;
  }

  /**
   * @return The "previous" button for this view
   */
  public JButton getPreviousButton() {
    return previousButton;
  }

  public void setPreviousButton(JButton previousButton) {
    this.previousButton = previousButton;
  }

  /**
   * @return The "finish" button for this view
   */
  public JButton getFinishButton() {
    return finishButton;
  }

  public void setFinishButton(JButton finishButton) {
    this.finishButton = finishButton;
  }

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

    // Enable the button (model should only reference actual buttons)
    switch (event.getWizardButton()) {
      case CANCEL:
        cancelButton.setEnabled(event.isEnabled());
        break;
      case EXIT:
        exitButton.setEnabled(event.isEnabled());
        break;
      case NEXT:
        nextButton.setEnabled(event.isEnabled());
        break;
      case PREVIOUS:
        previousButton.setEnabled(event.isEnabled());
        break;
      case FINISH:
        finishButton.setEnabled(event.isEnabled());
        break;
    }

  }

  /**
   * <p>Updates the panel components with fresh wizard model contents (such as when a previous panel affects this one)</p>
   *
   * @param event The "wizard model changed" event
   */
  @Subscribe
  public void onWizardModelChangedEvent(WizardModelChangedEvent event) {

    // Do nothing

  }

}
