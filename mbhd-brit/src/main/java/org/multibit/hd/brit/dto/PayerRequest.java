package org.multibit.hd.brit.dto;

import com.google.bitcoin.core.Utils;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Strings;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

/**
 *  <p>DTO to provide the following to BRIT:</p>
 *  <ul>
 *  <li>This is the unencrypted version of the message sent by the Payer to the Matcher</li>
 * <li>Typically 'encrypt' is called and the EncryptedPayerRequest is actually sent on the wire</li>
 *  </ul>
 *  </p>
 *  
 */
public class PayerRequest {

  private static final Logger log = LoggerFactory.getLogger(PayerRequest.class);

  public static final String OPTIONAL_NOT_PRESENT_TEXT = "not-present";

  private final BRITWalletId britWalletId;

  private final byte[] sessionKey;

  private final Optional<Date> firstTransactionDate;

  public static final char SERIALISER_SEPARATOR = '\n';

  public PayerRequest(BRITWalletId britWalletId, byte[] sessionKey, Optional<Date> firstTransactionDate) {
    this.britWalletId = britWalletId;
    this.sessionKey = sessionKey;
    this.firstTransactionDate = firstTransactionDate;
  }

  public int getVersion() {
    return 1;
  }

  public byte[] getSessionKey() {
    return sessionKey;
  }

  public BRITWalletId getBRITWalletId() {
    return britWalletId;
  }

  public Optional<Date> getFirstTransactionDate() {
    return firstTransactionDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PayerRequest that = (PayerRequest) o;

    if (britWalletId != null ? !britWalletId.equals(that.britWalletId) : that.britWalletId != null) return false;
    if (firstTransactionDate != null ? !firstTransactionDate.equals(that.firstTransactionDate) : that.firstTransactionDate != null)
      return false;
    if (!Arrays.equals(sessionKey, that.sessionKey)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = britWalletId != null ? britWalletId.hashCode() : 0;
    result = 31 * result + (sessionKey != null ? Arrays.hashCode(sessionKey) : 0);
    result = 31 * result + (firstTransactionDate != null ? firstTransactionDate.hashCode() : 0);
    return result;
  }

  /**
   * Serialise the contents of the PayerRequest to a byte stream
   * (This is not very efficient but the intermediate string is human readable)
   *
   * @return byte stream representing the PayerRequest
   */
  public byte[] serialise() {
    StringBuilder builder = new StringBuilder();
    builder.append(Utils.bytesToHexString(britWalletId.getBytes())).append(SERIALISER_SEPARATOR)
            .append(Utils.bytesToHexString(sessionKey)).append(SERIALISER_SEPARATOR);
    if (firstTransactionDate.isPresent()) {
      builder.append(firstTransactionDate.get().getTime());
    } else {
      builder.append(OPTIONAL_NOT_PRESENT_TEXT);
    }
    log.debug("Serialised payerRequest = \n" + builder.toString());
    try {
      return builder.toString().getBytes("UTF8");
    } catch (UnsupportedEncodingException uee) {
      // Will not happen
      uee.printStackTrace();
      return null;
    }
  }

  public static PayerRequest parse(byte[] serialisedPaymentRequest) throws UnsupportedEncodingException {
    String serialisedPaymentRequestAsString = new String(serialisedPaymentRequest, "UTF8");

    log.debug("Attempting to parse payment request:\n" + serialisedPaymentRequestAsString);
    String[] rows = Strings.split(serialisedPaymentRequestAsString, SERIALISER_SEPARATOR);
    if (rows.length == 3) {
      BRITWalletId britWalletId = new BRITWalletId(rows[0]);
      byte[] sesssionKey = Utils.parseAsHexOrBase58(rows[1]);
      Optional<Date> firstTransactionDateOptional;
      if (OPTIONAL_NOT_PRESENT_TEXT.equals(rows[2])) {
        firstTransactionDateOptional = Optional.absent();
      } else {
        firstTransactionDateOptional = Optional.of(new Date(Long.parseLong(rows[2])));
      }

      return new PayerRequest(britWalletId, sesssionKey, firstTransactionDateOptional);

    } else {
      // Cannot parse
      log.error("Cannot parse"); // TODO throw exception
      return null;
    }
  }
}
