package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.contacts.AddContactUseCase;
import org.multibit.hd.ui.fest.use_cases.contacts.ShowContactsScreenUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the contact screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactsScreen {

  public static void verifyUsing(FrameFixture window) {

    Map<String,Object> parameters = Maps.newHashMap();

    // Select the contacts screen
    new ShowContactsScreenUseCase(window).execute(parameters);

    // Add a new contact
    new AddContactUseCase(window).execute(parameters);

  }
}
