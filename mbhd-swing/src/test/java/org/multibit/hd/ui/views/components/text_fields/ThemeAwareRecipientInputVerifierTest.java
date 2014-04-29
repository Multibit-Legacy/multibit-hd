package org.multibit.hd.ui.views.components.text_fields;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.select_contact.RecipientComboBoxEditor;

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

    contactService = mock(ContactService.class);

  }

  @Test
  public void testVerifyText() throws Exception {

    List<Contact> allContacts = Lists.newArrayList();

    // Arrange
    when(contactService.allContacts()).thenReturn(allContacts);

    JComboBox<Recipient> comboBox = ComboBoxes.newRecipientComboBox(contactService, BitcoinNetwork.current().get());

    ThemeAwareRecipientInputVerifier testObject = new ThemeAwareRecipientInputVerifier(contactService, BitcoinNetwork.current().get());

    RecipientComboBoxEditor.ComboBoxTextField comboEditor = ((RecipientComboBoxEditor.ComboBoxTextField) comboBox.getEditor().getEditorComponent());

    // Act
    comboBox.setSelectedItem("");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(" ");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgdfjkt");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYa");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");
    assertThat(testObject.verify(comboEditor)).isTrue();

    // Use a public domain P2SH address
    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tUB");
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU");
    assertThat(testObject.verify(comboEditor)).isTrue();


  }

  @Test
  public void testVerifyRecipients() throws Exception {

    final List<Contact> allContacts = Lists.newArrayList();

    // Arrange
    when(contactService.allContacts()).thenReturn(allContacts);

    JComboBox<Recipient> comboBox = ComboBoxes.newRecipientComboBox(contactService, BitcoinNetwork.current().get());

    ThemeAwareRecipientInputVerifier testObject = new ThemeAwareRecipientInputVerifier(contactService, BitcoinNetwork.current().get());

    RecipientComboBoxEditor.ComboBoxTextField comboEditor = ((RecipientComboBoxEditor.ComboBoxTextField) comboBox.getEditor().getEditorComponent());

    // Act
    comboBox.setSelectedItem(new Recipient("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"));
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(new Recipient(" "));
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(new Recipient("AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"));
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(new Recipient("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgdfjkt"));
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(new Recipient("1AhN6rPdrMuKBGFDKR1k9A8SCLYa"));
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(new Recipient("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"));
    assertThat(testObject.verify(comboEditor)).isTrue();

    // Use a public domain P2SH address
    comboBox.setSelectedItem(new Recipient("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1t"));
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(new Recipient("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tUB"));
    assertThat(testObject.verify(comboEditor)).isFalse();

    comboBox.setSelectedItem(new Recipient("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU"));
    assertThat(testObject.verify(comboEditor)).isTrue();

  }

}