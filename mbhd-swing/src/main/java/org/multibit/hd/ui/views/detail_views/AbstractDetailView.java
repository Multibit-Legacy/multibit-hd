package org.multibit.hd.ui.views.detail_views;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.WizardComponentModelChangedEvent;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;

/**
 * <p>Abstract base class providing the following to detail views:</p>
 * <ul>
 * <li>Standard methods common to details views</li>
 * </ul>
 * <p>A detail view contains the title and components. It relies on
 * its implementers to provide the panel containing the specific components for the
 * user interaction.</p>
 *
 * @param <M> The detail model
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractDetailView<M extends DetailModel> {

  private final M detailModel;

  private JPanel detailPanel;

  private final DetailView detailView;

  /**
   * @param detailModel The wizard model managing the states
   * @param detailView   The panel name to filter events from components
   * @param title       The key to the main title of the wizard panel
   */
  public AbstractDetailView(M detailModel, DetailView detailView, MessageKey title) {

    Preconditions.checkNotNull(detailModel, "'detailModel' must be present");
    Preconditions.checkNotNull(detailView, "'detailView' must be present");
    Preconditions.checkNotNull(title, "'title' must be present");

    this.detailModel = detailModel;
    this.detailView = detailView;

    // All detail views can receive events
    CoreServices.uiEventBus.register(this);

    // All detail panels are decorated with the same theme and layout at creation
    // so just need a vanilla panel to begin with
    detailPanel = Panels.newPanel();

    // All detail panels require a backing model
    newDetailModel();

    // Create a new detail panel and apply the detail theme
    PanelDecorator.applyWizardTheme(detailPanel, newDetailViewPanel(), title);

  }

  /**
   * @return The wizard model providing aggregated state information
   */
  public M getDetailModel() {
    return detailModel;
  }

  /**
   * @return The detail view
   */
  public DetailView getDetailView() {
    return detailView;
  }

  /**
   * @return The wizard panel (title, wizard components, buttons)
   */
  public JPanel getDetailPanel() {
    return detailPanel;
  }

  /**
   * <p>Called when the detail view is first created to initialise the model and subsequently on a locale change event.</p>
   *
   * <p>This is called before {@link AbstractDetailView#newDetailViewPanel()}</p>
   *
   * <p>Implementers must create a new panel model and bind it to the overall detail view</p>
   */
  public abstract void newDetailModel();

  /**
   * <p>Called when the wizard is first created to initialise the panel and subsequently on a locale change event.</p>
   *
   * <p>This is called after {@link AbstractDetailView#newDetailModel()}</p>
   *
   * <p>Implementers must create a new panel</p>
   *
   * @return A new panel containing the data components specific to this detail view (e.g. contacts or transactions)
   */
  public abstract JPanel newDetailViewPanel();

  /**
   * <p>Update the view with any required view events to create a clean initial state (all initialisation will have completed)</p>
   *
   * <p>Default implementation is to do nothing</p>
   */
  public void fireInitialStateViewEvents() {

    // Do nothing

  }

  /**
   * <p>Called before this detail view is about to be shown</p>
   *
   * <p>Typically this is where a detail view would reference the detail model to obtain latest values for display</p>
   *
   * @return True if the panel can be shown, false if the show operation should be aborted
   */
  public boolean beforeShow() {

    return true;

  }

  /**
   * <p>React to a "component model changed" event</p>
   *
   * @param event The wizard button enable event
   */
  @Subscribe
  public void onDetailComponentModelChangedEvent(WizardComponentModelChangedEvent event) {

  }

}
