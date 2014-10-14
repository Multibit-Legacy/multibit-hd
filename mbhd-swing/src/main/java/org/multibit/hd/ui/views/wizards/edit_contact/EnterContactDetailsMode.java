package org.multibit.hd.ui.views.wizards.edit_contact;

import org.multibit.hd.ui.languages.MessageKey;

/**
 * <p>Enum to provide the following to "edit contact" wizard:</p>
 * <ul>
 * <li>Modes of operation (mew, edit single, edit multiple)</li>
 * <li>Appropriate titles for the panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum EnterContactDetailsMode {

  NEW(MessageKey.NEW_CONTACT_TITLE),
  EDIT_SINGLE(MessageKey.EDIT_CONTACT_TITLE),
  EDIT_MULTIPLE(MessageKey.EDIT_CONTACTS_TITLE);

  private final MessageKey messageKey;

  EnterContactDetailsMode(MessageKey messageKey) {

    this.messageKey = messageKey;
  }

  public MessageKey getMessageKey() {
    return messageKey;
  }
}

