package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.TransactionConfidence;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * <p>Data object to provide the following to Payments display:</p>
 * <ul>
 * <li>Contains transaction relevant data</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class TransactionData implements PaymentData {

  private final PaymentStatus statusWithOrdinal;

  private final String transactionId;

  private final Optional<Coin> amountBTC;

  private FiatPayment amountFiat;

  private final Optional<Coin> miningFee;

  private final Optional<Coin> clientFee;

  private final TransactionConfidence.ConfidenceType confidenceType;

  private final DateTime date;

  private final PaymentType type;

  private final boolean coinBase;

  private String description;

  private String note;

  /**
   * The transaction in its raw form (toStringed)
   */
  private final String rawTransaction;

  /**
   * The size (in bytes)of the transaction
   */
  private int size;

  /**
   * The bitcoin addresses in this wallet that the transaction sends bitcoin to
   */
  private final Collection<Address> outputAddresses;

  /**
   * @param transactionId     The transaction ID
   * @param date              The creation date
   * @param statusWithOrdinal The status with ordinal
   * @param amountBTC         The amount in coins
   * @param amountFiat        The amount in fiat
   * @param miningFee         The mining fee in coins
   * @param clientFee         The client fee in coins
   * @param confidenceType    The confidence type
   * @param paymentType       The payment type
   * @param description       The description
   * @param isCoinbase        True if coinbase
   * @param outputAddresses   The output addresses
   * @param rawTransaction    The raw transaction
   * @param size              The size in bytes
   * @param isMock            True if this is a mock (CSV export header)
   */
  public TransactionData(
    String transactionId,
    DateTime date,
    PaymentStatus statusWithOrdinal,
    Optional<Coin> amountBTC,
    @Nullable FiatPayment amountFiat,
    Optional<Coin> miningFee,
    Optional<Coin> clientFee,
    TransactionConfidence.ConfidenceType confidenceType,
    PaymentType paymentType,
    String description,
    boolean isCoinbase,
    Collection<Address> outputAddresses,
    String rawTransaction,
    int size,
    boolean isMock
  ) {

    // Apply preconditions if being used in a real environment
    if (!isMock) {
      Preconditions.checkNotNull(transactionId, "'transactionId' must be present");
      Preconditions.checkNotNull(date, "'date' must be present");
      Preconditions.checkNotNull(statusWithOrdinal, "'statusWithOrdinal' must be present");
      Preconditions.checkNotNull(amountBTC, "'amountBTC' must be present");
      Preconditions.checkNotNull(miningFee, "'miningFee' must be present");
      Preconditions.checkNotNull(clientFee, "'clientFee' must be present");
      Preconditions.checkNotNull(confidenceType, "'confidenceType' must be present");
      Preconditions.checkNotNull(paymentType, "'paymentType' must be present");
      Preconditions.checkNotNull(description, "'description' must be present");
      Preconditions.checkNotNull(outputAddresses, "'outputAddress' must be present");
      Preconditions.checkNotNull(rawTransaction, "'rawTransaction' must be present");
    }

    this.transactionId = transactionId;
    this.date = date;
    this.statusWithOrdinal = statusWithOrdinal;
    this.amountBTC = amountBTC;
    this.amountFiat = amountFiat;
    this.miningFee = miningFee;
    this.clientFee = clientFee;
    this.confidenceType = confidenceType;
    this.type = paymentType;
    this.description = description;
    this.coinBase = isCoinbase;
    this.outputAddresses = outputAddresses;
    this.rawTransaction = rawTransaction;
    this.size = size;
  }

  @Override
  public String toString() {
    return "TransactionData{" +
      "transactionId='" + transactionId + '\'' +
      "statusWithOrdinal=" + statusWithOrdinal +
      ", amountBTC=" + amountBTC +
      ", amountFiat=" + amountFiat +
      ", miningFee=" + miningFee +
      ", clientFee=" + clientFee +
      ", confidenceType=" + confidenceType +
      ", type=" + type +
      ", date=" + date +
      ", description='" + description + "'" +
      ", note='" + note + "'" +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TransactionData that = (TransactionData) o;

    if (statusWithOrdinal != that.statusWithOrdinal) {
      return false;
    }
    if (!amountBTC.equals(that.amountBTC)) {
      return false;
    }
    if (amountFiat != null) {
      if (!amountFiat.equals(that.amountFiat)) {
        return false;
      }
    }
    if (confidenceType != that.confidenceType) {
      return false;
    }
    if (!miningFee.equals(that.miningFee)) {
      return false;
    }
    if (!clientFee.equals(that.clientFee)) {
      return false;
    }
    if (!transactionId.equals(that.transactionId)) {
      return false;
    }
    if (!type.equals(that.type)) {
      return false;
    }
    if (!date.equals(that.date)) {
      return false;
    }
    if (!description.equals(that.description)) {
      return false;
    }
    if (!note.equals(that.note)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = transactionId.hashCode();
    result = 31 * result + statusWithOrdinal.hashCode();
    result = 31 * result + amountBTC.hashCode();
    if (amountFiat != null) {
      result = 31 * result + amountFiat.hashCode();
    }
    result = 31 * result + miningFee.hashCode();
    result = 31 * result + clientFee.hashCode();
    result = 31 * result + confidenceType.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + date.hashCode();
    result = 31 * result + description.hashCode();
    if (note != null) {
      result = 31 * result + note.hashCode();
    }
    return result;
  }

  public String getTransactionId() {
    return transactionId;
  }

  @Override
  public Optional<Coin> getAmountCoin() {
    return amountBTC;
  }

  public Optional<Coin> getMiningFee() {
    return miningFee;
  }

  public Optional<Coin> getClientFee() {
    return clientFee;
  }

  public TransactionConfidence.ConfidenceType getConfidenceType() {
    return confidenceType;
  }

  @Override
  public DateTime getDate() {
    return date;
  }

  @Override
  public PaymentStatus getStatus() {
    return statusWithOrdinal;
  }

  @Override
  public PaymentType getType() {
    return type;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public FiatPayment getAmountFiat() {
    return amountFiat;
  }

  public void setAmountFiat(FiatPayment fiatPayment) {
    this.amountFiat = fiatPayment;
  }

  @Override
  public boolean isCoinBase() {
    return coinBase;
  }

  public Collection<Address> getOutputAddresses() {
    return outputAddresses;
  }

  public String getRawTransaction() {
    return rawTransaction;
  }

  public int getSize() {
    return size;
  }
}
