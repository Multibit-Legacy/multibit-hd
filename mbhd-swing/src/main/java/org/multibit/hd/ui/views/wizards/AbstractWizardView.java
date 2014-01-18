package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.WizardButtonEnabledEvent;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;

/**
 * <p>Wizard panel to provide the following to UI:</p>
 * <ul>
 * <li>Welcome users to the application and allow them to select a language</li>
 * </ul>
 *
 * @param <W> the wizard model
 * @param <P> the panel model
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractWizardView<W extends WizardModel, P> {

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
  public AbstractWizardView(W wizardModel, String panelName, MessageKey title) {

    Preconditions.checkNotNull(wizardModel, "'wizardModel' must be present");
    Preconditions.checkNotNull(title, "'title' must be present");

    this.wizardModel = wizardModel;
    this.panelName = panelName;

    // All wizard views can receive events
    CoreServices.uiEventBus.register(this);

    // All wizard panels are decorated with the same theme and layout at creation
    // so just need a vanilla panel to begin with
    wizardPanel = Panels.newPanel();

    PanelDecorator.applyWizardTheme(wizardPanel, newDataPanel(), title);

  }

  /**
   * @return The panel name associated with this view
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The wizard model providing aggregated state information
   */
  public W getWizardModel() {
    return wizardModel;
  }

  /**
   * @return The panel model
   */
  public Optional<P> getPanelModel() {
    return panelModel;
  }

  /**
   * @param panelModel The panel model
   */
  public void setPanelModel(P panelModel) {
    this.panelModel = Optional.fromNullable(panelModel);
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
   * @return The wizard panel (title, wizard components, buttons)
   */
  public JPanel getWizardPanel() {
    return wizardPanel;
  }

  /**
   * @return A new panel containing the data components specific to this wizard view (e.g. language selector or seed phrase display)
   */
  public abstract JPanel newDataPanel();

  /**
   * Update the view with any required view events (all initialisation will have completed)
   */
  public abstract void fireViewEvents();

  /**
   * Update the panel data model with the contents of the panel view components (if necessary)
   *
   * Called when the Next and Previous buttons are clicked and in response to a ComponentModelChangedEvent
   *
   * @return True if the panel update has triggered an update to the wizard model
   */
  public abstract boolean updatePanelModel();

  /**
   * React to a "wizard button enable" event
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
}
