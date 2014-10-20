package org.multibit.hd.ui.views;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ProgressChangedEvent;
import org.multibit.hd.ui.events.view.SystemStatusChangedEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Future;
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
public class FooterView extends AbstractView {

  private static final Logger log = LoggerFactory.getLogger(FooterView.class);

  private final JPanel contentPanel;
  private final JProgressBar progressBar;
  private final JLabel messageLabel;
  private final JLabel statusLabel;
  private final JLabel statusIcon;

  private final ListeningScheduledExecutorService scheduledExecutorService = SafeExecutors.newScheduledThreadPool(3, "hide-progress");
  private final List<Future> hideProgressFutures = Lists.newArrayList();

  public FooterView() {

    super();

    contentPanel = Panels.newPanel(new MigLayout(
      Panels.migLayout("insets 7"),
      "[][][]",
      "[]"
    ));

    contentPanel.setMinimumSize(new Dimension(MultiBitUI.WIZARD_MIN_WIDTH,MultiBitUI.FOOTER_MINIMUM_HEIGHT));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.footerPanelBackground());
    contentPanel.setOpaque(true);

    progressBar = new JProgressBar();
    progressBar.setMinimum(0);
    progressBar.setMaximum(100);
    progressBar.setForeground(Themes.currentTheme.infoAlertBackground());

    progressBar.setEnabled(false);
    progressBar.setOpaque(false);
    progressBar.setVisible(false);

    messageLabel = Labels.newBlankLabel();

    // Label text and icon are different colours so must be separated
    statusLabel = Labels.newBlankLabel();
    statusIcon = Labels.newBlankLabel();
    AwesomeDecorator.bindIcon(AwesomeIcon.CIRCLE, statusIcon, false, MultiBitUI.SMALL_ICON_SIZE);

    // Create a TOR icon - don't use green or amber colouring it is visually confusing
    JLabel torIcon = Labels.newBlankLabel();
    AwesomeDecorator.bindIcon(AwesomeIcon.LOCK, torIcon, false, MultiBitUI.SMALL_ICON_SIZE);
    AccessibilityDecorator.apply(torIcon, MessageKey.SELECT_TOR, MessageKey.SELECT_TOR_TOOLTIP);
    torIcon.setVisible(Configurations.currentConfiguration.isTor());

    // Start with no knowledge so assume the worst
    statusIcon.setForeground(Themes.currentTheme.dangerAlertBackground());

    contentPanel.add(torIcon, "shrink,left");
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
  public void onSystemStatusChangeEvent(final SystemStatusChangedEvent event) {

    if (statusLabel == null) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        statusLabel.setText(event.getLocalisedMessage());
        statusIcon.setToolTipText(event.getLocalisedMessage());
        switch (event.getSeverity()) {
          case RED:
            statusIcon.setForeground(Themes.currentTheme.statusRed());
            break;
          case AMBER:
            statusIcon.setForeground(Themes.currentTheme.statusAmber());
            break;
          case GREEN:
            statusIcon.setForeground(Themes.currentTheme.statusGreen());
            break;
          default:
            // Unknown status
            throw new IllegalStateException("Unknown event severity " + event.getSeverity());
        }

      }
    });

  }

  /**
   * <p>Handles the representation of a progress change</p>
   *
   * @param event The progress change event
   */
  @Subscribe
  public void onProgressChangedEvent(final ProgressChangedEvent event) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        progressBar.setEnabled(true);

        // Provide some ranges to allow different colouring
        // If you get a compilation error here you need to be using guava 16.0.1 or above !
        Range<Integer> hidden = Range.lessThan(0);
        Range<Integer> amber = Range.closed(0, 99);
        Range<Integer> green = Range.greaterThan(99);

        if (hidden.contains(event.getPercent())) {
          if (hideProgressFutures.isEmpty()) {
            // No earlier activity so hide immediately
            progressBar.setVisible(false);
          }
        }

        if (amber.contains(event.getPercent())) {

          // Make the progress bar amber
          NimbusDecorator.applyThemeColor(Themes.currentTheme.statusAmber(), progressBar);
          progressBar.setValue(event.getPercent());
          progressBar.setVisible(true);

        }

        if (green.contains(event.getPercent())) {

          // Cancel all existing hide operations
          cancelPendingHideProgressFutures();

          // Make the progress bar green
          NimbusDecorator.applyThemeColor(Themes.currentTheme.statusGreen(), progressBar);
          progressBar.setValue(100);
          progressBar.setVisible(true);

          // Schedule the new hide
          hideProgressFutures.add(scheduleHideProgressBar());

        }
      }
    });

  }

  /**
   * Cancel all existing pending futures
   */
  private void cancelPendingHideProgressFutures() {

    for (Future future : hideProgressFutures) {
      future.cancel(true);
    }
    hideProgressFutures.clear();

  }

  /**
   * @return A one-shot scheduled future for hiding the progress bar after a predetermined delay
   */
  private ScheduledFuture<?> scheduleHideProgressBar() {

    return scheduledExecutorService.schedule(new Runnable() {
      @Override
      public void run() {

        // Ensure we execute the update on the Swing thread
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            log.trace("Hiding progress bar");
            progressBar.setVisible(false);

          }
        });

      }
    }, 4, TimeUnit.SECONDS);

  }

}
