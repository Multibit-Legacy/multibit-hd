package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ProgressChangedEvent;
import org.multibit.hd.ui.events.view.SystemStatusChangedEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the footer display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class FooterView {

  private final JPanel contentPanel;
  private final JProgressBar progressBar;
  private final JLabel messageLabel;
  private final JLabel statusLabel;
  private final JLabel statusIcon;

  public FooterView() {

    CoreServices.uiEventBus.register(this);

    contentPanel = Panels.newPanel(new MigLayout(
      "ins 7",
      "[][][]",
      "[]"
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.headerPanelBackground());

    progressBar = new JProgressBar();
    progressBar.setMinimum(0);
    progressBar.setMaximum(100);
    progressBar.setForeground(Themes.currentTheme.infoAlertBackground());

    messageLabel = new JLabel();

    // Label text and icon are different colours so must be separated
    statusLabel = new JLabel("");
    statusIcon = new JLabel("");
    AwesomeDecorator.bindIcon(AwesomeIcon.CIRCLE, statusIcon, false, 16);

    // Start with no knowledge so assume the worst
    statusIcon.setForeground(Themes.currentTheme.dangerAlertBackground());

    contentPanel.add(progressBar, "shrink,left");
    contentPanel.add(messageLabel, "grow,push");
    contentPanel.add(statusLabel, "split,shrink,right");
    contentPanel.add(statusIcon, "right");

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * <p>Handles the representation of a system status change</p>
   *
   * @param event The system status change event
   */
  @Subscribe
  public void onSystemStatusChangeEvent(SystemStatusChangedEvent event) {

    statusLabel.setText(event.getLocalisedMessage());
    switch (event.getSeverity()) {
      case RED:
        statusIcon.setForeground(Themes.currentTheme.dangerAlertBackground());
        break;
      case AMBER:
        statusIcon.setForeground(Themes.currentTheme.warningAlertBackground());
        break;
      case GREEN:
        statusIcon.setForeground(Themes.currentTheme.successAlertBackground());
        break;
      default:
        // Unknown status
        throw new IllegalStateException("Unknown event severity "+event.getSeverity());
    }

  }

  /**
   * <p>Handles the representation of a progress change</p>
   *
   * @param event The progress change event
   */
  @Subscribe
  public void onProgressChangedEvent(ProgressChangedEvent event) {

    // Show the downloading message until it finishes
    if (event.getPercent() < 100) {
      messageLabel.setText(event.getLocalisedMessage());
    } else {
      // Synchronized so clear the message
      messageLabel.setText("");
    }

    progressBar.setValue(event.getPercent());

  }

}
