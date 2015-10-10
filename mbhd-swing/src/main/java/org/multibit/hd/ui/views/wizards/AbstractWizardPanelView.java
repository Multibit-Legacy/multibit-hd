package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.EnvironmentEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardButtonEnabledEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_environment_alert.DisplayEnvironmentAlertModel;
import org.multibit.hd.ui.views.components.display_environment_alert.DisplayEnvironmentAlertView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

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
 * @param <P> the wizard panel model
 *
 * @since 0.0.1
 */
public abstract class AbstractWizardPanelView<M extends AbstractWizardModel, P> {

  /**
   * Avoid sharing this logger since the naming becomes confusing
   */
  private static final Logger log = LoggerFactory.getLogger(AbstractWizardPanelView.class);

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
  private Optional<P> panelModel = Optional.absent();

  /**
   * The wizard screen panel (title, contents, buttons)
   */
  private JPanel wizardScreenPanel;

  /**
   * The content panel with the specific components for data entry/review
   */
  private JPanel contentPanel;

  /**
   * True if the components making up this screen have been created
   */
  private boolean hasComponents = false;

  /**
   * True if the contents making up this screen have been populated
   */
  private boolean initialised = false;

  // Components
  private List<ModelAndView> components = Lists.newArrayList();

  // Buttons
  private Optional<JButton> exitButton = Optional.absent();
  private Optional<JButton> cancelButton = Optional.absent();
  private Optional<JButton> nextButton = Optional.absent();
  private Optional<JButton> previousButton = Optional.absent();
  private Optional<JButton> createButton = Optional.absent();
  private Optional<JButton> restoreButton = Optional.absent();
  private Optional<JButton> finishButton = Optional.absent();
  private Optional<JButton> applyButton = Optional.absent();

  // Labels
  /**
   * The title label in case it needs to be modified after initialisation (e.g. changing a hardware wallet)
   */
  protected JLabel title;

  /**
   * @param wizard         The wizard
   * @param panelName      The panel name to filter events from components
   * @param backgroundIcon The icon for the content section background
   * @param titleKey       The key for the title section text
   * @param values         The values for the title key
   */
  public AbstractWizardPanelView(
    AbstractWizard<M> wizard,
    String panelName,
    AwesomeIcon backgroundIcon,
    MessageKey titleKey,
    Object... values) {

    Preconditions.checkNotNull(wizard, "'wizard' must be present");
    Preconditions.checkNotNull(titleKey, "'title' must be present");

    this.wizardModel = wizard.getWizardModel();
    this.panelName = panelName;

    // All wizard panel views can receive Core and View events
    ViewEvents.subscribe(this);
    CoreEvents.subscribe(this);

    // All wizard screen panels are decorated with the same theme and
    // layout at creation so just need a simple panel to begin with
    wizardScreenPanel = Panels.newRoundedPanel();

    // All wizard panels require a backing model
    newPanelModel();

    // Create a new wizard panel and apply the wizard theme
    PanelDecorator.applyWizardTheme(wizardScreenPanel);

    // Add the title to the wizard
    title = Labels.newTitleLabel(titleKey, values);
    wizardScreenPanel.add(title, "span 4," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",gap 0, shrink 200,aligny top,align center,h 90lp!,wrap");

    // Provide a basic empty content panel (allows lazy initialisation later)
    contentPanel = Panels.newDetailBackgroundPanel(backgroundIcon);

    // Add it to the wizard panel as a placeholder
    wizardScreenPanel.add(contentPanel, "span 4,grow,push,wrap");

    // Add the buttons to the wizard
    initialiseButtons(wizard);

  }

  /**
   * <p>The wizard is closing so unsubscribe</p>
   */
  public void unsubscribe() {

    ViewEvents.unsubscribe(this);
    CoreEvents.unsubscribe(this);

  }

  /**
   * <p>Called when the wizard is first created to initialise the panel model.</p>
   *
   * <p>Implementers must create a new panel model and bind it to the overall wizard</p>
   */
  public abstract void newPanelModel();

  /**
   * <p>Initialise the content section of the wizard panel just before first showing</p>
   * <p>Implementers should set the layout and populate the components</p>
   * <h3>Example panel creation</h3>
   * <pre>
   * ... initialise components ...
   *
   * getComponents().add(aComponent);
   *
   * contentPanel.setLayout(new MigLayout(
   *   Panels.migXYLayout(),
   *   "[][]", // Column constraints
   *   "[]10[]10[][][]10[][]" // Row constraints
   * ));
   *
   * ... populate panel ...
   * </pre>
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
   * @param mavs The ModelAndView instances to register as potentially containing UI event handlers
   */
  public void registerComponents(ModelAndView... mavs) {

    Preconditions.checkNotNull(mavs, "'mavs' must be present");

    // Add one by one to verify references
    for (ModelAndView mav : mavs) {

      Preconditions.checkNotNull(mav, "'mav' must be present");
      components.add(mav);

    }

  }

  /**
   * <p>Reduced visibility since this is only required during the wizard hide process</p>
   *
   * @return The list of {@link ModelAndView} entries for this view to allow the deregister of UI events. Can be empty, but never null.
   */
  /* package local */ List<ModelAndView> getComponents() {
    return components;
  }

  /**
   * @return The wizard model providing aggregated state information
   */
  public M getWizardModel() {
    return wizardModel;
  }

