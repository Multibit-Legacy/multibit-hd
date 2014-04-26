package org.multibit.hd.ui.views.components.text_fields;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilter;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilters;

import javax.swing.*;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThemeAwareRecipientInputVerifierTest {

  private ContactService contactService;

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    contactService= mock(ContactService.class);

  }

  @Test
  public void testVerifyText() throws Exception {

    List<Contact> allContacts = Lists.newArrayList();

    // Arrange
    when(contactService.allContacts()).thenReturn(allContacts);

    AutoCompleteFilter<Recipient> filter = AutoCompleteFilters.newRecipientFilter(contactService);
    JComboBox<Recipient> comboBox = ComboBoxes.newRecipientComboBox(filter);

    ThemeAwareRecipientInputVerifier testObject = new ThemeAwareRecipientInputVerifier();

    // Act
    comboBox.setSelectedItem("");
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(" ");
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgdfjkt");
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYa");
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");
    assertThat(testObject.verify(comboBox)).isTrue();

    // Use a public domain P2SH address
    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t");
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tUB");
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU");
    assertThat(testObject.verify(comboBox)).isTrue();


  }

  @Test
  public void testVerifyRecipients() throws Exception {

    final List<Contact> allContacts = Lists.newArrayList();

    // Arrange
    when(contactService.allContacts()).thenReturn(allContacts);

    AutoCompleteFilter<Recipient> filter = AutoCompleteFilters.newRecipientFilter(contactService);
    JComboBox<Recipient> comboBox = ComboBoxes.newRecipientComboBox(filter);

    ThemeAwareRecipientInputVerifier testObject = new ThemeAwareRecipientInputVerifier();

    // Act
    comboBox.setSelectedItem(new Recipient("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"));
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(new Recipient(" "));
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(new Recipient("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"));
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(new Recipient("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgdfjkt"));
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(new Recipient("1AhN6rPdrMuKBGFDKR1k9A8SCLYa"));
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(new Recipient("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"));
    assertThat(testObject.verify(comboBox)).isTrue();

    // Use a public domain P2SH address
    comboBox.setSelectedItem(new Recipient("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t"));
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(new Recipient("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tUB"));
    assertThat(testObject.verify(comboBox)).isFalse();

    comboBox.setSelectedItem(new Recipient("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU"));
    assertThat(testObject.verify(comboBox)).isTrue();

  }

}