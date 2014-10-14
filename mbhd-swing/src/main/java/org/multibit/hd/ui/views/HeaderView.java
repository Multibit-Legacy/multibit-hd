package org.multibit.hd.ui.views;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.controller.RemoveAlertEvent;
import org.multibit.hd.ui.events.view.AlertAddedEvent;
import org.multibit.hd.ui.events.view.BalanceChangedEvent;
import org.multibit.hd.ui.events.view.ViewChangedEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the balance display</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class HeaderView extends AbstractView {

  private final ModelAndView<DisplayAmountModel, DisplayAmountView> balanceDisplayMaV;

  private JLabel alertMessageLabel;
  private JLabel alertRemainingLabel;

  private JButton alertButton;
  private JButton closeButton;

  private final JPanel contentPanel;
  private final JPanel alertPanel;

  public HeaderView() {

    super();

    contentPanel = Panels.newPanel(new MigLayout(
      Panels.migLayout("fillx,insets 10 10 5 10,hidemode 3"), // Layout insets ensure border is tight to sidebar
      "[][]", // Columns
      "[][shrink]" // Rows
    ));

    // Create the alert panel
    alertPanel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(),
      "[grow][][][]", // Columns
      "[]" // Rows
    ));

    // Start off in hiding
    alertPanel.setVisible(false);

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.headerPanelBackground());
    contentPanel.setOpaque(true);

    // Create the balance display hiding it initially
    balanceDisplayMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.HEADER, true, "header.balance");
    balanceDisplayMaV.getView().setVisible(false);

    // Provide a fixed height to avoid an annoying "slide down" during unlock
    contentPanel.add(balanceDisplayMaV.getView().newComponentPanel(), "growx,push,hmin 50,wrap");
    contentPanel.add(alertPanel, "growx,aligny top,push");

    populateAlertPanel();

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }


  /**
   * <p>Handles the representation of the balance based on the current configuration</p>
   *
   * @param event The balance change event
   */
  @Subscribe
  public void onBalanceChangedEvent(final BalanceChangedEvent event) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Handle the update
        balanceDisplayMaV.getModel().setLocalAmount(event.getLocalBalance());
        balanceDisplayMaV.getModel().setCoinAmount(event.getCoinBalance());
        balanceDisplayMaV.getModel().setRateProvider(event.getRateProvider());
        if (event.getRateProvider().isPresent()) {
          balanceDisplayMaV.getModel().setLocalAmountVisible(true);
        }

        // Do not set the visibility here, use the ViewChangedEvent

        balanceDisplayMaV.getView().updateView(Configurations.currentConfiguration);
      }
    });

  }

  /**
   * <p>Handles the presentation of a new alert</p>
   *
   * @param event The show alert event
   */
  @Subscribe
  public void onAlertAddedEvent(final AlertAddedEvent event) {


    Preconditions.checkNotNull(event, "'event' must be present");

    final AlertModel alertModel = event.getAlertModel();

    Preconditions.checkNotNull(alertModel, "'alertModel' must be present");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Update the text according to the model
        alertMessageLabel.setText(alertModel.getLocalisedMessage());
        alertRemainingLabel.setText(alertModel.getRemainingText());

        if (alertModel.getButton().isPresent()) {

          JButton button = alertModel.getButton().get();
          alertButton.setAction(button.getAction());
          alertButton.setText(button.getText());
          alertButton.setIcon(button.getIcon());
          alertButton.setName(button.getName());
          alertButton.setToolTipText(button.getToolTipText());

          alertButton.setVisible(true);
        } else {
          alertButton.setVisible(false);
        }

        // Don't play sounds here since this will be called each time an alert is dismissed
        switch (alertModel.getSeverity()) {
          case RED:
            PanelDecorator.applyDangerTheme(alertPanel);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), alertButton);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), closeButton);
            break;
          case AMBER:
            PanelDecorator.applyWarningTheme(alertPanel);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.warningAlertBackground(), alertButton);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.warningAlertBackground(), closeButton);
            break;
          case GREEN:
            PanelDecorator.applySuccessTheme(alertPanel);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.successAlertBackground(), alertButton);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.successAlertBackground(), closeButton);
            break;
          case PINK:
            PanelDecorator.applyPendingTheme(alertPanel);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.pendingAlertBackground(), alertButton);
            NimbusDecorator.applyThemeColor(Themes.currentTheme.pendingAlertBackground(), closeButton);
            break;
          default:
            throw new IllegalStateException("Unknown severity: " + alertModel.getSeverity().name());
        }

        alertPanel.setVisible(true);

      }
    });

  }

  /**
   * <p>Remove any existing alert</p>
   *
   * @param event The remove alert event
   */
  @Subscribe
  public void onAlertRemovedEvent(RemoveAlertEvent event) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // Hide the alert panel
        alertPanel.setVisible(false);
      }
    });

  }

  /**
   * <p>Called when the view should be should be changed</p>
   *
   * @param event The view changed event
   */
  @Subscribe
  public void onViewChangedEvent(final ViewChangedEvent event) {

    if (event.getViewKey().equals(ViewKey.HEADER)) {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          balanceDisplayMaV.getView().setVisible(event.isVisible());
          if (alertMessageLabel.getText().length() != 0 && event.isVisible()) {
            alertPanel.setVisible(event.isVisible());
          }
        }
      });

    }

  }

  /**
   * <p>Populate the alert panel in preparation for any alerts</p>
   */
  private void populateAlertPanel() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be in the EDT. Check MainController.");

    alertPanel.removeAll();

    alertMessageLabel = Labels.newBlankLabel();
    alertMessageLabel.setName("alert_message_label");

    alertRemainingLabel = Labels.newBlankLabel();
    alertRemainingLabel.setName("alert_remaining_label");

    // Placeholder button that gets overwritten
    alertButton = new JButton();
    alertButton.setName("alert_button");
    alertButton.setVisible(false);

    closeButton = Buttons.newPanelCloseButton(getCloseAlertAction());

    // Determine how to add them back into the panel
    if (Languages.isLeftToRight()) {
      alertPanel.add(alertMessageLabel, "push");
      alertPanel.add(alertRemainingLabel, "shrink,right");
      alertPanel.add(alertButton, "shrink,right");
      alertPanel.add(closeButton);
    } else {
      alertPanel.add(closeButton);
      alertPanel.add(alertButton, "shrink,left");
      alertPanel.add(alertRemainingLabel, "shrink,left");
      alertPanel.add(alertMessageLabel, "push");
    }

  }

  /**
   * @return A new action for closing the alert panel
   */
  private Action getCloseAlertAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        ControllerEvents.fireRemoveAlertEvent();

      }

    };
  }


}
