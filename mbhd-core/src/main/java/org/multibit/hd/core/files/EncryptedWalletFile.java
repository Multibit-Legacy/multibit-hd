package org.multibit.hd.core.files;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.store.WalletProtobufSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Keepkey on 10/10/16.
 */
public class EncryptedWalletFile extends EncryptedFileListItem{
    public EncryptedWalletFile (String fileName){
        super(fileName);
    }

    @Override
    public boolean isValidDecryption(InputStream inputStream) throws IOException {
        try{
            Protos.Wallet walletProto = WalletProtobufSerializer.parseToProto(inputStream);
            return true;
        }

        catch(InvalidProtocolBufferException ex){
            return false;
        }
    }
    public static boolean isParseable(byte[] decryptedBytes) throws IOException{
        InputStream inputStream = new ByteArrayInputStream(decryptedBytes);
        try{
            Protos.Wallet walletProto = WalletProtobufSerializer.parseToProto(inputStream);
            return true;
        }

        catch(InvalidProtocolBufferException ex){
            return false;
        }

    }
}