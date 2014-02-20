package org.multibit.hd.ui.views.wizards.edit_contact;

import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

import java.util.List;

/**
 * <p>Model object to provide the following to "edit contact" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EditContactWizardModel extends AbstractWizardModel<EditContactState> {

  private final List<Contact> contacts;

  /**
   * @param state    The state object
   * @param contacts The contacts to edit
   */
  public EditContactWizardModel(EditContactState state, List<Contact> contacts) {
    super(state);

    this.contacts = contacts;
  }

  /**
   * @return The edited contacts
   */
  public List<Contact> getContacts() {
    return contacts;
  }

}
