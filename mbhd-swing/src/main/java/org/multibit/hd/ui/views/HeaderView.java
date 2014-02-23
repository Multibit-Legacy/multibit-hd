package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.controller.RemoveAlertEvent;
import org.multibit.hd.ui.events.view.AlertAddedEvent;
import org.multibit.hd.ui.events.view.BalanceChangedEvent;
import org.multibit.hd.ui.events.view.LocaleChangedEvent;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the balance display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HeaderView {

  private final ModelAndView<DisplayAmountModel, DisplayAmountView> balanceDisplayMaV;

  private JLabel alertMessageLabel;
  private JLabel alertRemainingLabel;
  private JButton alertButton;

  private final JPanel contentPanel;
  private final JPanel alertPanel;

  public HeaderView() {

    CoreServices.uiEventBus.register(this);

    // Create the content panel
    contentPanel = Panels.newPanel(new MigLayout(
      "fillx,insets 10 10 0 10,hidemode 3", // Layout insets ensure border is tight to sidebar
      "[]", // Columns
      "[][shrink]" // Rows
    ));

    // Create the alert panel
    alertPanel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout insets define the padding for the alert
      "[grow][][][]", // Columns
      "[]" // Rows
    ));

    // Start off in hiding
    alertPanel.setVisible(false);

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.headerPanelBackground());

    // Create the balance display
    balanceDisplayMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.HEADER);

    contentPanel.add(balanceDisplayMaV.getView().newComponentPanel(), "growx,wrap");
    contentPanel.add(alertPanel, "growx,aligny top,push");

    onLocaleChangedEvent(null);
  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * <p>Handles the representation of the header when a locale change occurs</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onLocaleChangedEvent(LocaleChangedEvent event) {

    populateAlertPanel();

  }

  /**
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onBalanceChangedEvent(BalanceChangedEvent event) {

    // Handle the update
    balanceDisplayMaV.getModel().setLocalAmount(event.getLocalBalance());
    balanceDisplayMaV.getModel().setSatoshis(event.getSatoshis());
    balanceDisplayMaV.getModel().setRateProvider(event.getRateProvider());

    balanceDisplayMaV.getView().updateView();
  }

  /**
   * <p>Handles the presentation of a new alert</p>
   *
   * @param event The show alert event
   */
  @Subscribe
  public void onAlertAddedEvent(AlertAddedEvent event) {

    AlertModel alertModel = event.getAlertModel();

    // Update the text according to the model
    alertMessageLabel.setText(alertModel.getLocalisedMessage());
    alertRemainingLabel.setText(alertModel.getRemainingText());

    if (alertModel.getButton().isPresent()) {

      JButton button = alertModel.getButton().get();
      alertButton.setAction(button.getAction());
      alertButton.setText(button.getText());
      alertButton.setIcon(button.getIcon());

      alertButton.setVisible(true);
    }

    switch (alertModel.getSeverity()) {
      case RED:
        PanelDecorator.applyDangerTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), alertButton);
        break;
      case AMBER:
        PanelDecorator.applyWarningTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.warningAlertBackground(), alertButton);
        break;
      case GREEN:
        PanelDecorator.applySuccessTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.successAlertBackground(), alertButton);
        break;
      case PINK:
        PanelDecorator.applyPendingTheme(alertPanel);
        NimbusDecorator.applyThemeColor(Themes.currentTheme.pendingAlertBackground(), alertButton);
        break;
      default:
        throw new IllegalStateException("Unknown severity: " + alertModel.getSeverity().name());
    }

    alertPanel.setVisible(true);

  }

  /**
   * <p>Remove any existing alert</p>
   *
   * @param event The remove alert event
   */
  @Subscribe
  public void onAlertRemovedEvent(RemoveAlertEvent event) {

    // Hide the alert panel
    alertPanel.setVisible(false);

  }

  private void populateAlertPanel() {

    alertPanel.removeAll();

    alertMessageLabel = Labels.newBlankLabel();
    alertRemainingLabel = Labels.newBlankLabel();

    alertButton = new JButton();
    alertButton.setVisible(false);

    JLabel closeLabel = Labels.newPanelCloseLabel(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        ControllerEvents.fireRemoveAlertEvent();
      }
    });

    // Determine how to add them back into the panel
    if (Languages.isLeftToRight()) {
      alertPanel.add(alertMessageLabel, "push");
      alertPanel.add(alertRemainingLabel, "shrink,right");
      alertPanel.add(alertButton, "shrink,right");
      alertPanel.add(closeLabel);
    } else {
      alertPanel.add(closeLabel);
      alertPanel.add(alertButton, "shrink,left");
      alertPanel.add(alertRemainingLabel, "shrink,left");
      alertPanel.add(alertMessageLabel, "push");
    }

  }

}
