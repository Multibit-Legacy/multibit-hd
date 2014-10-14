package org.multibit.hd.core.files;

import org.bitcoinj.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Utility to provide the following to file system:</p>
 * <ul>
 * <li>Handling temporary files</li>
 * </ul>
 *
 * @since 0.0.1
 * TODO Consider SecureFiles and Guava's Files utility which is more robust
 */
public class Files {

  private static final Logger log = LoggerFactory.getLogger(Files.class);

  /**
   * Utilities have private constructor
   */
  private Files() {
  }

  /**
   * Write a file from the inputstream to the outputstream
   */
  public static void writeFile(InputStream in, FileOutputStream out)
    throws IOException {
    byte[] buffer = new byte[1024];
    int len;

    while ((len = in.read(buffer)) >= 0)
      out.write(buffer, 0, len);

    in.close();

    out.flush();
    out.getFD().sync();
    out.close();
  }

  /**
   * Saves the input stream first to the given temp file, then renames to the destFile.
   */
  public static void writeFile(InputStream inputStream, File temp, File destFile) throws IOException {
    FileOutputStream tempStream = null;

    try {
      tempStream = new FileOutputStream(temp);
      writeFile(inputStream, tempStream);
      // Attempt to force the bits to hit the disk. In reality the OS or hard disk itself may still decide
      // to not write through to physical media for at least a few seconds, but this is the best we can do.
      tempStream = null;
      if (Utils.isWindows()) {
        // Work around an issue on Windows whereby you can't rename over existing files.
        File canonical = destFile.getCanonicalFile();
        if (canonical.exists() && !canonical.delete()) {
          throw new IOException("Failed to delete canonical wallet file for replacement with autosave");
        }
        if (temp.renameTo(canonical)) return; // else fall through.
        throw new IOException("Failed to rename " + temp + " to " + canonical);
      } else if (!temp.renameTo(destFile)) {
        throw new IOException("Failed to rename " + temp + " to " + destFile);
      }
    } catch (RuntimeException e) {
      log.error("Failed whilst saving wallet", e);
      throw e;
    } finally {
      if (tempStream != null) {
        tempStream.close();
      }
    }
  }
}
