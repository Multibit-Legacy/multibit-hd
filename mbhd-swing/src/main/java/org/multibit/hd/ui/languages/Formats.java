package org.multibit.hd.ui.languages;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.Satoshis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * <p>Utility to provide the following to controllers:</p>
 * <ul>
 * <li>Access to international formats for date/time and decimal data</li>
 * <li>Access to alert layouts in different languages</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Formats {

  /**
   * <p>Provide a split representation for the Bitcoin balance display.</p>
   * <p>For example, 12345.6789 becomes "12,345.67", "89" </p>
   * <p>The amount will be adjusted by the symbolic multiplier from the current confiuration</p>
   *
   * @param satoshis              The amount in satoshis
   * @param languageConfiguration The  language configuration to use as the basis for presentation
   * @param bitcoinConfiguration  The Bitcoin configuration to use as the basis for the symbol
   *
   * @return The left [0] and right [1] components suitable for presentation as a balance with no symbolic decoration
   */
  public static String[] formatSatoshisAsSymbolic(
    BigInteger satoshis,
    LanguageConfiguration languageConfiguration,
    BitcoinConfiguration bitcoinConfiguration
  ) {
    return formatSatoshisAsSymbolic(satoshis, languageConfiguration, bitcoinConfiguration, true);
  }

  /**
   * <p>Provide a split representation for the Bitcoin balance display.</p>
   * <p>For example, 123456789 in uBTC becomes "1,234,567.", "89" </p>
   * <p>The amount will be adjusted by the symbolic multiplier from the current configuration</p>
   *
   * @param satoshis              The amount in satoshis
   * @param languageConfiguration The  language configuration to use as the basis for presentation
   * @param bitcoinConfiguration  The Bitcoin configuration to use as the basis for the symbol
   * @param showNegative          If true, show '-' for negative numbers
   *
   * @return The left [0] and right [1] components suitable for presentation as a balance with no symbolic decoration
   */
  public static String[] formatSatoshisAsSymbolic(
    BigInteger satoshis,
    LanguageConfiguration languageConfiguration,
    BitcoinConfiguration bitcoinConfiguration,
    boolean showNegative
  ) {

    Preconditions.checkNotNull(satoshis, "'satoshis' must be present");
    Preconditions.checkNotNull(languageConfiguration, "'languageConfiguration' must be present");
    Preconditions.checkNotNull(bitcoinConfiguration, "'bitcoinConfiguration' must be present");

    Locale currentLocale = languageConfiguration.getLocale();
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.of(bitcoinConfiguration.getBitcoinSymbol());

    DecimalFormatSymbols dfs = configureDecimalFormatSymbols(bitcoinConfiguration, currentLocale);
    DecimalFormat localFormat = configureBitcoinDecimalFormat(dfs, bitcoinSymbol, showNegative);

    // Apply formatting to the symbolic amount
    String formattedAmount = localFormat.format(Satoshis.toSymbolicAmount(satoshis, bitcoinSymbol));

    // The Satoshi symbol does not have decimals
    if (BitcoinSymbol.SATOSHI.equals(bitcoinSymbol)) {

      return new String[]{
        formattedAmount,
        ""
      };

    }

    // All other representations require a decimal

    int decimalIndex = formattedAmount.lastIndexOf(dfs.getDecimalSeparator());

    if (decimalIndex == -1) {
      formattedAmount += dfs.getDecimalSeparator() + "00";
      decimalIndex = formattedAmount.lastIndexOf(dfs.getDecimalSeparator());
    }

    return new String[]{
      formattedAmount.substring(0, decimalIndex + 3), // 12,345.67 (significant figures)
      formattedAmount.substring(decimalIndex + 3) // 89 (lesser figures truncated )
    };

  }

  /**
   * <p>Provide a single text representation for the Bitcoin balance display.</p>
   * <p>For example, 123456789 becomes "1,234,567.89 uBTC" or "uXBT 12,345.6789" </p>
   * <p>The amount will be adjusted by the symbolic multiplier from the current configuration</p>
   *
   * @param satoshis              The amount in satoshis
   * @param languageConfiguration The  language configuration to use as the basis for presentation
   * @param bitcoinConfiguration  The Bitcoin configuration to use as the basis for the symbol
   *
   * @return The string suitable for presentation as a balance with symbol in a UTF-8 string
   */
  public static String formatSatoshisAsSymbolicText(
    BigInteger satoshis,
    LanguageConfiguration languageConfiguration,
    BitcoinConfiguration bitcoinConfiguration
  ) {

    String[] formattedAmount = formatSatoshisAsSymbolic(satoshis, languageConfiguration, bitcoinConfiguration);

    String lineSymbol = BitcoinSymbol.of(bitcoinConfiguration.getBitcoinSymbol()).getTextSymbol();

    // Convert to single text line with leading or trailing symbol
    if (bitcoinConfiguration.isCurrencySymbolLeading()) {
      return lineSymbol + "\u00a0" + formattedAmount[0] + formattedAmount[1];
    } else {
      return formattedAmount[0] + formattedAmount[1] + "\u00a0" + lineSymbol;
    }

  }


  /**
   * <p>Provide a simple representation for a local currency amount.</p>
   *
   * @param amount               The amount as a plain number (no multipliers)
   * @param locale               The locale to use
   * @param bitcoinConfiguration The Bitcoin configuration to use as the basis for the symbol
   * @param showNegative         True if the negative prefix is allowed
   *
   * @return The local currency representation with no symbolic decoration
   */
  public static String formatLocalAmount(BigDecimal amount, Locale locale, BitcoinConfiguration bitcoinConfiguration, boolean showNegative) {

    if (amount == null) {
      return "";
    }

    DecimalFormatSymbols dfs = configureDecimalFormatSymbols(bitcoinConfiguration, locale);
    DecimalFormat localFormat = configureLocalDecimalFormat(dfs, bitcoinConfiguration, showNegative);

    return localFormat.format(amount);

  }

  /**
   * @param dfs The decimal format symbols
   *
   * @return A decimal format suitable for Bitcoin balance representation
   */
  private static DecimalFormat configureBitcoinDecimalFormat(DecimalFormatSymbols dfs, BitcoinSymbol bitcoinSymbol, boolean showNegative) {

    DecimalFormat format = new DecimalFormat();

    format.setDecimalFormatSymbols(dfs);

    format.setMaximumIntegerDigits(16);
    format.setMinimumIntegerDigits(1);

    format.setMaximumFractionDigits(bitcoinSymbol.decimalPlaces());
    format.setMinimumFractionDigits(bitcoinSymbol.decimalPlaces());

    format.setDecimalSeparatorAlwaysShown(false);

    if (showNegative) {
      format.setNegativePrefix("-");
    } else {
      format.setNegativePrefix("");
    }

    return format;
  }

  /**
   * @param dfs                  The decimal format symbols
   * @param bitcoinConfiguration The Bitcoin configuration to use
   * @param showNegative         True if the negative prefix is allowed
   *
   * @return A decimal format suitable for local currency balance representation
   */
  private static DecimalFormat configureLocalDecimalFormat(DecimalFormatSymbols dfs, BitcoinConfiguration bitcoinConfiguration, boolean showNegative) {

    DecimalFormat format = new DecimalFormat();

    format.setDecimalFormatSymbols(dfs);

    format.setMinimumIntegerDigits(1);
    format.setMaximumFractionDigits(bitcoinConfiguration.getLocalDecimalPlaces());
    format.setMinimumFractionDigits(bitcoinConfiguration.getLocalDecimalPlaces());

    format.setDecimalSeparatorAlwaysShown(true);

    if (showNegative) {
      format.setNegativePrefix("-");
    } else {
      format.setNegativePrefix("");
    }

    return format;
  }

  /**
   * @param bitcoinConfiguration The Bitcoin configuration
   * @param currentLocale        The current locale
   *
   * @return The decimal format symbols to use based on the configuration and locale
   */
  private static DecimalFormatSymbols configureDecimalFormatSymbols(BitcoinConfiguration bitcoinConfiguration, Locale currentLocale) {

    DecimalFormatSymbols dfs = new DecimalFormatSymbols(currentLocale);

    dfs.setDecimalSeparator(bitcoinConfiguration.getDecimalSeparator().charAt(0));
    dfs.setGroupingSeparator(bitcoinConfiguration.getGroupingSeparator().charAt(0));

    return dfs;

  }

  /**
   * @param event The "transaction seen" event
   *
   * @return A String suitably formatted for presentation as an alert message
   */
  public static String formatAlertMessage(TransactionSeenEvent event) {

    // Decode the "transaction seen" event
    final BigInteger amount = event.getAmount();

    // Create a suitable representation for inline text (no icon)
    final String messageAmount = Formats.formatSatoshisAsSymbolicText(
      amount,
      Configurations.currentConfiguration.getLanguage(),
      Configurations.currentConfiguration.getBitcoin()
    );

    // Construct a suitable alert message
    return Languages.safeText(MessageKey.PAYMENT_RECEIVED_ALERT, messageAmount);

  }

  /**
   * @param bitcoinURI The Bitcoin URI
   *
   * @return A String suitably formatted for presentation as an alert message
   */
  public static Optional<String> formatAlertMessage(BitcoinURI bitcoinURI) {

    Optional<String> alertMessage = Optional.absent();

    // Decode the Bitcoin URI
    Optional<Address> address = Optional.fromNullable(bitcoinURI.getAddress());
    Optional<BigInteger> amount = Optional.fromNullable(bitcoinURI.getAmount());
    Optional<String> label = Optional.fromNullable(bitcoinURI.getLabel());

    // Only proceed if we have an address
    if (address.isPresent()) {

      final String messageAmount;
      if (amount.isPresent()) {
        // Create a suitable representation for inline text (no icon)
        messageAmount = Formats.formatSatoshisAsSymbolicText(
          amount.get(),
          Configurations.currentConfiguration.getLanguage(),
          Configurations.currentConfiguration.getBitcoin()
        );
      } else {
        messageAmount = Languages.safeText(MessageKey.NOT_AVAILABLE);
      }

      // Ensure we truncate the label if present
      String truncatedLabel = Languages.truncatedList(Lists.newArrayList(label.or(Languages.safeText(MessageKey.NOT_AVAILABLE))), 35);

      // Construct a suitable alert message
      alertMessage = Optional.of(Languages.safeText(MessageKey.BITCOIN_URI_ALERT, truncatedLabel, address.get().toString(), messageAmount));
    }

    return alertMessage;

  }


}
