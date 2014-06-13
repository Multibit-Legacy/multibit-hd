package org.multibit.hd.ui.views.components.text_fields;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.views.components.TextBoxes;

import static org.fest.assertions.Assertions.assertThat;

public class ThemeAwareDecimalInputVerifierTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @Test
  public void testVerifyBitcoinAmount_mBTC() throws Exception {

    // Arrange
    FormattedDecimalField textField = TextBoxes.newBitcoinAmount(21_000_000_000f);

    ThemeAwareDecimalInputVerifier testObject = (ThemeAwareDecimalInputVerifier) textField.getInputVerifier();

    // Act

    // Characters
    // We must permit empty to allow for focus transition
    textField.setText("");
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText(" ");
    assertThat(testObject.verify(textField)).isFalse();

    textField.setText("a");
    assertThat(testObject.verify(textField)).isFalse();

    textField.setText("0x");
    assertThat(testObject.verify(textField)).isFalse();

    textField.setText("$1");
    assertThat(testObject.verify(textField)).isFalse();

    // Numbers

    textField.setText("0");
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("-1");
    assertThat(testObject.verify(textField)).isFalse();

    textField.setText("0.1");
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("0.001"); // 1 uBTC
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("0.00001"); // 1 sat
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("1");
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("1000"); // 1 BTC
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("20000000000"); // 20 000 000 BTC
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("21000000001"); // 21 000 001 BTC
    assertThat(testObject.verify(textField)).isFalse();

    textField.setText("1,000"); // 1 BTC
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("20,000,000,000"); // 20 000 000 BTC
    assertThat(testObject.verify(textField)).isTrue();

    textField.setText("21,000,000,001"); // 21 000 001 BTC
    assertThat(testObject.verify(textField)).isFalse();

  }
}