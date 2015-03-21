package org.multibit.hd.core.testing.payments;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.hardware.core.concurrent.SafeExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Fail.fail;

/**
 * <p>A HTTPS server listening on localhost:8443 and responding with Payment Protocol requests created from fixtures</p>
 *
 * <p>To debug SSL include <code>-Djavax.net.debug=ssl:handshake</code> as a VM command line parameter</p>
 *
 */
public class PaymentProtocolHttpsServer {

  private static final Logger log = LoggerFactory.getLogger(PaymentProtocolHttpsServer.class);

  private SSLServerSocket serverSocket;

  private static ListeningExecutorService executorService = SafeExecutors.newSingleThreadExecutor("bip70-server");

  /**
   * @return True if the server started OK
   */
  public boolean start() {

    InputStream is = null;
    try {
      log.debug("Initialise the trust store containing the trusted certificates (including localhost:8443)");
      URL trustStoreUrl = PaymentProtocolHttpsServer.class.getResource("/mbhd-cacerts-with-localhost");
      System.setProperty("javax.net.ssl.trustStore", trustStoreUrl.getFile());
      System.setProperty("javax.net.ssl.trustStorePassword", SSLManager.PASSPHRASE);

      SSLContext sslContext = SSLContext.getInstance("TLS");

      log.debug("Initialise the key store containing the private server keys (CN=localhost is required)");
      KeyStore ks = KeyStore.getInstance("JKS");
      is = PaymentProtocolHttpsServer.class.getResourceAsStream("/localhost.jks");
      ks.load(is, SSLManager.PASSPHRASE.toCharArray());

      log.debug("Initialise the key manager factory");
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, SSLManager.PASSPHRASE.toCharArray());

      log.debug("Initialise the trust manager factory");
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      tmf.init(ks);

      // Setup the HTTPS context and parameters
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

      // Create a ServerSocketFactory from the SSLContext
      ServerSocketFactory ssf = sslContext.getServerSocketFactory();

      // Create unauthenticated server socket on localhost:8443
      serverSocket = (SSLServerSocket) ssf.createServerSocket(8443);
      serverSocket.setNeedClientAuth(false);
      serverSocket.setWantClientAuth(false);
      String[] suites = serverSocket.getSupportedCipherSuites();
      serverSocket.setEnabledCipherSuites(suites);

      return true;

    } catch (Exception e) {
      log.error("Failed to create HTTPS server", e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException ioe) {
          log.error("Failed to close key store", ioe);
        }
      }
    }

    // Must have failed to be here
    return false;

  }

  /**
   * @param fixture The classpath reference of the fixture to add (served as byte[])
   */
  public void addFixture(final String fixture) {

    Preconditions.checkState(!executorService.isTerminated(), "Executor service must not be terminated");

    log.debug("Adding fixture: '{}'", fixture);

    ListenableFuture<Boolean> listenableFuture = executorService.submit(new FixtureCallable(serverSocket, "application/bitcoin-paymentrequest", fixture));
    Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
      @Override
      public void onSuccess(@Nullable Boolean result) {

        log.info("Fixture '{}' served successfully", fixture);
      }

      @Override
      public void onFailure(Throwable t) {
        fail("Unexpected failure for fixture: " + fixture, t);
      }
    });

  }

  /**
   * Remove all entries from the fixture queue and reset the executor service
   */
  @SuppressFBWarnings({"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD"})
  public void reset() {

    executorService.shutdownNow();
    try {
      executorService.awaitTermination(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.warn("Failed to terminate executor service cleanly");
    }

    executorService = SafeExecutors.newSingleThreadExecutor("bip70-server");

  }

  public void stop() {

    reset();
    try {
      serverSocket.close();
    } catch (IOException e) {
      log.warn("Failed to close server socket", e);
    }

  }
}
