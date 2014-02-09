package org.multibit.hd.ui.views;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ProgressChangedEvent;
import org.multibit.hd.ui.events.view.SystemStatusChangedEvent;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

  // Hide the progress bar
  private Optional<? extends ScheduledFuture<?>> hideProgressBarFuture = Optional.absent();

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
    progressBar.setEnabled(false);

    messageLabel = new JLabel();

    // Label text and icon are different colours so must be separated
    statusLabel = new JLabel("");
    statusIcon = new JLabel("");
    AwesomeDecorator.bindIcon(AwesomeIcon.CIRCLE, statusIcon, false, AwesomeDecorator.SMALL_ICON_SIZE);

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
        throw new IllegalStateException("Unknown event severity " + event.getSeverity());
    }

  }

  /**
   * <p>Handles the representation of a progress change</p>
   *
   * @param event The progress change event
   */
  @Subscribe
  public void onProgressChangedEvent(ProgressChangedEvent event) {

    progressBar.setEnabled(true);

    // Provide some ranges to allow different colouring
    Range<Integer> hidden = Ranges.lessThan(0);
    Range<Integer> amber = Ranges.closed(0, 99);
    Range<Integer> green = Ranges.greaterThan(99);

    if (hidden.contains(event.getPercent())) {
      progressBar.setVisible(false);
    }

    if (amber.contains(event.getPercent())) {
      NimbusDecorator.applyThemeColor(Themes.currentTheme.warningAlertBackground(), progressBar);
      progressBar.setValue(event.getPercent());
      progressBar.setVisible(true);
    }

    if (green.contains(event.getPercent())) {
      NimbusDecorator.applyThemeColor(Themes.currentTheme.successAlertBackground(), progressBar);
      progressBar.setValue(Math.min(100, event.getPercent()));
      progressBar.setVisible(true);

      // Check if we are already in the process of hiding the progress bar
      if (hideProgressBarFuture.isPresent()) {

        // Cancel the current progress bar hide
        hideProgressBarFuture.get().cancel(true);
      }

      // Create a new one
      hideProgressBarFuture = Optional.of(scheduleHideProgressBar());

    }

  }

  /**
   * @return A scheduled future for hiding the progress bar after a predetermined delay
   */
  private ScheduledFuture<?> scheduleHideProgressBar() {

    return SafeExecutors.newScheduledThreadPool(1).schedule(new Runnable() {
      @Override
      public void run() {

        // Ensure we execute the update on the Swing thread
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            progressBar.setVisible(false);

          }
        });

      }
    }, 4, TimeUnit.SECONDS);
  }

}
