package org.multibit.hd.ui.views.screens;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.events.view.ThemeChangedEvent;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.util.Map;

/**
 * <p>Abstract base class to provide the following to UI:</p>
 * <ul>
 * <li>Provision of common methods to detail views</li>
 * </ul>
 *
 * @param <M> The detail model
 *
 * @since 0.0.1
 */
public abstract class AbstractScreen<M extends ScreenModel> {

  private final JPanel detailPanel;
  private final M detailModel;

  /**
   * @param detailModel The detail data model containing the aggregate information of all components
   */
  protected AbstractScreen(M detailModel) {

    Preconditions.checkNotNull(detailModel, "'model' must be present");

    this.detailModel = detailModel;

    CoreServices.uiEventBus.register(this);

    detailPanel = Panels.newPanel();

    // Use current locale for initial creation
    onLocaleChangedEvent(new LocaleChangedEvent());

  }

  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    // Clear out any existing components
    detailPanel.removeAll();

    // Invalidate for new layout
    Panels.invalidate(detailPanel);

  }

  @Subscribe
  public void onThemeChangedEvent(ThemeChangedEvent event) {

    onLocaleChangedEvent(null);

  }

  /**
   * <p>Add fresh content to the wizard view map</p>
   * <p>The map will be empty whenever this is called</p>
   */
  protected abstract void populateWizardViewMap(Map<String, AbstractScreenView> wizardViewMap);

  /**
   * <p>Close the wizard</p>
   */
  public void close() {

    Panels.hideLightBox();

  }

  /**
   * <p>Show the named panel</p>
   */
  public void show(String name) {

  }

  /**
   * @return The wizard panel
   */
  public JPanel getDetailPanel() {
    return detailPanel;
  }

}
