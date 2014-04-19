package org.multibit.hd.ui.fest.use_cases.sidebar_screens;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactsScreenUseCase extends AbstractFestUseCase {

  public ContactsScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(3);

    // Expect the Contacts screen to show
    window
      .button(MessageKey.SEARCH.getKey())
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
      .requireEnabled();

    window
      .table(MessageKey.CONTACTS.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
