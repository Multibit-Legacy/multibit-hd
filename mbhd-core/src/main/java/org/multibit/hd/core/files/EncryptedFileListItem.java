package org.multibit.hd.core.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Keepkey on 10/10/16.
 */
public abstract class EncryptedFileListItem extends File  {
    public EncryptedFileListItem (String path){
        super(path);

    }


    abstract public boolean isValidDecryption(InputStream inputStream) throws IOException ;
}
