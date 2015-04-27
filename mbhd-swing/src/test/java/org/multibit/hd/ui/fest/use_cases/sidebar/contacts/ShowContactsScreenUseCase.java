package org.multibit.hd.ui.fest.use_cases.sidebar.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ShowContactsScreenUseCase extends AbstractFestUseCase {

  public ShowContactsScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(CONTACTS_ROW);

    // Expect the Contacts screen to show
    window
      .textBox(MessageKey.SEARCH.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SEARCH.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .comboBox(MessageKey.CONTACTS.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.EDIT.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.DELETE.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.UNDO.getKey())
      .requireVisible()
      .requireDisabled();

    window
      .table(MessageKey.CONTACTS.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
