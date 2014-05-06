package org.multibit.hd.core.files;

import com.google.bitcoin.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;

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


  // TODO Remove this
  public static void copyFile(File sourceFile, File destinationFile) throws IOException {
    if (!destinationFile.exists()) {
      destinationFile.createNewFile();
    }
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    FileChannel source = null;
    FileChannel destination = null;
    try {
      fileInputStream = new FileInputStream(sourceFile);
      source = fileInputStream.getChannel();
      fileOutputStream = new FileOutputStream(destinationFile);
      destination = fileOutputStream.getChannel();
      long transferred = 0;
      long bytes = source.size();
      while (transferred < bytes) {
        transferred += destination.transferFrom(source, 0, source.size());
        destination.position(transferred);
      }
    } finally {
      if (source != null) {
        source.close();
      } else if (fileInputStream != null) {
        fileInputStream.close();
      }
      if (destination != null) {
        destination.close();
      } else if (fileOutputStream != null) {
        fileOutputStream.flush();
        fileOutputStream.close();
      }
    }
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
        canonical.delete();
        if (temp.renameTo(canonical))
          return;  // else fall through.
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
