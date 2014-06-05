package org.multibit.hd.core.managers;

import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.files.SecureFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * <p>Manager to provide the following to other core classes:</p>
 * <ul>
 * <li>Installation of X509 certificates to application trust store</li>
 * <li>Utility methods for making SSL socket connections with the trust store</li>
 * </ul>
 * <p>Tried to keep this manager self-contained to allow portability to other applications</p>
 *
 * @since 0.0.1
 */
public enum SSLManager {

  INSTANCE

  // End of enum
  ;

  private static final Logger log = LoggerFactory.getLogger(SSLManager.class);

  /**
   * The certificates are only used to verify hard-coded hosts which are protected
   * by a digital signature on the JAR therefore this password is meaningless
   *
   * For convenience we use the same as the default JVM
   */
  private final char[] PASSPHRASE = "changeit".toCharArray();

  /**
   * <p>Handles the process of installing all CA certificates for MultiBit and supporting services (e.g. exchanges)</p>
   *
   * @param applicationDirectory The application directory that must be writable
   * @param localTrustStoreName  The name of the local trust store (e.g. "appname-cacerts")
   * @param force                True if the SSL certificate should be refreshed from the main server
   */
  public void installCACertificates(File applicationDirectory, String localTrustStoreName, boolean force) {

    try {

      // Create an empty blank file if required
      final File appCacertsFile = SecureFiles.verifyOrCreateFile(applicationDirectory, localTrustStoreName);

      // Load the key store (could be empty)
      final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      try (InputStream in = new FileInputStream(appCacertsFile)) {
        ks.load(in, PASSPHRASE);
      } catch (EOFException e) {
        // Key store is empty so load from null
        ks.load(null, PASSPHRASE);
      }

      // Provide a quick startup option if the aliases are in place and we're not forcing a refresh
      if (ks.containsAlias("multibit.org-1") && ks.containsAlias("multibit.org-2") && !force) {

        // Must have finished to be here so define the cacerts file to be the one used for all SSL
        System.setProperty("javax.net.ssl.trustStore", appCacertsFile.getAbsolutePath());

        return;
      }

      // Build the trust manager factory
      final TrustManagerFactory tmf = TrustManagerFactory
        .getInstance(TrustManagerFactory
          .getDefaultAlgorithm());
      tmf.init(ks);

      // Use X509
      final X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
      final SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);

      // Create an SSL context based on TLS
      final SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, new TrustManager[]{tm}, null);

      // Create the SSL factory and expose it for general use
      SSLSocketFactory factory = context.getSocketFactory();

      boolean isTrusted = false;

      // Allocate space based on full population
      String[] hosts = new String[ExchangeKey.values().length + 1];
      int i = 0;
      hosts[i] = "multibit.org";
      i++;
      for (ExchangeKey exchangeKey : ExchangeKey.values()) {
        if (ExchangeKey.NONE.equals(exchangeKey)) {
          continue;
        }
        String sslUri = exchangeKey.getExchange().get().getExchangeSpecification().getSslUri();
        if (sslUri != null && sslUri.startsWith("https://")) {
          hosts[i] = URI.create(sslUri).getHost();
          log.debug("Added {}' to SSL hosts", hosts[i]);
          i++;
        }
      }

      for (String host : hosts) {

        // There may be gaps in the hosts
        if (host == null) {
          continue;
        }

        log.info("Opening connection to '{}:443'...", host);
        try {
          final SSLSocket socket = (SSLSocket) factory.createSocket(host, 443);
          socket.setSoTimeout(5000);

          log.info("Starting SSL handshake...");
          socket.startHandshake();
          socket.close();
          log.info("No errors. The certificate is already trusted");

          isTrusted = true;
        } catch (UnknownHostException | SocketTimeoutException | SocketException e) {
          // The host is unavailable or the network is down - quit now and use JVM defaults
          return;
        } catch (SSLException e) {
          // Need to import the certificate
        }

        if (!isTrusted) {

          final X509Certificate[] chain = tm.chain;
          if (chain == null) {
            log.info("Could not obtain server certificate chain");
            return;
          }

          log.info("Server sent " + chain.length + " certificate(s) which you can now add to the trust store:");
          final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
          final MessageDigest md5 = MessageDigest.getInstance("MD5");
          for (int index = 0; index < chain.length; index++) {

            X509Certificate cert = chain[index];
            sha1.update(cert.getEncoded());
            md5.update(cert.getEncoded());
            String alias = host + "-" + (index + 1);
            ks.setCertificateEntry(alias, cert);

            log.info("-> {}: Subject '{}'", (index + 1), cert.getSubjectDN());
            log.info("->   : Issuer '{}'", cert.getIssuerDN());
            log.info("->   : SHA1 '{}'", toHexString(sha1.digest()));
            log.info("->   : MD5 '{}'", toHexString(md5.digest()));
            log.info("->   : Alias '{}'", alias);
            try (OutputStream out = new FileOutputStream(appCacertsFile)) {
              ks.store(out, PASSPHRASE);
            }

          }
        }
      }

      // Must have finished to be here so define the cacerts file to be the one used for all SSL
      System.setProperty("javax.net.ssl.trustStore", appCacertsFile.getAbsolutePath());
    } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException e) {

      throw new IllegalStateException("CA Certificate update has failed.", e);

    }

  }

  /**
   * Simple Hex display utility
   *
   * @param bytes The bytes
   *
   * @return The hex representation
   */

  private String toHexString(byte[] bytes) {

    final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    StringBuilder sb = new StringBuilder(bytes.length * 3);
    for (int b : bytes) {
      b &= 0xff;
      sb.append(HEXDIGITS[b >> 4]);
      sb.append(HEXDIGITS[b & 15]);
      sb.append(' ');
    }
    return sb.toString();
  }

  /**
   * A persistent X509 trust manager
   */
  private class SavingTrustManager implements X509TrustManager {

    private final X509TrustManager tm;
    private X509Certificate[] chain;

    SavingTrustManager(X509TrustManager tm) {
      this.tm = tm;
    }

    public X509Certificate[] getAcceptedIssuers() {
      throw new UnsupportedOperationException();
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      throw new UnsupportedOperationException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      this.chain = chain;
      tm.checkServerTrusted(chain, authType);
    }

  }

}
