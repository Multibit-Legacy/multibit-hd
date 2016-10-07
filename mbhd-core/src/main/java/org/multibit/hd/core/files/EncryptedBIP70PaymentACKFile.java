package org.multibit.hd.core.files;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bitcoin.protocols.payments.Protos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Keepkey on 10/10/16.
 */
public class EncryptedBIP70PaymentACKFile extends EncryptedFileListItem implements EncryptedFile {
    public EncryptedBIP70PaymentACKFile (String fileName){
        super(fileName);
    }

    @Override
    public boolean isValidDecryption(InputStream inputStream) throws IOException {
        try{
            Protos.PaymentACK.parseFrom(inputStream);
            return true;
        }

        catch(InvalidProtocolBufferException ex){
            return false;
        }
    }
}