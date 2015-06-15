package org.multibit.hd.ui.gravatar;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.joda.time.DateTime;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.Models;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

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
  private final static int SIZE = MultiBitUI.LARGE_ICON_SIZE;
  private final static String RATING = Rating.GENERAL.getCode();
  private final static String DEFAULT_IMAGE = DefaultImage.MYSTERY_MAN.getCode();

  // Fixed entries
  private final static String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
  private final static String PARAMETERS = "?s=" + SIZE + "&r=" + RATING + "&d=" + DEFAULT_IMAGE;

  // Maintain a multi-threaded shared reference to a failure mode
  private static AtomicReference<Optional<DateTime>> lastFailedDownload = new AtomicReference<>(Optional.<DateTime>absent());

  // Keep an image thread pool
  private static final ListeningExecutorService gravatarExecutorService = SafeExecutors.newFixedThreadPool(10, "gravatar");

  // Maintain an image cache
  private static LoadingCache<String, Optional<BufferedImage>> cache = CacheBuilder
    .newBuilder()
    .maximumSize(1000)
    .build(new CacheLoader<String, Optional<BufferedImage>>() {
      @Override
      public Optional<BufferedImage> load(String cleanEmailAddress) throws Exception {

        // Get the image synchronously (the overall cache call is wrapped in an executor)
        return loadBufferedImage(cleanEmailAddress);

      }
    });

  /**
   * Utilities have private constructors
   */
  private Gravatars() {
  }

  /**
   * <p>Non-blocking call to retrieve a gravatar and provide notification on success or failure</p>
   *
   * @param emailAddress The email address
   *
   * @return A listenable future containing the corresponding image (default if the email address is unknown) or absent if an error occurs
   */
  public static ListenableFuture<Optional<BufferedImage>> retrieveGravatar(final String emailAddress) {

    Preconditions.checkNotNull(emailAddress, "'emailAddress' must be present");

    final String cleanEmailAddress = emailAddress.toLowerCase().trim();

    return gravatarExecutorService.submit(new Callable<Optional<BufferedImage>>() {

      @Override
      public Optional<BufferedImage> call() throws Exception {
        return cache.get(cleanEmailAddress);
      }
    });

  }

  /**
   * @param emailAddress The cleaned email address to use as an MD5 lookup
   *
   * @return The buffered image if present
   */
  private static Optional<BufferedImage> loadBufferedImage(String emailAddress) {

    log.debug("Loading image from external resource");

    // Require a hex MD5 hash of email address (lowercase) no whitespace
    final String emailHash = Hashing
      .md5()
      .hashString(emailAddress, Charsets.UTF_8)
      .toString();

    // Create the URL
    final URL url;
    try {
      url = new URL(GRAVATAR_URL + emailHash + ".jpg" + PARAMETERS);
    } catch (MalformedURLException e) {
      // This should never happen
      log.error("Gravatar URL malformed", e);
      return Optional.absent();
    }

    try (InputStream stream = url.openStream()) {
      return Optional.of(ImageIO.read(stream));
    } catch (IOException e) {
      // This may happen if no network is available
      log.warn("Gravatar download failed" + e.getMessage());

      // Avoid flooding the user with failure alerts
      DateTime now = Dates.nowUtc();
      if (lastFailedDownload.get().isPresent()) {
        DateTime lastFailure = lastFailedDownload.get().get();
        if (lastFailure.plusMinutes(1).isBefore(now)) {
          // It's been a while since we had a failure so OK to notify the user again
          ControllerEvents.fireAddAlertEvent(Models.newAlertModel(Languages.safeText(MessageKey.BITCOIN_NETWORK_CONFIGURATION_ERROR), RAGStatus.AMBER));
        }
      }
      lastFailedDownload.set(Optional.of(now));

      return Optional.absent();
    }
  }

}