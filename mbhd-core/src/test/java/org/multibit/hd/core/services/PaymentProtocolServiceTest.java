package org.multibit.hd.core.services;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.uri.BitcoinURI;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.InstallationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class PaymentProtocolServiceTest {

  private static final Logger log = LoggerFactory.getLogger(PaymentProtocolServiceTest.class);

  private static NetworkParameters networkParameters = TestNet3Params.get();

  private PaymentProtocolService testObject;

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    testObject = new PaymentProtocolService(networkParameters);

  }

  @Test
  public void testPaymentProtocolURI_SinglePaymentRequestUrl() throws Exception {

    // Act
    BitcoinURI bitcoinURI = new BitcoinURI(networkParameters, "bitcoin:mrhz5ZgSF3C1BSdyCKt3gEdhKoRL5BNfJV?r=https://example.org/abc123&amount=1");

    // Assert
    final List<String> paymentRequestUrls = bitcoinURI.getPaymentRequestUrls();
    assertThat(paymentRequestUrls.size()).isEqualTo(1);

    // The primary payment request URL is in its own field
    assertThat(bitcoinURI.getPaymentRequestUrl()).isEqualTo("https://example.org/abc123");
    assertThat(paymentRequestUrls.get(0)).isEqualTo("https://example.org/abc123");

  }

  @Test
  public void testPaymentProtocolURI_MultiplePaymentRequestUrls() throws Exception {

    // Act
    BitcoinURI bitcoinURI = new BitcoinURI(networkParameters, "bitcoin:mrhz5ZgSF3C1BSdyCKt3gEdhKoRL5BNfJV?r=https://example.org/abc123&r1=https://example" +
      ".org/def456&r2=https://example.org/ghi789&amount=1");

    // Assert
    final List<String> paymentRequestUrls = bitcoinURI.getPaymentRequestUrls();
    assertThat(paymentRequestUrls.size()).isEqualTo(3);

    // The primary payment request URL is in its own field
    assertThat(bitcoinURI.getPaymentRequestUrl()).isEqualTo("https://example.org/abc123");

    // Backup payment request URLs are in reverse order
    assertThat(paymentRequestUrls.get(0)).isEqualTo("https://example.org/ghi789");
    assertThat(paymentRequestUrls.get(1)).isEqualTo("https://example.org/def456");
    assertThat(paymentRequestUrls.get(2)).isEqualTo("https://example.org/abc123");

  }

}
