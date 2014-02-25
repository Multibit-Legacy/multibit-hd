package org.multibit.hd.ui.views.wizards.edit_history;

import org.multibit.hd.ui.i18n.MessageKey;

/**
 * <p>Enum to provide the following to "edit history" wizard:</p>
 * <ul>
 * <li>Modes of operation (mew, edit single, edit multiple)</li>
 * <li>Appropriate titles for the panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum EnterHistoryDetailsMode {

  NEW(MessageKey.NEW_HISTORY_ENTRY_TITLE),
  EDIT_SINGLE(MessageKey.EDIT_HISTORY_ENTRY_TITLE),
  EDIT_MULTIPLE(MessageKey.EDIT_HISTORY_ENTRIES_TITLE);

  private final MessageKey messageKey;

  EnterHistoryDetailsMode(MessageKey messageKey) {

    this.messageKey = messageKey;
  }

  public MessageKey getMessageKey() {
    return messageKey;
  }
}

