package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.api.MessageKey;
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

  /**
   * @param wizardModel The wizard model managing the states
   * @param title       The key to the main title of the wizard panel
   */
  public AbstractWizardView(W wizardModel, MessageKey title) {

    Preconditions.checkNotNull(wizardModel, "'wizardModel' must be present");
    Preconditions.checkNotNull(title, "'title' must be present");

    this.wizardModel = wizardModel;

    // All wizard panels are decorated with the same theme at creation
    wizardPanel = Panels.newPanel();
    PanelDecorator.applyWizardTheme(wizardPanel, newDataPanel(), title);

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
   * Update the panel data model with the contents of the panel view components (if necessary)
   */
  public abstract void updatePanelModel();

}
