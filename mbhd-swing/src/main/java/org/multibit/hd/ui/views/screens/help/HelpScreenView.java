package org.multibit.hd.ui.views.screens.help;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListeningExecutorService;
import net.miginfocom.swing.MigLayout;
import org.imgscalr.Scalr;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the help detail display</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class HelpScreenView extends AbstractScreenView<HelpScreenModel> {

  private static final Logger log = LoggerFactory.getLogger(HelpScreenView.class);

  /**
   * A primitive form of "browser history"
   */
  private int currentPageIndex = 0;
  private final LinkedList<URL> pageList = Lists.newLinkedList();
  private JEditorPane editorPane;

  private JButton backButton;
  private JButton forwardButton;
  private JButton launchBrowserButton;

  /**
   * True if relative and MultiBit URLs should be modified to point to the internal help
   */
  private boolean useInternalHelp = false;

  /**
   * Handles the loading of the internal images
   */
  private ListeningExecutorService listeningExecutorService;

  /**
   * We have to use a Hashtable here because of Swing internal handling
   */
  private Hashtable internalImageCache = new Hashtable();

  /**
   * Dynamic resource lookup is not well supported so hard coded values are used
   * as an interim solution
   */
  private static final Iterable<String> imageNames = Splitter.on("\n").split(
    // In Intellij just highlight the names and paste into "" to get the list
    "about.png\n" +
      "appearance.png\n" +
      "change-password.png\n" +
      "contacts.png\n" +
      "edit-contact.png\n" +
      "edit-wallet.png\n" +
      "empty-wallet.png\n" +
      "exchange-rates.png\n" +
      "history.png\n" +
      "labs.png\n" +
      "languages.png\n" +
      "payments.png\n" +
      "preferences.png\n" +
      "repair-wallet.png\n" +
      "request-payment.png\n" +
      "restorePassword.png\n" +
      "send-payment.png\n" +
      "send-receive.png\n" +
      "sign-message.png\n" +
      "sounds.png\n" +
      "tools.png\n" +
      "transaction-detail.png\n" +
      "transaction-overview.png\n" +
      "units.png\n" +
      "verify-message.png\n" +
      "verify-network.png"
  );

  // View components

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
      "[][][]push[]", // Column constraints
      "[shrink]10[grow]" // Row constraints
    );

    // Create the content panel
    JPanel contentPanel = Panels.newPanel(layout);

    backButton = Buttons.newBackButton(getBackAction());
    forwardButton = Buttons.newForwardButton(getForwardAction());
    launchBrowserButton = Buttons.newLaunchBrowserButton(getLaunchBrowserAction(), MessageKey.VIEW_IN_EXTERNAL_BROWSER, MessageKey.VIEW_IN_EXTERNAL_BROWSER_TOOLTIP);

    // Control visibility and availability
    launchBrowserButton.setEnabled(Desktop.isDesktopSupported());

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
    contentPanel.add(launchBrowserButton, "shrink");
    contentPanel.add(Labels.newBlankLabel(), "grow,push,wrap"); // Empty label to pack buttons
    contentPanel.add(scrollPane, "span 4,grow,push");

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
            log.warn(e.getMessage(), e);
          }
        }
      });

  }

  /**
   * @return An editor pane with support for basic HTML (v3.2)
   */
  private JEditorPane createBrowser() {

    // Test the main website help is available
    // Look up the standard MultiBit help (via HTTPS)
    URL helpBaseUrl = null;
    try {
      // TODO Change contents.html to index.html
      helpBaseUrl = URI.create(InstallationManager.MBHD_WEBSITE_HELP_BASE + "/contents.html").toURL();

      String content = Resources.toString(helpBaseUrl, Charsets.UTF_8);
      if (!content.contains("<li>")) {
        // Something is wrong at the server end so switch to internal mode
        useInternalHelp = true;
      }

    } catch (MalformedURLException e) {
      // This is a coding error so should blow up
      log.error(e.getMessage(), e);
      return null;
    } catch (IOException e) {
      log.warn("Problem with MultiBit.org so switching to internal help", e);
      useInternalHelp = true;
    }

    // Only populate the image cache if we have to
    if (useInternalHelp) {
      populateImageCache();
    }

    try {
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

      // Make it read-only to allow links to be followed
      editorPane.setEditable(false);

      // Apply theme
      editorPane.setBackground(Themes.currentTheme.detailPanelBackground());

      // Create the HTML editor kit (contains style rules etc)
      HTMLEditorKit kit = createEditorKit();
      editorPane.setEditorKit(kit);

      // Create a default document to manage HTML
      HTMLDocument htmlDocument = (HTMLDocument) kit.createDefaultDocument();
      htmlDocument.setBase(helpBaseUrl);
      editorPane.setDocument(htmlDocument);

      // Create the starting page
      addPage(URI.create(InstallationManager.MBHD_WEBSITE_HELP_BASE + "/contents.html").toURL());

    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }

    editorPane.addHyperlinkListener(
      new HyperlinkListener() {
        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {

          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

            URL url = e.getURL();

            boolean relative = !url.toString().startsWith("http");
            boolean multiBit = url.toString().startsWith(InstallationManager.MBHD_WEBSITE_HELP_DOMAIN);

            // Ignore off site links
            if (!relative && !multiBit) {

              // User is clicking on an external link so hint at proper browser
              Sounds.playBeep();
              launchBrowserButton.setBackground(Themes.currentTheme.readOnlyBackground());

            } else {

              // User has clicked on the link so treat as a new page
              addPage(e.getURL());

              // We are allowed to browse to this page
              browse(currentPage());
            }
          }

        }
      });

    return editorPane;
  }

  /**
   * Binds the image cache to the given document (a new one per page)
   *
   * @param document The document (usually from the editor kit)
   */
  private void bindImageCache(Document document) {

    Dictionary cache = (Dictionary) document.getProperty("imageCache");
    if (cache == null) {
      cache = internalImageCache;
      document.putProperty("imageCache", cache);
    }

  }

  /**
   * Populate the image cache with internal images using their external URL as a key
   */
  @SuppressWarnings("unchecked")
  private void populateImageCache() {

    internalImageCache = new Hashtable();

    // This can take a while so keep it off the EDT
    listeningExecutorService = SafeExecutors.newSingleThreadExecutor("load-internal-help");

    // Run the decryption on a different thread
    listeningExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {

          try {

            for (String imageName : imageNames) {

              // Only interested in /assets/images
              // Images are directly under the domain so we build a suitable
              // absolute URL to fool the JEditorPane
              // Note that we have "mbhd-0.1" in the URL but not the resource path
              URL mockUrl = new URL(
                InstallationManager.MBHD_WEBSITE_HELP_DOMAIN +
                  "/images/en/screenshots/mbhd-0.1/" +
                  imageName
              );

              // Load the image from the classpath (no "mbhd-0.1")
              InputStream is = HelpScreenView.class.getResourceAsStream(
                "/assets/images/en/screenshots/mbhd-01/" +
                  imageName
              );
              if (is == null) {
                throw new IOException("Could not locate: '" + imageName + "' on the /assets classpath");
              }
              BufferedImage image = ImageIO.read(is);
              image.flush();

              // Resize it if necessary
              final int MAX_WIDTH = 670;
              if (image.getWidth(null) > MAX_WIDTH) {
                // Assume a screen shot and calculate the appropriate ratio
                // for minimum UI width
                double ratio = image.getWidth(null) / MAX_WIDTH;
                int height = (int) (image.getHeight(null) / ratio);
                image = Scalr.resize(
                  image,
                  Scalr.Method.ULTRA_QUALITY,
                  MAX_WIDTH, height,
                  Scalr.OP_ANTIALIAS
                );

              }

              // Cache it for later
              internalImageCache.put(mockUrl, image);

              log.debug("Cached /asset '{}'", imageName);

            }


          } catch (IOException e) {
            // This is a coding error
            log.error("Problem with the internal image assets.", e);
          }

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

        Document document = super.createDefaultDocument();

        if (useInternalHelp) {
          bindImageCache(document);
        }

        return document;
      }

    };

    // Define some color entries
    Color linkColor = Themes.currentTheme.sidebarSelectedText();

    String linkCss = String.format("#%02x%02x%02x", linkColor.getRed(), linkColor.getGreen(), linkColor.getBlue());
    String headingCss = "#973131";

    // Set a basic style sheet
    StyleSheet styleSheet = kit.getStyleSheet();

    // Avoid setting the background here since it can bleed through the look and feel
    styleSheet.addRule("body{font-family:\"Helvetica Neue\",\"Liberation Sans\",Arial,sans-serif;margin:0;padding:0;}");
    styleSheet.addRule("h1,h2{font-family:\"Helvetica Neue\",\"Liberation Sans\",Arial,sans-serif;font-weight:normal;}");
    styleSheet.addRule("h1{color:" + headingCss + ";font-size:200%;}");
    styleSheet.addRule("h2{color:" + headingCss + ";font-size:180%;}");
    styleSheet.addRule("h3{color:" + headingCss + ";font-size:150%;}");
    styleSheet.addRule("h4{color:" + headingCss + ";font-size:120%;}");
    styleSheet.addRule("h1 img,h2 img,h3 img{vertical-align:middle;margin-right:5px;}");
    styleSheet.addRule("a:link,a:visited,a:active{color:" + linkCss + ";}");
    styleSheet.addRule("a:link:hover,a:visited:hover,a:active:hover{color:" + linkCss + ";}");
    styleSheet.addRule("a img{border:0;}");

    return kit;

  }

  /**
   * <p>Point the editor pane to the given URL for rendering</p>
   *
   * @param url The URL to render
   */
  private void browse(final URL url) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          try {

            editorPane.setPage(url);

            launchBrowserButton.setBackground(Themes.currentTheme.buttonBackground());

          } catch (IOException e) {
            // Log the error and report a failure to the user via the alerts
            log.error(e.getMessage(), e);
            ControllerEvents.fireAddAlertEvent(
              Models.newAlertModel(
                Languages.safeText(MessageKey.NETWORK_CONFIGURATION_ERROR),
                RAGStatus.AMBER
              ));
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
          Desktop.getDesktop().browse(currentPage().toURI());
        } catch (IOException | URISyntaxException e1) {
          ExceptionHandler.handleThrowable(e1);
        }

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

}
