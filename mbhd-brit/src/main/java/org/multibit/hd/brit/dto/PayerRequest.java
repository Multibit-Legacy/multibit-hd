package org.multibit.hd.brit.dto;

import com.google.bitcoin.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Strings;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

/**
 *  <p>DTO to provide the following to BRIT:<br>
 *  <ul>
 *  <li>This is the unencrypted version of the message sent by the Payer to the Matcher</li>
 *  <li>Typically 'encrypt' is called and the EncryptedPayerRequest is actually sent on the wire</li>
 *  </ul>
 *  </p>
 *  
 */
public class PayerRequest {

  private static final Logger log = LoggerFactory.getLogger(PayerRequest.class);

  private final BRITWalletId britWalletId;

  private final byte[] sessionKey;

  private final Date firstTransactionDate;

  public static final String SERIALISER_SEPARATOR = "\n";

  public PayerRequest(BRITWalletId britWalletId, byte[] sessionKey, Date firstTransactionDate) {
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

  public Date getFirstTransactionDate() {
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
   * @return byte stream representing the PayerRequest
   */
  public byte[] serialise() {
    StringBuilder builder = new StringBuilder();
    builder.append(Utils.bytesToHexString(britWalletId.getBytes())).append(SERIALISER_SEPARATOR)
            .append(Utils.bytesToHexString(sessionKey)).append(SERIALISER_SEPARATOR)
            .append(firstTransactionDate.getTime());
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
    String[] rows = Strings.split(serialisedPaymentRequestAsString, '\n');
    if (rows.length == 3) {
      BRITWalletId britWalletId = new BRITWalletId(rows[0]);
      byte[] sesssionKey = Utils.parseAsHexOrBase58(rows[1]);
      long firstTransactionDateAsLong = Long.parseLong(rows[2]);
      Date firstTransactionDate = new Date(firstTransactionDateAsLong);
      return new PayerRequest(britWalletId, sesssionKey, firstTransactionDate);

    } else {
      // Cannot parse
      log.error("Cannot parse"); // TODO throw exception
      return null;
    }
  }
}
