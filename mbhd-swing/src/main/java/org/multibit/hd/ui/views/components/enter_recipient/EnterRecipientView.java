package org.multibit.hd.ui.views.components.enter_recipient;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.api.Recipient;
import org.multibit.hd.ui.views.AbstractView;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilter;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilters;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a dual-purpose combobox</li>
 * <li>Support for locating contacts by name</li>
 * <li>Support for entering recipient Bitcoin address representations (address, seed, key etc)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterRecipientView extends AbstractView<EnterRecipientModel> {

  // View components
  private JComboBox<Contact> recipientComboBox;

  /**
   * @param model The model backing this view
   */
  public EnterRecipientView(EnterRecipientModel model) {
    super(model);

  }

  @Override
  public JPanel newPanel() {

    AutoCompleteFilter<Contact> filter = AutoCompleteFilters.newContactFilter();
    recipientComboBox = ComboBoxes.newRecipientComboBox(filter);
    recipientComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateModel();
      }
    });

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[][][]", // Columns
      "[]" // Rows
    ));

    panel.add(Labels.newRecipient());
    // Specify minimum width for consistent appearance across contact names and locales
    panel.add(recipientComboBox, "growx,w min:350:,push");
    panel.add(Labels.newWalletImageLabel(""), "shrink,wrap");

    return panel;

  }

  @Override
  public void updateModel() {

    // TODO Add in support for real address/contact
    Recipient recipient = new Recipient("1abc123");

    getModel().get().setValue(recipient);
  }

}
