package org.multibit.hd.core.atom;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.multibit.commons.concurrent.SafeExecutors;

import javax.xml.bind.JAXB;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

/**
 * <p>Utilities to provide the following to application:</p>
 * <ul>
 * <li>Parsing Atom feeds</li>
 * </ul>
 *
 * @since 0.1.5
 */
public class AtomFeeds {

  /**
   * Utilities have private constructor
   */
  private AtomFeeds() {
  }

  /**
   * <p>Perform an asynchronous parse of the MultiBit.org Atom XML feed using JAXB</p>
   *
   * @return A listenable future containing the result of the asynchronous read
   */
  public static ListenableFuture<AtomFeed> parseMultiBitOrgFeed() {

    ListeningExecutorService executorService = SafeExecutors.newSingleThreadExecutor("atom-feed-check");

    return executorService.submit(new Callable<AtomFeed>() {
      @Override
      public AtomFeed call() throws Exception {

        URL url = new URL("https://multibit.org/atom.xml");
        URLConnection connection = url.openConnection();

        try (InputStream is = connection.getInputStream()) {
          return JAXB.unmarshal(is, AtomFeed.class);
        }

      }
    });

  }

}
