package org.multibit.hd.ui.views.screens.help;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.utils.SafeDesktop;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the help detail display</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class HelpScreenView extends AbstractScreenView<HelpScreenModel> implements PropertyChangeListener {

  private static final Logger log = LoggerFactory.getLogger(HelpScreenView.class);

  /**
   * A primitive form of "browser history"
   */
  private int currentPageIndex = 0;
  private final LinkedList<URL> pageList = Lists.newLinkedList();
  private JEditorPane editorPane;

  private JButton backButton;
  private JButton forwardButton;
  private JButton refreshButton;
  private JButton homeButton;
  private JButton launchBrowserButton;
  private Icon launchBrowserRegularIcon;
  private Icon launchBrowserHighlitIcon;

  private JButton showErrorReportingButton;

  /**
   * True if relative and MultiBit URLs should be modified to point to the internal help
   */
  private boolean useInternalHelp = false;

  /**
   * Handles the loading of the internal images (lazy initialisation to avoid delays on start)
   */
  private ListeningExecutorService listeningExecutorService = SafeExecutors.newSingleThreadExecutor("load-internal-help");

  private URL homeUrl;

  // View components

  /**
   * The link color
   */
  private final Color linkColor = Themes.currentTheme.sidebarSelectedText();
  private final String linkHexColor = String.format("#%02x%02x%02x", linkColor.getRed(), linkColor.getGreen(), linkColor.getBlue());

  private final ListeningExecutorService cacertsExecutorService = SafeExecutors.newSingleThreadExecutor("help-repair-cacerts");


  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public HelpScreenView(HelpScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);

  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    MigLayout layout = new MigLayout(
      Panels.migXYDetailLayout(),
      "[][][][][][]push[]", // Column constraints
      "[shrink]10[grow]" // Row constraints
    );

    // Create the content panel
    JPanel contentPanel = Panels.newPanel(layout);

    backButton = Buttons.newBackButton(getBackAction());
    forwardButton = Buttons.newForwardButton(getForwardAction());

    refreshButton = Buttons.newRefreshButton(getRefreshAction());
    homeButton = Buttons.newHomeButton(getHomeAction());

    launchBrowserButton = Buttons.newLaunchBrowserButton(
      getLaunchBrowserAction(),
      MessageKey.VIEW_IN_EXTERNAL_BROWSER,
      MessageKey.VIEW_IN_EXTERNAL_BROWSER_TOOLTIP
    );

    launchBrowserRegularIcon = launchBrowserButton.getIcon();
    launchBrowserHighlitIcon = ImageDecorator.toImageIcon(
      AwesomeDecorator.createIcon(
              AwesomeIcon.EXTERNAL_LINK,
              Themes.currentTheme.sidebarSelectedText(),
              MultiBitUI.NORMAL_ICON_SIZE
      ));

    showErrorReportingButton = Buttons.newShowErrorReportButton(getShowErrorReportingAction());

    // Control visibility and availability
    launchBrowserButton.setEnabled(Desktop.isDesktopSupported());

    // Note adding search facility is more complex than it first appears
    // You will need a corresponding service on the website and
    // a mechanism of caching all words in the help corpus along
    // with the article titles

    // Create the browser
    editorPane = createBrowser();

    // Create the scroll pane and add the HTML editor pane to it
    JScrollPane scrollPane = new JScrollPane(editorPane);

    // Ensure it is accessible
    AccessibilityDecorator.apply(scrollPane, MessageKey.HELP);

    scrollPane.setViewportBorder(null);

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(scrollPane, true);

    // Add to the panel
    contentPanel.add(backButton, "shrink");
    contentPanel.add(forwardButton, "shrink");
    contentPanel.add(refreshButton, "shrink");
    contentPanel.add(homeButton, "shrink");
    contentPanel.add(launchBrowserButton, "shrink");
    contentPanel.add(showErrorReportingButton, "shrink");
    contentPanel.add(Labels.newBlankLabel(), "grow,push,wrap"); // Empty label to pack buttons
    contentPanel.add(scrollPane, "span 7,grow,push");

    return contentPanel;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Load the current page in the history
          try {
            editorPane.setPage(currentPage());
          } catch (IOException e) {
            log.warn("Unable to load current page ", e);
          }
        }
      });

  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    // TODO Find some way of detecting failed load "page" and "document" don't give anything

    // Tried a "expect property change" flag but asynchronous load takes random time and we'll
    // end up with many false positives, could have a "watchdog" thread that expects the
    // flag to be cleared after a set period (e.g. 30s or something)

    // Given the variation in network performance across the world, there is no right way to
    // easily achieve this so leaving it for now

  }

  /**
   * @return An editor pane with support for basic HTML (v3.2)
   */
  private JEditorPane createBrowser() {
    // If the remote help does not load it could be due to an out of date multibit.org SSLcert so
    // refresh all the certs in the background
    boolean refreshCerts = false;

    // Test the main website help is available
    // Look up the standard MultiBit help (via HTTPS)
    try {
      // Create the starting page
      homeUrl = URI.create(InstallationManager.MBHD_WEBSITE_HELP_BASE + "/contents.html").toURL();
      addPage(homeUrl);

      String content = HttpsManager.getContentAsString(homeUrl);
      if (!content.contains("<li>")) {
        // Something is wrong at the server end so switch to internal mode
        log.warn("Content from MultiBit.org does not contain <li> so switching to internal help");
        useInternalHelp = true;
        refreshCerts = true;
      }

    } catch (MalformedURLException e) {
      // This is a coding error so should blow up
      log.error("Unable to load help home page ", e);
      return null;
    } catch (IOException e) {
      log.warn("Problem with MultiBit.org so switching to internal help", e);
      useInternalHelp = true;
      refreshCerts = true;
    }

    // Always use internal help for FEST tests to provide predictable output
    if (InstallationManager.unrestricted) {
      useInternalHelp = true;
    }

    // Create an editor pane to wrap the HTML editor kit
    editorPane = new JEditorPane() {

      @Override
      protected InputStream getStream(URL page) throws IOException {

        // This method only works for pages, not elements within pages

        if (useInternalHelp) {

          // Remove the host
          String replacedPath = page.getPath().replace(InstallationManager.MBHD_WEBSITE_HELP_DOMAIN, "");

          // Replace with a classpath
          replacedPath = "/assets/html/en/help" + replacedPath;

          // Read from the classpath
          return HelpScreenView.class.getResourceAsStream(replacedPath);
        }

        // Fall back to standard reader
        return Resources.asByteSource(page).openBufferedStream();
      }

    };

    // Ensure FEST can find it
    editorPane.setName(MessageKey.HELP.getKey() + ".editorPane");

    // Make it read-only to allow links to be followed
    editorPane.setEditable(false);

    // Apply theme
    editorPane.setBackground(Themes.currentTheme.detailPanelBackground());
    editorPane.setForeground(Themes.currentTheme.text());

    // Create the HTML editor kit (contains style rules etc)
    HTMLEditorKit kit = createEditorKit();
    editorPane.setEditorKit(kit);

    // Create a default document to manage HTML
    HTMLDocument htmlDocument = (HTMLDocument) kit.createDefaultDocument();
    htmlDocument.setBase(homeUrl);
    editorPane.setDocument(htmlDocument);

    editorPane.addHyperlinkListener(
      new HyperlinkListener() {

        @SuppressFBWarnings({"ITU_INAPPROPRIATE_TOSTRING_USE", "S508C_SET_COMP_COLOR", "S508C_SET_COMP_COLOR"})
        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {

          final URL url = e.getURL();

          if (url != null) {
            boolean multiBitHelp = url.toString().startsWith(InstallationManager.MBHD_WEBSITE_HELP_DOMAIN)
              && url.toString().contains("/hd")
              && url.toString().endsWith(".html");

            if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {

              if (!multiBitHelp) {

                // Indicate an external link
                if (launchBrowserButton.isEnabled()) {
                  launchBrowserButton.setForeground(Themes.currentTheme.sidebarSelectedText());
                  launchBrowserButton.setIcon(launchBrowserHighlitIcon);
                }
              }
            }

            if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {

              if (launchBrowserButton.isEnabled()) {
                launchBrowserButton.setForeground(Themes.currentTheme.buttonText());
                launchBrowserButton.setIcon(launchBrowserRegularIcon);
              }
            }

            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

              // Force the main browser if not MultiBit HD help (i.e. a relative link to the FAQ)
              if (!multiBitHelp) {

                listeningExecutorService.submit(
                  new Runnable() {
                    @Override
                    public void run() {
                      try {
                        if (launchBrowserButton.isEnabled()) {
                          if (!SafeDesktop.browse(url.toURI())) {
                            Sounds.playBeep();
                          }
                        } else {
                          // No browser available
                          Sounds.playBeep();
                        }
                      } catch (URISyntaxException e1) {
                        Sounds.playBeep();
                      }
                    }
                  });

              } else {

                // User has clicked on the link so treat as a new page
                addPage(e.getURL());

                // We are allowed to browse to this page
                browse(currentPage());
              }
            }
          }

        }
      });

    // Keep track of loading events
    editorPane.addPropertyChangeListener(this);

    // Refresh certs in background if necessary
    if (refreshCerts) {
      refreshCertsInBackground();
    }

    return editorPane;
  }

  private void refreshCertsInBackground() {
    ListenableFuture cacertsFuture = cacertsExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {
          log.debug("Starting refresh of SSL certs...");
          HttpsManager.INSTANCE.installCACertificates(
            InstallationManager.getOrCreateApplicationDataDirectory(),
            InstallationManager.CA_CERTS_NAME,
            null, // Use default host list
            true // Force loading
          );

        }
      });
    Futures.addCallback(
      cacertsFuture, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {
          log.debug("SSL certs have been updated.");
        }

        @Override
        public void onFailure(Throwable t) {
          log.error("SSL certs update FAILED - error was {}", t);
        }
      });
  }

  /**
   * @return The HTML editor kit providing the CSS styles
   */
  private HTMLEditorKit createEditorKit() {

    // Create an HTML editor kit
    HTMLEditorKit kit = new HTMLEditorKit() {

      @Override
      public Document createDefaultDocument() {

        return super.createDefaultDocument();
      }

    };

    // Set a basic style sheet
    StyleSheet styleSheet = kit.getStyleSheet();

    // Avoid setting the background here since it can bleed through the look and feel
    styleSheet.addRule("body{font-family:\"Helvetica Neue\",\"Liberation Sans\",Arial,sans-serif;margin:0;padding:0;}");
    styleSheet.addRule("h1,h2{font-family:\"Helvetica Neue\",\"Liberation Sans\",Arial,sans-serif;font-weight:normal;}");
    Color headingHexColor = Themes.currentTheme.text();
    String headingHexColorString = String.format("#%02x%02x%02x", headingHexColor.getRed(), headingHexColor.getGreen(), headingHexColor.getBlue());
    styleSheet.addRule("h1{color:" + headingHexColorString + ";font-size:200%;}");
    styleSheet.addRule("h2{color:" + headingHexColorString + ";font-size:180%;}");
    styleSheet.addRule("h3{color:" + headingHexColorString + ";font-size:150%;}");
    styleSheet.addRule("h4{color:" + headingHexColorString + ";font-size:120%;}");
    styleSheet.addRule("h1 img,h2 img,h3 img{vertical-align:middle;margin-right:5px;}");
    styleSheet.addRule("a { color: " + linkHexColor + "; font-weight:bold;}");
    styleSheet.addRule("a img{border:0;}");

    return kit;

  }

  /**
   * <p>Point the editor pane to the given URL for rendering</p>
   *
   * @param url The URL to render
   */
  private void browse(final URL url) {

    listeningExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {
          try {

            editorPane.setPage(url);

            // Reset the button background
            launchBrowserButton.setBackground(Themes.currentTheme.buttonBackground());

          } catch (IOException e) {
            // Log the error and report a failure to the user via the alerts
            log.warn("Unable to load page " + url, e);
            ControllerEvents.fireAddAlertEvent(
              Models.newAlertModel(
                Languages.safeText(MessageKey.GENERAL_NETWORK_CONFIGURATION_ERROR),
                RAGStatus.AMBER
              ));
            // Switch to internal mode if not already using it
            if (!useInternalHelp) {
              editorPane = createBrowser();
            }
          }
        }
      });

  }

  /**
   * <p>Adds the page to the list, inserting it so that a "back" operation will </p>
   *
   * @param url The URL of the page to add
   */
  private void addPage(URL url) {

    if (currentPageIndex >= pageList.size() - 1) {
      // We're at the end of the list so append the new page
      pageList.add(url);
      // Make this the current page
      currentPageIndex = pageList.size() - 1;
    } else {
      // We're not at the end so insert after current position
      // to maintain back button, then update to current
      pageList.add(currentPageIndex + 1, url);
      currentPageIndex++;
    }

    handleNavigationButtons();

  }

  /**
   * @return The URL for the current page in the history
   */
  private URL currentPage() {
    return pageList.get(currentPageIndex);
  }

  /**
   * @return The URL for the previous in the history
   */
  private URL previousPage() {

    // Limit current page index to first index or decrement
    currentPageIndex = (currentPageIndex <= 0) ? 0 : currentPageIndex - 1;

    // Enable/disable the navigation buttons
    handleNavigationButtons();

    return pageList.get(currentPageIndex);
  }

  /**
   * @return The URL for the next page in the history
   */
  private URL nextPage() {

    // Limit current page index to last index or increment
    currentPageIndex = (currentPageIndex >= pageList.size() - 1) ? pageList.size() - 1 : currentPageIndex + 1;

    return pageList.get(currentPageIndex);

  }

  /**
   * <p>Control how the navigation buttons are presented depending on the history</p>
   */
  private void handleNavigationButtons() {

    backButton.setEnabled(currentPageIndex > 0);
    forwardButton.setEnabled(currentPageIndex < pageList.size() - 1);

  }

  /**
   * @return The "launch browser" action
   */
  private Action getLaunchBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          if (!SafeDesktop.browse(currentPage().toURI())) {
            Sounds.playBeep();
          }
        } catch (URISyntaxException e1) {
          ExceptionHandler.handleThrowable(e1);
        }

      }
    };
  }

  /**
   * @return The "show error reporting dialog" action
   */
  private Action getShowErrorReportingAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        ExceptionHandler.handleManualErrorReport();

      }
    };
  }

  /**
   * @return The "forward" action
   */
  private Action getForwardAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        browse(nextPage());

      }
    };

  }

  /**
   * @return The "back" action
   */
  private Action getBackAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        browse(previousPage());

      }
    };
  }

  /**
   * @return The "refresh" action
   */
  private Action getRefreshAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        browse(currentPage());

      }
    };
  }

  /**
   * @return The "home" action
   */
  private Action getHomeAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        addPage(homeUrl);
        browse(homeUrl);

      }
    };
  }
}
