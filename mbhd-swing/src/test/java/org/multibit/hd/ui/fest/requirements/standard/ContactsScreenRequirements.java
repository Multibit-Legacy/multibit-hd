package org.multibit.hd.ui.fest.requirements.standard;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.contacts.*;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "contacts" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ContactsScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Select the contacts screen
    new ShowContactsScreenUseCase(window).execute(parameters);

    // Click Add then immediate Cancel
    new AddThenCancelContactUseCase(window).execute(parameters);

    // Click Add and fill in "Alice"
    new AddAliceContactUseCase(window).execute(parameters);

    // Click Add and fill in "Bob"
    new AddBobContactUseCase(window).execute(parameters);

    // Press Enter on Bob but then Cancel
    new EditThenCancelBobContactKeyboardUseCase(window).execute(parameters);

    // Click Edit and update Bob's extra info
    new EditBobContactUseCase(window).execute(parameters);

    // Click Edit and fill in some extra info on Bob but then Cancel
    //new EditThenCancelBobContactUseCase(window).execute(parameters);

    // Select Alice and Bob then use multi-edit
    new EditAliceAndBobContactUseCase(window).execute(parameters);

    // Click Add and fill in "Uriah"
    new AddUriahContactUseCase(window).execute(parameters);

    // Select Uriah and Click Delete
    new DeleteUriahContactUseCase(window).execute(parameters);

    // Click Undo and restore Uriah
    new UndoUriahContactUseCase(window).execute(parameters);

    // Search for some entries
    new SearchContactUseCase(window).execute(parameters);

  }
}
