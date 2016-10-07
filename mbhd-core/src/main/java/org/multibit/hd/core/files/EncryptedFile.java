package org.multibit.hd.core.files;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Keepkey on 10/10/16.
 */
public interface EncryptedFile {
    public boolean isValidDecryption(InputStream inputStream)throws IOException;
}