  /**
   * @return The panel model specific to this view. Never null.
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

        // At this point the user cannot have made changes
        getWizardModel().setDirty(false);

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
    if (previousButton.isPresent()) {
      return previousButton.get();
    } else {
      return null;
    }
  }

  public void setPreviousButton(JButton previousButton) {
    this.previousButton = Optional.fromNullable(previousButton);
  }

  /**
   * @return The "restore" button for this view
   */
  public JButton getRestoreButton() {
    return restoreButton.get();
  }

  public void setRestoreButton(JButton recoverButton) {
    this.restoreButton = Optional.fromNullable(recoverButton);
  }

  /**
   * @return The "create" button for this view
   */
  public JButton getCreateButton() {
    return createButton.get();
  }

  public void setCreateButton(JButton createButton) {
    this.createButton = Optional.fromNullable(createButton);
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
   * <p>This method is guaranteed to run on the EDT</p>
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
   * <p>To set focus to a primary component use this construct:</p>
   *
   * <pre>
   * getCancelButton().requestFocusInWindow();
   * </pre>
   *
   * <p>This method is guaranteed to run on the EDT</p>
   */
  public void afterShow() {

    // Do nothing

  }

  /**
   * <p>Standard handling for environment popovers</p>
   *
   * @param displayEnvironmentPopoverMaV The display environment popover MaV
   */
  protected void checkForEnvironmentEventPopover(ModelAndView<DisplayEnvironmentAlertModel, DisplayEnvironmentAlertView> displayEnvironmentPopoverMaV) {

    // Check for any environment alerts
    Optional<EnvironmentEvent> environmentEvent = CoreServices.getApplicationEventService().getLatestEnvironmentEvent();

    if (environmentEvent.isPresent()) {

      log.debug("Showing environment event popover");

      // Provide the event as the model
      displayEnvironmentPopoverMaV.getModel().setValue(environmentEvent.get());

      // Show the environment alert as a popover
      JPanel popoverPanel = displayEnvironmentPopoverMaV.getView().newComponentPanel();

      // Potentially decorate the panel (or do nothing)
      switch (environmentEvent.get().getSummary().getAlertType()) {
        case DEBUGGER_ATTACHED:
          popoverPanel.add(Panels.newDebuggerWarning(), "align center,wrap");
          break;
        case UNSUPPORTED_FIRMWARE_ATTACHED:
          popoverPanel.add(Panels.newUnsupportedFirmware(), "align center,wrap");
          break;
        case DEPRECATED_FIRMWARE_ATTACHED:
          popoverPanel.add(Panels.newDeprecatedFirmware(), "align center,wrap");
          break;
        case UNSUPPORTED_CONFIGURATION_ATTACHED:
          popoverPanel.add(Panels.newUnsupportedConfigurationPassphrase(), "align center,wrap");
          break;
        default:
          // Do nothing
          return;
      }

      // Check for an existing lightbox popover
      if (!Panels.isLightBoxPopoverShowing()) {
        // Show the popover
        Panels.showLightBoxPopover(popoverPanel);
      }

      // Discard the environment event now that the user is aware (this prevents multiple showings)
      CoreServices.getApplicationEventService().onEnvironmentEvent(null);

    }
  }

  /**
   * <p>Called before this wizard is about to be hidden.</p>
   *
   * <p>Typically this is where a panel view would {@link #updateFromComponentModels}, but implementations will vary</p>
   *
   * <p>This method is guaranteed to run on the EDT</p>
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
   * <p>This method is guaranteed to run on the EDT</p>
   *
   * @param componentModel The component model (
   */
  public abstract void updateFromComponentModels(Optional componentModel);

  /**
   * <p>Deregisters the default button (called automatically when the wizard closes)</p>
   */
  public void deregisterDefaultButton() {

    Panels.getApplicationFrame().getRootPane().setDefaultButton(null);

  }

  /**
   * <p>Registers the default button for the wizard panel. Use this method with caution since it may give unintended side effects that affect the user experience.</p>
   *
   * @param button The button to use as the default (triggered on an "ENTER" key release)
   */
  public void registerDefaultButton(JButton button) {

    Panels.getApplicationFrame().getRootPane().setDefaultButton(button);

    // Remove the binding for pressed
    Panels.getApplicationFrame().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
      .put(KeyStroke.getKeyStroke("ENTER"), "none");

    // Target the binding for released
    Panels.getApplicationFrame().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
      .put(KeyStroke.getKeyStroke("released ENTER"), "press");

    if (button.getText().equalsIgnoreCase(Languages.safeText(MessageKey.EXIT))) {
      NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), button);
    } else {
      NimbusDecorator.applyThemeColor(Themes.currentTheme.buttonDefaultBackground(), button);
    }

  }

  /**
   * @return True if this wizard panel has been initialised (lazy loading needs this)
   */
  public boolean isInitialised() {
    return initialised;
  }

  public void setInitialised(boolean initialised) {
    this.initialised = initialised;
  }

  /**
   * @return True if the components are all non-null (early events against uninitialised views need this to filter)
   */
  public boolean hasComponents() {
    return hasComponents;
  }

  public void setHasComponents(boolean hasComponents) {
    this.hasComponents = hasComponents;
  }

  /**
   * <p>React to a "wizard button enable" event</p>
   *
   * @param event The wizard button enable event
   */
  @Subscribe
  public void onWizardButtonEnabled(final WizardButtonEnabledEvent event) {

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(panelName, "'panelName' must be present");

    // Is the event applicable?
    if (!event.getPanelName().equals(panelName)) {
      return;
    }

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
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
            case CREATE:
              if (createButton.isPresent()) {
                createButton.get().setEnabled(event.isEnabled());
              }
              break;
            default:
              // No dothing
          }

        }
      });


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
