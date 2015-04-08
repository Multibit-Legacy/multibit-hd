package org.multibit.hd.core.testing.payments;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.crypto.TrustStoreLoader;
import org.bitcoinj.params.MainNetParams;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentSessionSummary;
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.PaymentProtocolService;
import org.multibit.hd.core.services.PaymentProtocolServiceTest;
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
import java.net.URI;
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
      System.setProperty("javax.net.ssl.trustStorePassword", HttpsManager.PASSPHRASE);

      SSLContext sslContext = SSLContext.getInstance("TLS");

      log.debug("Initialise the key store containing the private server keys (CN=localhost is required)");
      KeyStore ks = KeyStore.getInstance("JKS");
      is = PaymentProtocolHttpsServer.class.getResourceAsStream("/localhost.jks");
      ks.load(is, HttpsManager.PASSPHRASE.toCharArray());

      log.debug("Initialise the key manager factory");
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, HttpsManager.PASSPHRASE.toCharArray());

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
    */
   public void addPaymentACKCallable() {

     Preconditions.checkState(!executorService.isTerminated(), "Executor service must not be terminated");

     log.debug("Adding PaymentACKCallable");

     ListenableFuture<Boolean> listenableFuture = executorService.submit(new PaymentACKCallable(serverSocket, "application/bitcoin-paymentack"));
     Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
       @Override
       public void onSuccess(@Nullable Boolean result) {

         log.info("PaymentACKCallable served successfully");
       }

       @Override
       public void onFailure(Throwable t) {
         fail("Unexpected failure for PaymentACKCallable: ", t);
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

  /**
   * Start an https Payment Protocol server which can respond to a MultiBit HD instance
   * Start MBHD with the commandline parameter "project dir"/fixtures/payments/test-net-faucet.bitcoinpaymentrequest
   * @param args No args are needed
   */
  public static void main(String[] args) {
    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    PaymentProtocolHttpsServer server = new PaymentProtocolHttpsServer();

    log.debug("Result of server.start() was {}", server.start());

    // Add some responses - we consume one here (a PaymentRequest fixture) and then add three PaymentACKCallable responses
    server.addFixture("/fixtures/payments/localhost-signed.bitcoinpaymentrequest");
    server.addPaymentACKCallable();
    server.addPaymentACKCallable();
    server.addPaymentACKCallable();


    // Probe it once to see if it is up
    PaymentProtocolService paymentProtocolService = new PaymentProtocolService(MainNetParams.get());
    paymentProtocolService.start();

    final URI uri = URI.create(PaymentProtocolServiceTest.PAYMENT_REQUEST_BIP72_SINGLE);


    // Wait until the HTTPS server is up before setting the trust store loader
    TrustStoreLoader trustStoreLoader = new TrustStoreLoader.DefaultTrustStoreLoader();
    final PaymentSessionSummary paymentSessionSummary = paymentProtocolService.probeForPaymentSession(uri, false, trustStoreLoader);
    log.debug(paymentSessionSummary.toString());

    // Runs forever
    while (true) {
      Uninterruptibles.sleepUninterruptibly(20, TimeUnit.SECONDS);
      log.debug("Still running...");
    }
  }
}
