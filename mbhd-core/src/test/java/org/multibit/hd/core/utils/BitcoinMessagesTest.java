package org.multibit.hd.core.utils;

import com.google.common.base.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.testing.FixtureAsserts;

import static org.fest.assertions.Assertions.assertThat;

public class BitcoinMessagesTest {

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @After
  public void tearDown() throws Exception {

    InstallationManager.unrestricted = false;

  }

  @Test
  public void testFormatAsBitcoinSignedMessage() throws Exception {

    String actual = BitcoinMessages.formatAsBitcoinSignedMessage(
      "16R2kAxaUNM4xj6ykKbxEugpJdYyJzTP13",
      "Hello World",
      "H0b22gIQIfutUzm7Z9qchdfhUtaO52alhNPK3emrkGOfbOzGHVPuWD9rMIphxniwBNgF/YN4c5C/dMwXz3yJz5k="
    );

    FixtureAsserts.assertStringMatchesNormalisedStringFixture(
      "Unexpected formatting of Bitcoin signed message",
      actual,
      "/fixtures/messages/test-simple.txt");

  }

  @Test
  public void testParseSignedMessage_Not_Present() throws Exception {

    BitcoinMessages.SignedMessage actual = BitcoinMessages.parseSignedMessage(Optional.<String>absent());

    assertThat(actual.getMessage()).isEmpty();

  }

  @Test
  public void testParseSignedMessage_Not_Formatted() throws Exception {

    BitcoinMessages.SignedMessage actual = BitcoinMessages.parseSignedMessage(Optional.of("Random text"));

    assertThat(actual.getMessage()).isEqualTo("Random text");

  }

  @Test
  public void testParseSignedMessage_Formatted_Correctly() throws Exception {

    String signatureBlock = FixtureAsserts.fixture("/fixtures/messages/test-simple.txt");

    BitcoinMessages.SignedMessage actual = BitcoinMessages.parseSignedMessage(Optional.of(signatureBlock));

    assertThat(actual.getMessage()).isEqualTo("Hello World");
    assertThat(actual.getAddress()).isEqualTo("16R2kAxaUNM4xj6ykKbxEugpJdYyJzTP13");
    assertThat(actual.getSignature()).isEqualTo("H0b22gIQIfutUzm7Z9qchdfhUtaO52alhNPK3emrkGOfbOzGHVPuWD9rMIphxniwBNgF/YN4c5C/dMwXz3yJz5k=");
    assertThat(actual.getVersion()).startsWith("MultiBit HD");
    assertThat(actual.getComment()).isEqualTo("https://multibit.org");

  }

  @Test
  public void testParseSignedMessage_Malformed_Missing_Blank_Line() throws Exception {

    String signatureBlock = FixtureAsserts.fixture("/fixtures/messages/test-malformed-1.txt");

    BitcoinMessages.SignedMessage actual = BitcoinMessages.parseSignedMessage(Optional.of(signatureBlock));

    assertThat(actual.getMessage()).isEqualTo("Hello World");
    assertThat(actual.getAddress()).isEqualTo("16R2kAxaUNM4xj6ykKbxEugpJdYyJzTP13");
    assertThat(actual.getSignature()).isEqualTo("H0b22gIQIfutUzm7Z9qchdfhUtaO52alhNPK3emrkGOfbOzGHVPuWD9rMIphxniwBNgF/YN4c5C/dMwXz3yJz5k=");
    assertThat(actual.getVersion()).startsWith("MultiBit HD");
    assertThat(actual.getComment()).isEqualTo("https://multibit.org");

  }

  @Test
  public void testParseSignedMessage_Unknown_Field() throws Exception {

    String signatureBlock = FixtureAsserts.fixture("/fixtures/messages/test-malformed-2.txt");

    BitcoinMessages.SignedMessage actual = BitcoinMessages.parseSignedMessage(Optional.of(signatureBlock));

    assertThat(actual.getMessage()).isEqualTo("Hello World");
    assertThat(actual.getAddress()).isEqualTo("");
    assertThat(actual.getSignature()).isEqualTo("H0b22gIQIfutUzm7Z9qchdfhUtaO52alhNPK3emrkGOfbOzGHVPuWD9rMIphxniwBNgF/YN4c5C/dMwXz3yJz5k=");
    assertThat(actual.getVersion()).isEqualTo("");
    assertThat(actual.getComment()).isEqualTo("");

  }

  @Test
  public void testParseSignedMessage_Malformed_Begin_Signed_Message() throws Exception {

    String signatureBlock = FixtureAsserts.fixture("/fixtures/messages/test-malformed-3.txt");

    BitcoinMessages.SignedMessage actual = BitcoinMessages.parseSignedMessage(Optional.of(signatureBlock));

    assertThat(actual.getMessage()).isEqualTo("");
    assertThat(actual.getAddress()).isEqualTo("16R2kAxaUNM4xj6ykKbxEugpJdYyJzTP13");
    assertThat(actual.getSignature()).isEqualTo("H0b22gIQIfutUzm7Z9qchdfhUtaO52alhNPK3emrkGOfbOzGHVPuWD9rMIphxniwBNgF/YN4c5C/dMwXz3yJz5k=");
    assertThat(actual.getVersion()).startsWith("MultiBit HD");
    assertThat(actual.getComment()).isEqualTo("https://multibit.org");

  }

  @Test
  public void testParseSignedMessage_Malformed_Hyphens() throws Exception {

    String signatureBlock = FixtureAsserts.fixture("/fixtures/messages/test-malformed-4.txt");

    BitcoinMessages.SignedMessage actual = BitcoinMessages.parseSignedMessage(Optional.of(signatureBlock));

    String ls = String.format("%n").intern();
    assertThat(actual.getMessage().contains("Mary"+ls)).isTrue();
    assertThat(actual.getAddress()).isEqualTo("16R2kAxaUNM4xj6ykKbxEugpJdYyJzTP13");
    assertThat(actual.getSignature()).isEqualTo("ICt2ZS5hSnKuJtLTx3GImH611PgMK/cWKKuBktsMwxNmboa9ph3f1ypZ+Ti5GggEyhm1v7mc6B2H2ZSByhOWIr0=");
    assertThat(actual.getVersion()).startsWith("MultiBit HD");
    assertThat(actual.getComment()).isEqualTo("https://multibit.org");

  }

}