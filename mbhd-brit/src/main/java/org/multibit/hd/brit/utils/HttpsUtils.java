package org.multibit.hd.brit.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>Utility to provide the following to HTTPS application code:</p>
 * <ul>
 * <li>Easy access to HTTPS operations</li>
 * </ul>
 *
 * @since 0.1.0
 *  
 */
public class HttpsUtils {

  private static final Logger log = LoggerFactory.getLogger(HttpsUtils.class);

  /**
   * Utilities have a private constructor
   */
  private HttpsUtils() {
  }

  /**
   * Perform a post of the specified bytes to the specified URL
   *
   * @param url         The URL to post to
   * @param payload     the bytes to post
   * @param contentType The content type (e.g. "application/octet-stream")
   *
   * @return The bytes in the response payload
   */
  public static byte[] doPost(URL url, byte[] payload, String contentType) throws IOException {

    // Send the encrypted request to the endpoint
    log.debug("Attempting to post {} bytes to URL {}", payload.length, url);
    URLConnection connection;

    // URL connection channel
    connection = url.openConnection();

    // Configure the request
    connection.setRequestProperty("Content-Length", String.valueOf(payload.length));
    connection.setRequestProperty("Content-Type", contentType);
    connection.setRequestProperty("Accept", "*/*");
    // Let the server know that we want input
    connection.setDoInput(true);
    // Let the server know that we want to do output
    connection.setDoOutput(true);
    // No caching, we want the real thing
    connection.setUseCaches(false);

    // Try to send the POST output
    try (DataOutputStream postOutputStream = new DataOutputStream(connection.getOutputStream())) {
      postOutputStream.write(payload);
      postOutputStream.flush();
    }

    // Try to GET the response data
    try (DataInputStream responseInputStream = new DataInputStream(connection.getInputStream());
         ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream(1024)) {

      byte readByte;

      boolean keepGoing = true;
      while (keepGoing) {
        try {
          readByte = responseInputStream.readByte();
          responseOutputStream.write(readByte);
        } catch (IOException ioe) {
          // response is all read (EOFException) or has fallen over
          keepGoing = false;
        }
      }
      return responseOutputStream.toByteArray();
    }

  }

}
