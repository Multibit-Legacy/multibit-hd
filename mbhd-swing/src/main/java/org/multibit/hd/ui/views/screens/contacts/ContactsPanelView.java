package org.multibit.hd.ui.views.screens.contacts;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchModel;
import org.multibit.hd.ui.views.components.enter_search.EnterSearchView;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the contact detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactsPanelView extends AbstractScreenView<ContactsPanelModel> implements ActionListener {

  // View components
  private ModelAndView<EnterSearchModel, EnterSearchView> enterSearchMaV;
  private JComboBox<String> checkSelectorComboBox;
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;

  private JTable contactsTable;
  private ContactTableModel contactTableModel;


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
      "fill,insets 10 5 0 0", // Layout constraints
      "[][][][]push[]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    // Create view components
    enterSearchMaV = Components.newEnterSearchMaV(getScreen().name());
    checkSelectorComboBox = ComboBoxes.newContactsCheckboxComboBox(this);
    addButton = Buttons.newAddButton(getAddAction());
    editButton = Buttons.newEditButton(getEditAction());
    deleteButton = Buttons.newDeleteButton(getDeleteAction());

    getSearchAction();

    ContactService contactService = CoreServices.getOrCreateContactService(getCurrentWalletId());

    contactsTable = Tables.newContactsTable(contactService.allContacts(1, 10));
    contactTableModel = (ContactTableModel) contactsTable.getModel();

    // Detect clicks on the table
    contactsTable.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {

        if (e.getClickCount() > 0) {
          JTable target = (JTable) e.getSource();
          int row = target.getSelectedRow();
          int column = target.getSelectedColumn();

        }
      }

    });


    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(contactsTable);
    scrollPane.setViewportBorder(null);

    // Add to the panel
    contentPanel.add(enterSearchMaV.getView().newComponentPanel(), "span 5,growx,push,wrap");
    contentPanel.add(checkSelectorComboBox, "shrink");
    contentPanel.add(addButton, "shrink");
    contentPanel.add(editButton, "shrink");
    contentPanel.add(deleteButton, "shrink");
    contentPanel.add(new JLabel(""), "grow,push,wrap");
    contentPanel.add(scrollPane, "span 5,grow,push");

    return contentPanel;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        enterSearchMaV.getView().requestInitialFocus();
      }
    });

  }

  // TODO Move this into a wallet service
  private WalletId getCurrentWalletId() {
    if (WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {
      return WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId();
    }

    return new WalletId("66666666-77777777-88888888-99999999-aaaaaaaa");
  }

  /**
   * @return The search contact action
   */
  private Action getSearchAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {


      }
    };
  }

  /**
   * @return The add contact action
   */
  public Action getAddAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {


      }
    };
  }

  /**
   * @return The edit contact action
   */
  public Action getEditAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {


      }
    };
  }

  /**
   * @return The delete contact action
   */
  public Action getDeleteAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {


      }
    };
  }


  @Override
  public void actionPerformed(ActionEvent e) {

    // User has selected from the checkboxes so interpret the result
    int checkSelectorIndex = checkSelectorComboBox.getSelectedIndex();

    ContactTableModel model = (ContactTableModel) contactsTable.getModel();

    model.updateSelectionCheckboxes(checkSelectorIndex);

  }

}
