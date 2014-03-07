package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardButtonEnabledEvent;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;

/**
 * <p>Abstract base class providing the following to wizard panel views:</p>
 * <ul>
 * <li>Standard methods common to wizard panel views</li>
 * </ul>
 * <p>A wizard panel view contains three sections: title, content and buttons. It relies on
 * its implementers to provide the panel containing the specific components for the
 * user interaction.</p>
 *
 * @param <M> the wizard model
 * @param <P> the panel model
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractWizardPanelView<M extends WizardModel, P> {

  /**
   * The overall wizard model
   */
  private final M wizardModel;

  /**
   * The panel name to identify this panel and filter events
   */
  private final String panelName;

  /**
   * The optional panel model (some panels are read only views)
   */
  private Optional<P> panelModel;

  /**
   * The wizard screen panel (title, contents, buttons)
   */
  private JPanel wizardScreenPanel;

  /**
   * The content panel with the specific components for data entry/review
   */
  private JPanel contentPanel;

  /**
   * True if the contents making up this screen have been populated
   */
  private boolean initialised = false;

  // Buttons
  private Optional<JButton> exitButton = Optional.absent();
  private Optional<JButton> cancelButton = Optional.absent();
  private Optional<JButton> nextButton = Optional.absent();
  private Optional<JButton> previousButton = Optional.absent();
  private Optional<JButton> restoreButton = Optional.absent();
  private Optional<JButton> finishButton = Optional.absent();
  private Optional<JButton> applyButton = Optional.absent();

  /**
   * @param wizard         The wizard
   * @param panelName      The panel name to filter events from components
   * @param title          The key for the title section text
   * @param backgroundIcon The icon for the content section background
   */
  public AbstractWizardPanelView(AbstractWizard<M> wizard, String panelName, MessageKey title, AwesomeIcon backgroundIcon) {

    Preconditions.checkNotNull(wizard, "'wizard' must be present");
    Preconditions.checkNotNull(title, "'title' must be present");

    this.wizardModel = wizard.getWizardModel();
    this.panelName = panelName;

    // All wizard views can receive events
    CoreServices.uiEventBus.register(this);

    // All wizard screen panels are decorated with the same theme and
    // layout at creation so just need a simple panel to begin with
    wizardScreenPanel = Panels.newRoundedPanel();

    // All wizard panels require a backing model
    newPanelModel();

    // Create a new wizard panel and apply the wizard theme
    PanelDecorator.applyWizardTheme(wizardScreenPanel);

    // Add the title to the wizard
    initialiseTitle(wizardScreenPanel, title);

    // Provide a basic empty content panel (allows lazy initialisation later)
    contentPanel = Panels.newDetailBackgroundPanel(backgroundIcon);

    // Add it to the wizard panel as a placeholder
    wizardScreenPanel.add(contentPanel, "span 4,grow,wrap");

    // Add the buttons to the wizard
    initialiseButtons(wizard);

  }

  /**
   * <p>Called when the wizard is first created to initialise the panel model.</p>
   *
   * <p>This is called before {@link AbstractWizardPanelView#initialiseTitle(javax.swing.JPanel, org.multibit.hd.ui.i18n.MessageKey)} ()}</p>
   *
   * <p>Implementers must create a new panel model and bind it to the overall wizard</p>
   */
  public abstract void newPanelModel();

  /**
   * <p>Initialise the title section of the wizard panel</p>
   *
   * @param wizardScreenPanel The wizard screen panel (title, contents, buttons)
   * @param titleKey          The title key to add to the panel
   */
  protected void initialiseTitle(JPanel wizardScreenPanel, MessageKey titleKey) {

    wizardScreenPanel.add(Labels.newTitleLabel(titleKey), "span 4,shrink,wrap,aligny top");

  }

  /**
   * <p>Initialise the content section of the wizard panel just before first showing</p>
   * <p>Implementers should set the layout and populate the components</p>
   *
   * @param contentPanel The empty content panel with the current theme and initial background icon
   */
  public abstract void initialiseContent(JPanel contentPanel);

  /**
   * <p>Initialise the content section of the wizard panel</p>
   * <p>Implementers should use <code>PanelDecorator</code> to add buttons</p>
   *
   * @param wizard The wizard providing exit/cancel information for button selection
   */
  protected abstract void initialiseButtons(AbstractWizard<M> wizard);

  /**
   * @return The wizard model providing aggregated state information
   */
  public M getWizardModel() {
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
   * <p>Get the overall wizard screen panel (title, content, buttons) lazily initialising the content as necessary</p>
   *
   * @param initialiseContent True if the wizard screen content should be initialised
   *
   * @return The wizard panel
   */
  public JPanel getWizardScreenPanel(boolean initialiseContent) {

    if (initialiseContent) {
      if (!isInitialised()) {

        initialiseContent(contentPanel);

        setInitialised(true);

      }
    }

    return wizardScreenPanel;
  }

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
   * @return The "recover" button for this view
   */
  public JButton getRestoreButton() {
    return restoreButton.get();
  }

  public void setRestoreButton(JButton recoverButton) {
    this.restoreButton = Optional.fromNullable(recoverButton);
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
   * @return The "apply" button for this view
   */
  public JButton getApplyButton() {
    return applyButton.get();
  }

  public void setApplyButton(JButton applyButton) {
    this.applyButton = Optional.fromNullable(applyButton);
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
   * <p>Typically this is where a panel view would attempt to:</p>
   * <ul>
   * <li>set the focus for its primary component using (see later)</li>
   * <li>register a default button to speed up keyboard data entry</li>
   * </ul>
   *
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
   * <p>Called before this wizard panel is about to be hidden</p>
   *
   * <p>Typically this is where a panel view would {@link #updateFromComponentModels}, but implementations will vary</p>
   *
   * @param isExitCancel True if this hide action comes from a exit or cancel operation
   *
   * @return True if the panel can be hidden, false if the hide operation should be aborted (perhaps due to a data error)
   */
  public boolean beforeHide(boolean isExitCancel) {

    // Default is to return OK
    return true;

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
   * <p>Deregisters the default button (called automatically when the wizard closes)</p>
   */
  public void deregisterDefaultButton() {

    //   Panels.frame.getRootPane().setDefaultButton(null);

  }

  /**
   * <p>Registers the default button for the wizard panel. Use this method with caution since it may give unintended side effects that affect the user experience.</p>
   *
   * @param button The button to use as the default (triggered on an "ENTER" key release)
   */
  public void registerDefaultButton(JButton button) {

    Panels.frame.getRootPane().setDefaultButton(button);

    // Remove the binding for pressed
    Panels.frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
      .put(KeyStroke.getKeyStroke("ENTER"), "none");

    // Target the binding for released
    Panels.frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
      .put(KeyStroke.getKeyStroke("released ENTER"), "press");

  }

  /**
   * @return True if this wizard panel has been initialised
   */
  public boolean isInitialised() {
    return initialised;
  }

  public void setInitialised(boolean initialised) {
    this.initialised = initialised;
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
      case APPLY:
        if (applyButton.isPresent()) {
          applyButton.get().setEnabled(event.isEnabled());
        }
        break;
      case RESTORE:
        if (restoreButton.isPresent()) {
          restoreButton.get().setEnabled(event.isEnabled());
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
