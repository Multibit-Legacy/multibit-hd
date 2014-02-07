package org.multibit.hd.ui.views.screens.contacts;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.Tables;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the contact detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactsPanelView extends AbstractScreenView<ContactsPanelModel> {

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ContactsPanelView(ContactsPanelModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel newScreenViewPanel() {

    MigLayout layout = new MigLayout(
      "fill,insets 0", // Layout constraints
      "[][]", // Column constraints
      "[][]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action searchContactsAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newSendBitcoinWizard().getWizardPanel());
      }
    };

    Action newContactAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newReceiveBitcoinWizard().getWizardPanel());
      }
    };

    Action editContactAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newReceiveBitcoinWizard().getWizardPanel());
      }
    };

    Action deleteSelectedAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newReceiveBitcoinWizard().getWizardPanel());
      }
    };

    ContactService contactService = CoreServices.getContactService();

    JTable table = Tables.newContactsTable(contactService.allContacts(1, 10));

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    contentPanel.add(new JLabel("Contacts"),"growx,push,wrap");
    contentPanel.add(scrollPane,"grow,push");

    return contentPanel;
  }
}
