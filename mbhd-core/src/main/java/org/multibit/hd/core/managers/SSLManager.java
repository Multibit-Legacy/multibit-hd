package org.multibit.hd.core.managers;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.MessageDigest;
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
   * The host providing information over SSL
   */
  public final String HOST = "multibit.org";

  /**
   * The certificates are only used to verify hard-coded hosts which are protected
   * by a digital signature on the JAR therefore this password is meaningless
   *
   * For convenience we use the same as the default JVM
   */
  private final char[] PASSPHRASE = "changeit".toCharArray();

  /**
   * @param applicationDirectory The application directory that must be writable
   * @param localTrustStoreName  The name of the local trust store (e.g. "appname-cacerts")
   *
   * @throws Exception
   */
  public void installMultiBitSSLCertificate(File applicationDirectory, String localTrustStoreName) throws Exception {

    final File appCacertsFile = new File(applicationDirectory, localTrustStoreName);

    // Attempt to load the key store
    if (!appCacertsFile.exists()) {

      // Copy the existing "cacerts" from the JDK into the application directory
      // This gives us all the standard certificates
      File jreCacertsDirectory = new File(System.getProperty("java.home") + "/lib/security");
      File jreCacertsFile = new File(jreCacertsDirectory, "cacerts");
      Files.copy(jreCacertsFile, appCacertsFile);

    }

    // Load the key store (could be empty)
    final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    try (InputStream in = new FileInputStream(appCacertsFile)) {
      ks.load(in, PASSPHRASE);
    } catch (EOFException e) {
      // Key store is empty so load from null
      ks.load(null, PASSPHRASE);
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

    log.info("Opening connection to '{}:443'...", HOST);
    try {
      final SSLSocket socket = (SSLSocket) factory.createSocket(HOST, 443);
      socket.setSoTimeout(5000);

      log.info("Starting SSL handshake...");
      socket.startHandshake();
      socket.close();
      log.info("No errors. The certificate is already trusted");

      isTrusted = true;
    } catch (UnknownHostException e) {
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
      for (int i = 0; i < chain.length; i++) {

        X509Certificate cert = chain[i];
        sha1.update(cert.getEncoded());
        md5.update(cert.getEncoded());
        String alias = HOST + "-" + (i + 1);
        ks.setCertificateEntry(alias, cert);

        log.info("-> {}: Subject '{}'", (i + 1), cert.getSubjectDN());
        log.info("->   : Issuer '{}'", cert.getIssuerDN());
        log.info("->   : SHA1 '{}'", toHexString(sha1.digest()));
        log.info("->   : MD5 '{}'", toHexString(md5.digest()));
        log.info("->   : Alias '{}'", alias);
        try (OutputStream out = new FileOutputStream(appCacertsFile)) {
          ks.store(out, PASSPHRASE);
        }

      }
    }

    // Must have finished to be here so define the cacerts file to be the one used for all SSL
    System.setProperty("javax.net.ssl.trustStore", appCacertsFile.getAbsolutePath());

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
