/**
 * Copyright 2014 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Based on the WalletProtobufSerialiser written by Miron Cuperman, copyright Google (also MIT licence)
 */

package org.multibit.hd.core.store;

import com.google.bitcoin.core.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.protobuf.TextFormat;
import org.multibit.contact.MBHDProtos;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.exceptions.ContactsLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Serialize and de-serialize contacts to a byte stream containing a
 * <a href="http://code.google.com/apis/protocolbuffers/docs/overview.html">protocol buffer</a>. Protocol buffers are
 * a data interchange format developed by Google with an efficient binary representation, a type safe specification
 * language and compilers that generate code to work with those data structures for many languages. Protocol buffers
 * can have their format evolved over time: conceptually they represent data using (tag, length, value) tuples. The
 * format is defined by the <tt>bitcoin.proto</tt> file in the bitcoinj source distribution.<p>
 *
 * This class is used through its static methods. The most common operations are writeContacts and readContacts, which do
 * the obvious operations on Output/InputStreams. You can use a {@link java.io.ByteArrayInputStream} and equivalent
 * {@link java.io.ByteArrayOutputStream} if you'd like byte arrays instead. The protocol buffer can also be manipulated
 * in its object form if you'd like to modify the flattened data structure before serialization to binary.<p>
 * 
 * @author Miron Cuperman
 * @author Jim Burton
 */
public class ContactsProtobufSerializer {
    private static final Logger log = LoggerFactory.getLogger(org.multibit.hd.core.store.ContactsProtobufSerializer.class);


    public ContactsProtobufSerializer() {
    }


    /**
     * Formats the given Contacts to the given output stream in protocol buffer format.<p>
     */
    public void writeContacts(Set<Contact> contacts, OutputStream output) throws IOException {
        MBHDProtos.Contacts contactsProto = contactsToProto(contacts);
        contactsProto.writeTo(output);
    }

    /**
     * Returns the given contacts formatted as text. The text format is that used by protocol buffers and although it
     * can also be parsed using {@link TextFormat#merge(CharSequence, com.google.protobuf.Message.Builder)},
     * it is designed more for debugging than storage. It is not well specified and wallets are largely binary data
     * structures anyway, consisting as they do of keys (large random numbers) and {@link Transaction}s which also
     * mostly contain keys and hashes.
     */
    public String contactsToText(Set<Contact> contacts) {
      MBHDProtos.Contacts contactsProto = contactsToProto(contacts);
        return TextFormat.printToString(contactsProto);
    }

    /**
     * Converts the given contacts to the object representation of the protocol buffers. This can be modified, or
     * additional data fields set, before serialization takes place.
     */
    public MBHDProtos.Contacts contactsToProto(Set<Contact> contacts) {
      MBHDProtos.Contacts.Builder contactsBuilder = MBHDProtos.Contacts.newBuilder();

      Preconditions.checkNotNull(contacts, "Contacts must be specified");

      for (Contact contact : contacts) {
        MBHDProtos.Contact contactProto = makeContactProto(contact);
       contactsBuilder.addContact(contactProto);
      }

      return contactsBuilder.build();
    }

  private static MBHDProtos.Contact makeContactProto(Contact contact) {
    MBHDProtos.Contact.Builder contactBuilder = MBHDProtos.Contact.newBuilder();
    contactBuilder.setId(contact.getId().toString());
    contactBuilder.setName(contact.getName());
    contactBuilder.setBitcoinAddress(contact.getBitcoinAddress().or(""));
    contactBuilder.setEmail(contact.getEmail().or(""));
    contactBuilder.setImagePath(contact.getImagePath().or(""));
    contactBuilder.setExtendedPublicKey(contact.getExtendedPublicKey().or(""));
    contactBuilder.setNotes(contact.getNotes().or(""));

    // Construct tags
    List<String> tags = contact.getTags();
    if (tags != null) {
      int tagIndex = 0;
      for (String tag : tags) {
        MBHDProtos.Tag tagProto = makeTagProto(tag);
        contactBuilder.addTag(tagIndex, tagProto);
        tagIndex++;
      }
    }

    return contactBuilder.build();
  }

  private static MBHDProtos.Tag makeTagProto(String tag) {
    MBHDProtos.Tag.Builder tagBuilder = MBHDProtos.Tag.newBuilder();
    tagBuilder.setTagValue(tag);
    return tagBuilder.build();
  }

    /**
     * <p>Parses a Contacts from the given stream, using the provided Contacts instance to load data into.
     * <p>A Contacts db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
     * inconsistent data, You should always
     * handle {@link org.multibit.hd.core.exceptions.ContactsLoadException} and communicate failure to the user in an appropriate manner.</p>
     *
     * @throws ContactsLoadException thrown in various error conditions (see description).
     */
    public Set<Contact> readContacts(InputStream input) throws ContactsLoadException {
        try {
            MBHDProtos.Contacts contactsProto = parseToProto(input);
            Set<Contact> contacts = Sets.newHashSet();
            readContacts(contactsProto, contacts);
            return contacts;
        } catch (IOException e) {
            throw new ContactsLoadException("Could not parse input stream to protobuf", e);
        }
    }

    /**
     * <p>Loads contacts data from the given protocol buffer and inserts it into the given Set of Contact object.
     *
     * <p>A contact db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
     * inconsistent data, a wallet extension marked as mandatory that cannot be handled and so on. You should always
     * handle {@link ContactsLoadException} and communicate failure to the user in an appropriate manner.</p>
     *
     * @throws ContactsLoadException thrown in various error conditions (see description).
     */
    private void readContacts(MBHDProtos.Contacts contactsProto, Set<Contact> contacts) throws ContactsLoadException {
      Set<Contact> readContacts = Sets.newHashSet();

      List<MBHDProtos.Contact>contactProtos = contactsProto.getContactList();

      if (contactProtos != null) {
        for (MBHDProtos.Contact contactProto : contactProtos) {
          String idAsString = contactProto.getId();
          UUID id = UUID.fromString(idAsString);

          String name = contactProto.getName();

          Contact contact = new Contact(id, name);

          contact.setEmail(contactProto.getEmail());
          contact.setBitcoinAddress(contactProto.getBitcoinAddress());
          contact.setImagePath(contactProto.getImagePath());
          contact.setExtendedPublicKey(contactProto.getExtendedPublicKey());
          contact.setNotes(contactProto.getNotes());

          // Create tags
          List<String> tags = Lists.newArrayList();
          List<MBHDProtos.Tag> tagProtos = contactProto.getTagList();
          if (tagProtos != null) {
            for (MBHDProtos.Tag tagProto : tagProtos) {
              tags.add(tagProto.getTagValue());
            }
          }
          contact.setTags(tags);
          readContacts.add(contact);
        }
      }

      // Everything read ok - put the new contacts into the passed in contacts object
      contacts.clear();
      contacts.addAll(readContacts);
    }

    /**
     * Returns the loaded protocol buffer from the given byte stream. This method is designed for low level work involving the
     * wallet file format itself.
     */
    public static MBHDProtos.Contacts parseToProto(InputStream input) throws IOException {
        return MBHDProtos.Contacts.parseFrom(input);
    }
}
