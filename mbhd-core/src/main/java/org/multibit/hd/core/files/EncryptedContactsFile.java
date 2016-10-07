package org.multibit.hd.core.files;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.multibit.hd.core.store.ContactsProtobufSerializer;

/**
 * Created by Keepkey on 10/10/16.
 */
public class EncryptedContactsFile extends EncryptedFileListItem implements EncryptedFile {
    public EncryptedContactsFile(String fileName){
        super(fileName);
    }

    @Override
    public boolean isValidDecryption(InputStream inputStream)throws IOException {
        try{
            ContactsProtobufSerializer.parseToProto(inputStream);
            return true;
        }

    catch(InvalidProtocolBufferException ex){
        return false;
    }
    }

}
