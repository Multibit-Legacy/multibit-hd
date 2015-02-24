package org.multibit.hd.core.managers;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.files.SecureFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
   * by a digital signature on the JAR therefore this credentials is meaningless
   *
   * For convenience we use the same as the default JVM
   */
  public static final String PASSPHRASE = "changeit";

  /**
   * @param httpsUrl The HTTPS URL from which to get the data
   *
   * @return The contents of the URL as a String (UTF-8 encoding expected)
   *
   * @throws IOException If something goes wrong
   */
  public static String getContentAsString(URL httpsUrl) throws IOException {

    HttpsURLConnection con = (HttpsURLConnection) httpsUrl.openConnection();

    try (final InputStream in = con.getInputStream();
         final InputStreamReader inr = new InputStreamReader(in, Charsets.UTF_8)) {
      return CharStreams.toString(inr);
    }

  }

  /**
   * @param httpsUrl The HTTPS URL from which to get the data
   *
   * @return The contents of the URL as bytes
   *
   * @throws IOException If something goes wrong
   */
  public static byte[] getContentAsBytes(URL httpsUrl) throws IOException {

    HttpsURLConnection con = (HttpsURLConnection) httpsUrl.openConnection();

    try (final InputStream in = con.getInputStream()) {
      return ByteStreams.toByteArray(in);
    }

  }

  /**
   * <p>Handles the process of installing all CA certificates for MultiBit and supporting services (e.g. exchanges)</p>
   *
   * <p>Due to the risk of upstream responses being slow this should be run its own executor</p>
   *
   * @param applicationDirectory The application directory that must be writable
   * @param localTrustStoreName  The name of the local trust store (e.g. "appname-cacerts")
   * @param hosts                A list of host names to provide the initial trusted certificates (if null or empty the default list is used)
   * @param force                True if the SSL certificate should be refreshed from the main server
   */
  public void installCACertificates(File applicationDirectory, String localTrustStoreName, String[] hosts, boolean force) {

    try {

      // Create an empty blank file if required
      final File appCacertsFile = SecureFiles.verifyOrCreateFile(applicationDirectory, localTrustStoreName);
      final KeyStore ks = getKeyStore(appCacertsFile);

      // Provide a quick startup option if the aliases are in place and we're not forcing a refresh
      if (!force && ks.containsAlias("multibit.org-1") && ks.containsAlias("multibit.org-2")) {

        // Must have finished to be here so define the cacerts file to be the one used for all SSL
        System.setProperty("javax.net.ssl.trustStore", appCacertsFile.getAbsolutePath());

        return;
      }

      // Build the saving trust manager so we have a place to put our trusted certificates
      final SavingTrustManager tm = getSavingTrustManager(ks);

      // Create an SSL context based on TLS
      final SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, new TrustManager[]{tm}, null);

      // Create the SSL factory and expose it for general use
      SSLSocketFactory factory = context.getSocketFactory();

      // Determine which hosts will be loaded
      if (hosts == null || hosts.length == 0) {
        hosts = populateHosts();
      }

      for (String host : hosts) {

        boolean isTrusted = false;

        // There may be gaps in the hosts
        if (host == null) {
          continue;
        }

        try {
          final SSLSocket socket;
          if (host.contains(":")) {
            String[] endpoint = host.split(":");
            log.info("Opening connection to '{}'...", host);
            socket = (SSLSocket) factory.createSocket(endpoint[0], Integer.parseInt(endpoint[1]));
          } else {
            log.info("Opening default connection to '{}:443'...", host);
            socket = (SSLSocket) factory.createSocket(host, 443);
          }
          socket.setSoTimeout(5000);

          log.info("Starting SSL handshake...");
          socket.startHandshake();
          socket.close();
          log.info("No errors. The certificate is already trusted");

          isTrusted = true;
        } catch (UnknownHostException | SocketTimeoutException | SocketException e) {
          // The host is unavailable or the network is down - continue to the next one
          continue;
        } catch (SSLException e) {
          // Need to import the certificate
        }

        if (!isTrusted) {
          // Attempt to store the X509 certificate
          storeX509Certificate(appCacertsFile, ks, tm, host);
        }
      }

      // Must have finished to be here so define the cacerts file to be the one used for all SSL
      System.setProperty("javax.net.ssl.trustStore", appCacertsFile.getAbsolutePath());

    } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException e) {

      throw new IllegalStateException("CA Certificate update has failed.", e);

    }

  }

  /**
   * @param appCacertsFile The app CA certs file for the trust store
   *
   * @return A key store loaded from the CA certs file
   *
   * @throws KeyStoreException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws CertificateException
   */
  public KeyStore getKeyStore(File appCacertsFile) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {

    // Load the key store (could be empty)
    final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    try (InputStream in = new FileInputStream(appCacertsFile)) {
      ks.load(in, PASSPHRASE.toCharArray());
    } catch (EOFException e) {
      // Key store is empty so load from null
      ks.load(null, PASSPHRASE.toCharArray());
    }

    return ks;

  }

  /**
   * @param ks The keystore
   *
   * @return A saving trust manager so that certificates can be stored
   *
   * @throws NoSuchAlgorithmException
   * @throws KeyStoreException
   */
  private SavingTrustManager getSavingTrustManager(KeyStore ks) throws NoSuchAlgorithmException, KeyStoreException {

    final TrustManagerFactory tmf = TrustManagerFactory
      .getInstance(
        TrustManagerFactory
          .getDefaultAlgorithm());
    tmf.init(ks);

    // Use X509
    final X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];

    return new SavingTrustManager(defaultTrustManager);
  }

  /**
   * @return The default list of hosts requiring entry to the trust store
   */
  private String[] populateHosts() {

    String[] hosts = new String[ExchangeKey.values().length + 1];
    int i = 0;
    hosts[i] = "multibit.org";
    i++;
    hosts[i] = "beta.multibit.org";
    i++;
    hosts[i] = "www.multibit.org";
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
    return hosts;
  }

  /**
   * @param appCacertsFile The app CA certs file for the trust store
   * @param ks             The keystore
   * @param tm             The trust manager
   * @param host           The host
   *
   * @return True if the certificate was stored
   *
   * @throws NoSuchAlgorithmException
   * @throws KeyStoreException
   * @throws IOException
   * @throws CertificateException
   */
  public boolean storeX509Certificate(File appCacertsFile, KeyStore ks, SavingTrustManager tm, String host)
    throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {

    final X509Certificate[] chain = tm.chain;
    if (chain == null) {
      log.warn("Could not obtain server certificate chain");
      return false;
    }

    log.debug("Server sent " + chain.length + " certificate(s) which you can now add to the trust store:\n'{}'", appCacertsFile.getAbsolutePath());
    final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
    final MessageDigest md5 = MessageDigest.getInstance("MD5");
    for (int index = 0; index < chain.length; index++) {

      X509Certificate cert = chain[index];
      sha1.update(cert.getEncoded());
      md5.update(cert.getEncoded());
      String alias = host + "-" + (index + 1);
      ks.setCertificateEntry(alias, cert);

      log.debug("-> {}: Subject '{}'", (index + 1), cert.getSubjectDN());
      log.debug("->   : Issuer '{}'", cert.getIssuerDN());
      log.debug("->   : SHA1 '{}'", toHexString(sha1.digest()));
      log.debug("->   : MD5 '{}'", toHexString(md5.digest()));
      log.debug("->   : Alias '{}'", alias);
      try (OutputStream out = new FileOutputStream(appCacertsFile)) {
        ks.store(out, PASSPHRASE.toCharArray());
      }

    }

    // Must be OK to be here
    return true;
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
  static class SavingTrustManager implements X509TrustManager {

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
