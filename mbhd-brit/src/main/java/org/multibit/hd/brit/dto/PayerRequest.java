package org.multibit.hd.brit.dto;

import com.google.bitcoin.core.Utils;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.multibit.hd.brit.exceptions.PayerRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Strings;

import java.util.Arrays;
import java.util.Date;

/**
 * <p>DTO to provide the following to BRIT API:</p>
 * <ul>
 * <li>The unencrypted version of the message sent by the Payer to the Matcher</li>
 * <li>Typically 'encrypt' is called and the EncryptedPayerRequest is actually sent on the wire</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class PayerRequest {

  private static final Logger log = LoggerFactory.getLogger(PayerRequest.class);

  public static final String OPTIONAL_NOT_PRESENT_TEXT = "not-present";

  private final BRITWalletId britWalletId;

  private final byte[] sessionKey;

  private final Optional<Date> firstTransactionDate;

  public static final char SEPARATOR = '\n';

  public PayerRequest(BRITWalletId britWalletId, byte[] sessionKey, Optional<Date> firstTransactionDate) {

    this.britWalletId = britWalletId;
    this.sessionKey = sessionKey;
    this.firstTransactionDate = firstTransactionDate;

  }

  /**
   * @return The BRIT protocol version
   */
  public int getVersion() {
    return 1;
  }

  /**
   * @return The session key
   */
  public byte[] getSessionKey() {
    return sessionKey;
  }

  /**
   * @return The BRIT wallet ID
   */
  public BRITWalletId getBRITWalletId() {
    return britWalletId;
  }

  /**
   * @return The first transaction date (if present)
   */
  public Optional<Date> getFirstTransactionDate() {
    return firstTransactionDate;
  }

  /**
   * <p>Serialise the contents of the PayerRequest to a byte stream</p>
   * <p>(This is not very efficient but the intermediate string is human readable)</p>
   *
   * @return Bytes representing the PayerRequest
   */
  public byte[] serialise() {

    StringBuilder builder = new StringBuilder()
            .append(getVersion())
            .append(SEPARATOR)
            .append(Utils.bytesToHexString(britWalletId.getBytes()))
            .append(SEPARATOR)
            .append(Utils.bytesToHexString(sessionKey))
            .append(SEPARATOR);

    if (firstTransactionDate.isPresent()) {
      builder.append(firstTransactionDate.get().getTime());
    } else {
      builder.append(OPTIONAL_NOT_PRESENT_TEXT);
    }

    log.debug("Serialised payerRequest = \n{}\n", builder.toString());
    return builder.toString().getBytes(Charsets.UTF_8);
  }

  /**
   * <p>Parse the serialised Payer request</p>
   *
   * @param serialisedPayerRequest The serialised payer request
   * @return The Payer request
   */
  public static PayerRequest parse(byte[] serialisedPayerRequest) {

    String serialisedPaymentRequestAsString = new String(serialisedPayerRequest, Charsets.UTF_8);

    log.debug("Attempting to parse PayerRequest:\n{}\n", serialisedPaymentRequestAsString);
    String[] rows = Strings.split(serialisedPaymentRequestAsString, SEPARATOR);
    if (rows.length == 4) {
      if (Long.parseLong(rows[0]) != 1) {
        throw new PayerRequestException("The serialisedPayerRequest had a version of '" + rows[0] + "'. This code only understands a version of '1'");
      }

      final BRITWalletId britWalletId = new BRITWalletId(rows[1]);
      final byte[] sessionKey = Utils.parseAsHexOrBase58(rows[2]);
      final Optional<Date> firstTransactionDateOptional;

      if (OPTIONAL_NOT_PRESENT_TEXT.equals(rows[3])) {
        firstTransactionDateOptional = Optional.absent();
      } else {
        firstTransactionDateOptional = Optional.of(new Date(Long.parseLong(rows[3])));
      }

      log.debug("Parsed OK");
      return new PayerRequest(britWalletId, sessionKey, firstTransactionDateOptional);

    } else {
      throw new PayerRequestException("Expected 4 rows of data. Found " + rows.length);
    }
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PayerRequest that = (PayerRequest) o;

    // While inefficient, leave this code as is for readability
    if (britWalletId != null ? !britWalletId.equals(that.britWalletId) : that.britWalletId != null) {
      return false;
    }
    if (firstTransactionDate != null ? !firstTransactionDate.equals(that.firstTransactionDate) : that.firstTransactionDate != null) {
      return false;
    }

    return Arrays.equals(sessionKey, that.sessionKey);

  }

  @Override
  public int hashCode() {

    int result = britWalletId != null ? britWalletId.hashCode() : 0;
    result = 31 * result + (sessionKey != null ? Arrays.hashCode(sessionKey) : 0);
    result = 31 * result + (firstTransactionDate != null ? firstTransactionDate.hashCode() : 0);
    return result;

  }

  @Override
  public String toString() {

    return "PayerRequest{" +
            "britWalletId=" + britWalletId +
            ", sessionKey=" + Arrays.toString(sessionKey) +
            ", firstTransactionDate=" + firstTransactionDate +
            '}';
  }
}
