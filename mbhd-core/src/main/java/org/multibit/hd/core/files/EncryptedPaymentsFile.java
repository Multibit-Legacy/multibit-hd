package org.multibit.hd.core.files;

import com.google.protobuf.InvalidProtocolBufferException;
import org.multibit.hd.core.store.PaymentsProtobufSerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Keepkey on 10/10/16.
 */
public class EncryptedPaymentsFile extends EncryptedFileListItem  {
    public EncryptedPaymentsFile(String fileName){
        super(fileName);
    }


    @Override
    public boolean isValidDecryption(InputStream inputStream) throws IOException {
        try{
            PaymentsProtobufSerializer.parseToProto(inputStream);
            return true;
        }

        catch(InvalidProtocolBufferException ex){
            return false;
        }
    }
}

