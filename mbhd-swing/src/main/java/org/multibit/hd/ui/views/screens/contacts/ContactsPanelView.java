package org.multibit.hd.ui.views.screens.contacts;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Panels;
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
   * @param panelModel The wizard model managing the states
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of the wizard panel
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
      "fill", // Layout constraints
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
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

    //contentPanel.add(Components.newEnterRecipientMaV(searchContactsAction), "w 240,h 200,align center,push");
    contentPanel.add(Buttons.newReceiveBitcoinWizardButton(newContactAction), "w 240, h 200,align center,push,wrap");
    contentPanel.add(Components.newWalletDetailPanel(), "span 2,grow");

    return contentPanel;
  }
}
