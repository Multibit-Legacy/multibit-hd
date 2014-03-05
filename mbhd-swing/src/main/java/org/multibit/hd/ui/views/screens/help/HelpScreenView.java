package org.multibit.hd.ui.views.screens.help;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.io.IOException;
import java.net.URI;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the help detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HelpScreenView extends AbstractScreenView<HelpScreenModel> {

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
  public JPanel newScreenViewPanel() {

    MigLayout layout = new MigLayout(
      Panels.migLayout("fill,insets 10 5 0 0"),
      "[]", // Column constraints
      "[]" // Row constraints
    );

    // Create the content panel
    JPanel contentPanel = Panels.newPanel(layout);

    // Create an editor pane to wrap the HTML editor kit
    final JEditorPane editorPane = new JEditorPane();

    // Make it read-only to allow links to be followed
    editorPane.setEditable(false);

    // Create an HTML editor kit
    HTMLEditorKit kit = new HTMLEditorKit();
    editorPane.setEditorKit(kit);

    // Set a basic style sheet
    StyleSheet styleSheet = kit.getStyleSheet();

    // Avoid setting the background here since it can bleed through the look and feel
    styleSheet.addRule("body{font-family:\"Helvetica Neue\",\"Liberation Sans\",Arial,sans-serif;margin:0;padding:0;}");
    styleSheet.addRule("h1,h2{font-family:\"Helvetica Neue\",\"Liberation Sans\",Arial,sans-serif;font-weight:normal;}");
    styleSheet.addRule("h1{color:#973131;font-size:150%;}");
    styleSheet.addRule("h2{color:#973131;font-size:125%;}");
    styleSheet.addRule("h3{color:#973131;font-size:100%;}");
    styleSheet.addRule("h1 img,h2 img,h3 img{vertical-align:middle;margin-right:5px;}");
    styleSheet.addRule("a:link,a:visited,a:active{color:#973131;}");
    styleSheet.addRule("a:link:hover,a:visited:hover,a:active:hover{color:#973131;}");
    styleSheet.addRule("a img{border:0;}");

    // Look up the standard MultiBit help (via HTTP)
    URI uri = URI.create("http://multibit.org/v0.5/help_contents.html");

    // Create a default HTML document for the editor pane
    Document doc = kit.createDefaultDocument();
    editorPane.setDocument(doc);

    // TODO More robust error handling required
    try {
      editorPane.setPage(uri.toURL());
    } catch (IOException e) {
      e.printStackTrace();
    }

    editorPane.addHyperlinkListener(new HyperlinkListener() {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {

        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

          // User has clicked on the link so visit the page
          try {
            System.out.println(Resources.toString(e.getURL(), Charsets.UTF_8));
            editorPane.setPage(e.getURL());
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }

      }
    });

    // Create the scroll pane and add the HTML editor pane to it
    JScrollPane scrollPane = new JScrollPane(editorPane);
    scrollPane.setViewportBorder(null);

    // Add to the panel
    contentPanel.add(scrollPane, "grow,push");

    return contentPanel;
  }

  @Override
  public void afterShow() {

  }

}
