package org.multibit.hd.core.services;

import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.TrustStoreLoader;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.multibit.hd.core.dto.PaymentSessionSummary;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.SSLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>Service to provide the following to GUI classes:</p>
 * <ul>
 * <li>Handle Payment Protocol requests and sessions</li>
 * </ul>
 * <p/>
 */
public class PaymentProtocolService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(PaymentProtocolService.class);

  /**
   * The timeout before a server is deemed to be unresponsive to a payment request
   */
  private static final int PAYMENT_REQUEST_TIMEOUT_SECONDS = 30;

  /**
   * The Bitcoin network parameters
   */
  private final NetworkParameters networkParameters;

  public PaymentProtocolService(NetworkParameters networkParameters) {

    super();

    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    this.networkParameters = networkParameters;

  }

  @Override
  protected boolean startInternal() {

    return true;
  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    // Always treat as a hard shutdown
    return true;

  }

  /**
   * <p>Probe a location for a new payment session based on the information contained in the URI (http, https or bitcoin).</p>
   * <p>Normally this will result in a PaymentSessionSummary containing the PaymentSession however consumers must check the
   * status message to determine any problems that were encountered and how to respond to them.</p>
   * <p>MultiBit HD policy is that anyone issuing payment requests with PKI should have them signed by some
   * kind of CA or omit them. If the CA is not available then the user must decide how to proceed. Consequently consuming code
   * will have checkPKI set to true in the first instance and possibly false in the second.</p>
   *
   * @param paymentRequestUri The URI referencing the PaymentRequest
   * @param checkPKI          True if the PKI details should be checked (recommended - see policy note)
   * @param trustStoreLoader  The trust store loader linked to the local CA certs file
   *
   * @return A new payment session covering this payment request, absent if a failure occurred (see History)
   */
  public PaymentSessionSummary probeForPaymentSession(URI paymentRequestUri, boolean checkPKI, TrustStoreLoader trustStoreLoader) {

    Preconditions.checkNotNull(paymentRequestUri, "'paymentRequestUri' must be present");

    log.info("Probing '{}'", paymentRequestUri);

    String scheme = paymentRequestUri.getScheme() == null ? "" : paymentRequestUri.getScheme();
    String hostName = paymentRequestUri.getHost() == null ? "" : paymentRequestUri.getHost();

    try {

      // Determine how to obtain the payment request based on the scheme

      if (scheme.startsWith("bitcoin")) {
        // Remote resource serving payment requests indirectly
        final BitcoinURI bitcoinUri = new BitcoinURI(networkParameters, paymentRequestUri.toString());
        // TODO Consider multiple fallback URLs
        URL r = new URL(bitcoinUri.getPaymentRequestUrl());
        log.debug("Probing '{}' for payment request...", r);
        if (r.getProtocol().startsWith("https")) {
          // Read the protobuf bytes since HTTPS is not supported in Bitcoinj
          byte[] paymentRequestBytes = SSLManager.getContentAsBytes(r);
          Protos.PaymentRequest paymentRequest = Protos.PaymentRequest.parseFrom(paymentRequestBytes);
          PaymentSession paymentSession = new PaymentSession(paymentRequest, checkPKI, trustStoreLoader);

          return PaymentSessionSummary.newPaymentSessionOK(paymentSession);
        } else {
          // Remote resource serving payment requests directly over HTTP is supported in Bitcoinj
          final PaymentSession paymentSession = PaymentSession
            .createFromUrl(paymentRequestUri.toString(), checkPKI, trustStoreLoader)
            .get(PAYMENT_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

          return PaymentSessionSummary.newPaymentSessionOK(paymentSession);
        }

      } else if (scheme.startsWith("https")) {
        // Read the protobuf bytes since HTTPS is not supported in Bitcoinj
        byte[] paymentRequestBytes = SSLManager.getContentAsBytes(paymentRequestUri.toURL());
        Protos.PaymentRequest paymentRequest = Protos.PaymentRequest.parseFrom(paymentRequestBytes);
        PaymentSession paymentSession = new PaymentSession(paymentRequest, checkPKI, trustStoreLoader);

        return PaymentSessionSummary.newPaymentSessionOK(paymentSession);

      } else if (scheme.startsWith("http")) {
        // Remote resource serving payment requests directly over HTTP is supported in Bitcoinj
        final PaymentSession paymentSession = PaymentSession
          .createFromUrl(paymentRequestUri.toString(), checkPKI, trustStoreLoader)
          .get(PAYMENT_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return PaymentSessionSummary.newPaymentSessionOK(paymentSession);

      } else if (scheme.startsWith("file")) {
        // File based resource
        byte[] paymentRequestBytes = Resources.toByteArray(paymentRequestUri.toURL());
        Protos.PaymentRequest paymentRequest = Protos.PaymentRequest.parseFrom(paymentRequestBytes);
        PaymentSession paymentSession = new PaymentSession(paymentRequest, checkPKI, trustStoreLoader);

        return PaymentSessionSummary.newPaymentSessionOK(paymentSession);

      } else {
        // Assume classpath resource
        InputStream inputStream = PaymentProtocolService.class.getResourceAsStream(paymentRequestUri.toString());
        Protos.PaymentRequest paymentRequest = Protos.PaymentRequest.parseFrom(inputStream);
        PaymentSession paymentSession = new PaymentSession(paymentRequest, checkPKI, trustStoreLoader);

        return PaymentSessionSummary.newPaymentSessionOK(paymentSession);

      }

    } catch (PaymentProtocolException e) {
      // We can be more specific about handling the error
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    } catch (BitcoinURIParseException e) {
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    } catch (ExecutionException e) {
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    } catch (InterruptedException e) {
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    } catch (InvalidProtocolBufferException e) {
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    } catch (MalformedURLException e) {
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    } catch (IOException e) {
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    } catch (TimeoutException e) {
      return PaymentSessionSummary.newPaymentSessionFromException(e, hostName);
    }

  }

}
