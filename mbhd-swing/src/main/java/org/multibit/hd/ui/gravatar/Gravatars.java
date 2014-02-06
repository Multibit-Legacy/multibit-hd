package org.multibit.hd.ui.gravatar;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * <p>Utility to provide the following to application:</p>
 * <ul>
 * <li>Retrieving images from the Gravatar web service</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Gravatars {

  private static final Logger log = LoggerFactory.getLogger(Gravatars.class);

  // Set the system defaults
  private final static int SIZE = 50;
  private final static String RATING = Rating.GENERAL.getCode();
  private final static String DEFAULT_IMAGE = DefaultImage.MYSTERY_MAN.getCode();

  // Fixed entries
  private final static String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
  private final static String PARAMETERS = "?s=" + SIZE + "&r=" + RATING + "&d=" + DEFAULT_IMAGE;

  /**
   * Utilities have private constructors
   */
  private Gravatars() {
  }

  /**
   * @param emailAddress The email address
   *
   * @return The corresponding image (default if the email address is unknown) or absent if an error occurs
   */
  public static Optional<BufferedImage> retrieveGravatar(String emailAddress) {

    Preconditions.checkNotNull(emailAddress, "'emailAddress' must be present");

    // Require a hex MD5 hash of email address (lowercase) no whitespace
    String emailHash = Hashing
      .md5()
      .hashString(emailAddress.toLowerCase().trim(), Charset.forName("UTF-8"))
      .toString();

    // Create the URL
    final URL url;
    try {
      url = new URL(GRAVATAR_URL + emailHash + ".jpg" + PARAMETERS);
      log.debug("Gravatar lookup: '{}'", url.toString());
    } catch (MalformedURLException e) {
      // This should never happen
      log.error("Gravatar URL malformed", e);
      return Optional.absent();
    }

    try (InputStream stream = url.openStream()) {
      return Optional.of(ImageIO.read(stream));
    } catch (IOException e) {
      // This may happen if no network is available
      log.warn("Gravatar download failed", e);
      return Optional.absent();
    }

  }

}