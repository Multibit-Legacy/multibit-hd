package org.multibit.hd.ui.fest.use_cases.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen add Uriah contact</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class AddUriahContactUseCase extends AbstractFestUseCase {

  public AddUriahContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Click on Add
    window
      .button(MessageKey.ADD.getKey())
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.NEW_CONTACT_TITLE);

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Fill in Uriah Heep's details
    window
      .textBox(MessageKey.NAME.getKey())
      .setText("Uriah Heep");

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    // Allow time for persistence to complete
    pauseForDataPersistence();

    // Verify the underlying screen is back
    window
      .button(MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Verify a new row has been added
    assertThat(rowCount2).isEqualTo(rowCount1 + 1);

  }

}
