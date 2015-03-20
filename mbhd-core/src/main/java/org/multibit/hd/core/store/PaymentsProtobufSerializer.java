/**
 * Copyright 2014 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Based on the WalletProtobufSerialiser written by Miron Cuperman, copyright Google (also MIT licence)
 */

package org.multibit.hd.core.store;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.PaymentSessionStatus;
import org.multibit.hd.core.exceptions.PaymentsLoadException;
import org.multibit.hd.core.protobuf.MBHDPaymentsProtos;
import org.multibit.hd.core.utils.Addresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Serialize and de-serialize contacts to a byte stream containing a
 * <a href="http://code.google.com/apis/protocolbuffers/docs/overview.html">protocol buffer</a>.</p>
 * <p/>
 * <p>Protocol buffers are a data interchange format developed by Google with an efficient binary representation, a type safe specification
 * language and compilers that generate code to work with those data structures for many languages. Protocol buffers
 * can have their format evolved over time: conceptually they represent data using (tag, length, value) tuples.</p>
 * <p/>
 * <p>The format is defined by the <tt>payments.proto</tt> file in the MBHD source distribution.</p>
 * <p/>
 * <p>This class is used through its static methods. The most common operations are <code>writePayments</code> and <code>readPayments</code>, which do
 * the obvious operations on Output/InputStreams. You can use a {@link java.io.ByteArrayInputStream} and equivalent
 * {@link java.io.ByteArrayOutputStream} if byte arrays are preferred. The protocol buffer can also be manipulated
 * in its object form if you'd like to modify the flattened data structure before serialization to binary.</p>
 * <p/>
 * <p>Based on the original work by Miron Cuperman for the Bitcoinj project</p>
 */
public class PaymentsProtobufSerializer {

  private static final long ABSENT_VALUE = -1;

  private static final String ABSENT_STRING = "absent";

  private static final Logger log = LoggerFactory.getLogger(PaymentsProtobufSerializer.class);

  public PaymentsProtobufSerializer() {
  }

  /**
   * Formats the given Payments to the given output stream in protocol buffer format.<p>
   */
  public void writePayments(Payments payments, OutputStream output) throws IOException {
    MBHDPaymentsProtos.Payments paymentsProto = paymentsToProto(payments);
    paymentsProto.writeTo(output);
  }

  /**
   * Converts the given payments to the object representation of the protocol buffers. This can be modified, or
   * additional data fields set, before serialization takes place.
   */
  public MBHDPaymentsProtos.Payments paymentsToProto(Payments payments) {
    MBHDPaymentsProtos.Payments.Builder paymentsBuilder = MBHDPaymentsProtos.Payments.newBuilder();

    Preconditions.checkNotNull(payments, "Payments must be specified");

    Collection<MBHDPaymentRequestData> MBHDPaymentRequestDatas = payments.getMBHDPaymentRequestDatas();
    if (MBHDPaymentRequestDatas != null) {
      for (MBHDPaymentRequestData MBHDPaymentRequestData : MBHDPaymentRequestDatas) {
        MBHDPaymentsProtos.MBHDPaymentRequest paymentRequestProto = makeMbhdPaymentRequestProto(MBHDPaymentRequestData);
        paymentsBuilder.addMbhdPaymentRequest(paymentRequestProto);
      }
    }

    Collection<PaymentRequestData> paymentRequestDatas = payments.getPaymentRequestDatas();
    if (paymentRequestDatas != null) {
      for (PaymentRequestData paymentRequestData : paymentRequestDatas) {
        MBHDPaymentsProtos.PaymentRequest paymentRequestProto = makePaymentRequestProto(paymentRequestData);
        paymentsBuilder.addPaymentRequest(paymentRequestProto);
      }
    }

    Collection<TransactionInfo> transactionInfos = payments.getTransactionInfos();
    if (transactionInfos != null) {
      for (TransactionInfo transactionInfo : transactionInfos) {
        MBHDPaymentsProtos.TransactionInfo transactionInfoProto = makeTransactionInfoProto(transactionInfo);
        paymentsBuilder.addTransactionInfo(transactionInfoProto);
      }
    }

    return paymentsBuilder.build();
  }

  /**
   * <p>Parses a Payments from the given stream, using the provided Payments instance to loadContacts data into.
   * <p>A Payments db can be unreadable for various reasons, such as inability to present the file, corrupt data, internally
   * inconsistent data, You should always
   * handle {@link org.multibit.hd.core.exceptions.PaymentsLoadException} and communicate failure to the user in an appropriate manner.</p>
   *
   * @throws org.multibit.hd.core.exceptions.PaymentsLoadException thrown in various error conditions (see description).
   */
  public Payments readPayments(InputStream input) throws PaymentsLoadException {
    try {
      MBHDPaymentsProtos.Payments paymentsProto = parseToProto(input);
      Payments payments = new Payments();
      readPayments(paymentsProto, payments);
      return payments;
    } catch (IOException e) {
      throw new PaymentsLoadException("Could not parse input stream to protobuf", e);
    }
  }

  /**
   * <p>Loads payments data from the given protocol buffer and inserts it into the given Payments object.
   * <p/>
   * <p>A payments db can be unreadable for various reasons, such as inability to present the file, corrupt data, internally
   * inconsistent data, a wallet extension marked as mandatory that cannot be handled and so on. You should always
   * handle {@link org.multibit.hd.core.exceptions.PaymentsLoadException} and communicate failure to the user in an appropriate manner.</p>
   *
   * @throws org.multibit.hd.core.exceptions.PaymentsLoadException thrown in various error conditions (see description).
   */
  private void readPayments(MBHDPaymentsProtos.Payments paymentsProto, Payments payments) throws PaymentsLoadException {
    Collection<MBHDPaymentRequestData> MBHDPaymentRequestDatas = Lists.newArrayList();
    Collection<TransactionInfo> transactionInfos = Lists.newArrayList();
    Collection<PaymentRequestData> paymentRequestDatas = Lists.newArrayList();

    List<MBHDPaymentsProtos.MBHDPaymentRequest> mbhdPaymentRequestProtos = paymentsProto.getMbhdPaymentRequestList();
    if (mbhdPaymentRequestProtos != null) {
      for (MBHDPaymentsProtos.MBHDPaymentRequest mbhdPaymentRequestProto : mbhdPaymentRequestProtos) {

        MBHDPaymentRequestData MBHDPaymentRequestData = new MBHDPaymentRequestData();
        Optional<Address> address = Addresses.parse(mbhdPaymentRequestProto.getAddress());
        if (address.isPresent()) {
          MBHDPaymentRequestData.setAddress(address.get());
        } else {
          log.warn("Failed to parse address: '{}'", mbhdPaymentRequestProto.getAddress());
        }

        if (mbhdPaymentRequestProto.hasLabel()) {
          MBHDPaymentRequestData.setLabel(mbhdPaymentRequestProto.getLabel());
        }
        if (mbhdPaymentRequestProto.hasNote()) {
          MBHDPaymentRequestData.setNote(mbhdPaymentRequestProto.getNote());
        }
        if (mbhdPaymentRequestProto.hasDate()) {
          MBHDPaymentRequestData.setDate(new DateTime(mbhdPaymentRequestProto.getDate()));
        }
        if (mbhdPaymentRequestProto.hasAmountBTC()) {
          MBHDPaymentRequestData.setAmountCoin(Coin.valueOf(mbhdPaymentRequestProto.getAmountBTC()));
        }

        if (mbhdPaymentRequestProto.hasAmountFiat()) {

          FiatPayment fiatPayment = new FiatPayment();

          MBHDPaymentRequestData.setAmountFiat(fiatPayment);
          MBHDPaymentsProtos.FiatPayment fiatPaymentProto = mbhdPaymentRequestProto.getAmountFiat();

          if (fiatPaymentProto.hasCurrency()) {
            final String fiatCurrencyCode = fiatPaymentProto.getCurrency();
            final Optional<Currency> fiatCurrency;
            if (ABSENT_STRING.equals(fiatCurrencyCode)) {
              fiatCurrency = Optional.absent();
            } else {
              fiatCurrency = Optional.of(Currency.getInstance(fiatCurrencyCode));
            }
            fiatPayment.setCurrency(fiatCurrency);

            String fiatPaymentAmount = fiatPaymentProto.getAmount();
            Optional<BigDecimal> amountFiat;
            if (ABSENT_STRING.equals(fiatPaymentAmount)) {
              amountFiat = Optional.absent();
            } else {
              amountFiat = Optional.of(new BigDecimal(fiatPaymentAmount));
            }
            fiatPayment.setAmount(amountFiat);
          }

          if (fiatPaymentProto.hasExchange()) {
            if (ABSENT_STRING.equals(fiatPaymentProto.getExchange())) {
              fiatPayment.setExchangeName(Optional.<String>absent());
            } else {
              fiatPayment.setExchangeName(Optional.of(fiatPaymentProto.getExchange()));
            }
          }
          if (fiatPaymentProto.hasRate()) {
            fiatPayment.setRate(Optional.of(fiatPaymentProto.getRate()));
            if (ABSENT_STRING.equals(fiatPaymentProto.getRate())) {
              fiatPayment.setRate(Optional.<String>absent());
            } else {
              fiatPayment.setRate(Optional.of(fiatPaymentProto.getRate()));
            }
          }
        }

        MBHDPaymentRequestDatas.add(MBHDPaymentRequestData);
      }
    }

    List<MBHDPaymentsProtos.PaymentRequest> paymentRequestProtos = paymentsProto.getPaymentRequestList();

    for (MBHDPaymentsProtos.PaymentRequest paymentRequestProto : paymentRequestProtos) {
      PaymentRequestData paymentRequestData = new PaymentRequestData();

      if (paymentRequestProto.hasUuid()) {
        paymentRequestData.setUuid(UUID.fromString(paymentRequestProto.getUuid()));
      }

      if (paymentRequestProto.hasHash() && !paymentRequestProto.getHash().isEmpty()) {
        paymentRequestData.setTransactionHashOptional(Optional.of(new Sha256Hash(paymentRequestProto.getHash())));
      } else {
        paymentRequestData.setTransactionHashOptional(Optional.<Sha256Hash>absent());
      }

      if (paymentRequestProto.hasNote()) {
        paymentRequestData.setNote(paymentRequestProto.getNote());
      }
      if (paymentRequestProto.hasDate()) {
        paymentRequestData.setDate(new DateTime(paymentRequestProto.getDate()));
      }
      if (paymentRequestProto.hasAmountBTC()) {
        paymentRequestData.setAmountCoin(Coin.valueOf(paymentRequestProto.getAmountBTC()));
      }
      if (paymentRequestProto.hasIdentityDisplayName()) {
        paymentRequestData.setIdentityDisplayName(paymentRequestProto.getIdentityDisplayName());
      }
      if (paymentRequestProto.hasExpirationDate()) {
        paymentRequestData.setExpirationDate(new DateTime(paymentRequestProto.getExpirationDate()));
      }
      if (paymentRequestProto.hasTrustStatus()) {
        String trustStatusText = paymentRequestProto.getTrustStatus();
        if (PaymentSessionStatus.TRUSTED.name().equals(trustStatusText)) {
          paymentRequestData.setTrustStatus(PaymentSessionStatus.TRUSTED);
        } else if (PaymentSessionStatus.UNTRUSTED.name().equals(trustStatusText)) {
          paymentRequestData.setTrustStatus(PaymentSessionStatus.UNTRUSTED);
        } else if (PaymentSessionStatus.DOWN.name().equals(trustStatusText)) {
          paymentRequestData.setTrustStatus(PaymentSessionStatus.DOWN);
        } else if (PaymentSessionStatus.ERROR.name().equals(trustStatusText)) {
          paymentRequestData.setTrustStatus(PaymentSessionStatus.ERROR);
        } else {
          paymentRequestData.setTrustStatus(PaymentSessionStatus.UNKNOWN);
        }
      }

      if (paymentRequestProto.hasTrustErrorMessage()) {
        paymentRequestData.setTrustErrorMessage(paymentRequestProto.getTrustErrorMessage());
      }

      if (paymentRequestProto.hasAmountFiat()) {
        FiatPayment fiatPayment = new FiatPayment();

        paymentRequestData.setAmountFiat(fiatPayment);
        MBHDPaymentsProtos.FiatPayment fiatPaymentProto = paymentRequestProto.getAmountFiat();

        if (fiatPaymentProto.hasCurrency()) {
          final String fiatCurrencyCode = fiatPaymentProto.getCurrency();
          final Optional<Currency> fiatCurrency;
          if (ABSENT_STRING.equals(fiatCurrencyCode)) {
            fiatCurrency = Optional.absent();
          } else {
            fiatCurrency = Optional.of(Currency.getInstance(fiatCurrencyCode));
          }
          fiatPayment.setCurrency(fiatCurrency);

          String fiatPaymentAmount = fiatPaymentProto.getAmount();
          Optional<BigDecimal> amountFiat;
          if (ABSENT_STRING.equals(fiatPaymentAmount)) {
            amountFiat = Optional.absent();
          } else {
            amountFiat = Optional.of(new BigDecimal(fiatPaymentAmount));
          }
          fiatPayment.setAmount(amountFiat);
        }

        if (fiatPaymentProto.hasExchange()) {
          if (ABSENT_STRING.equals(fiatPaymentProto.getExchange())) {
            fiatPayment.setExchangeName(Optional.<String>absent());
          } else {
            fiatPayment.setExchangeName(Optional.of(fiatPaymentProto.getExchange()));
          }
        }
        if (fiatPaymentProto.hasRate()) {
          fiatPayment.setRate(Optional.of(fiatPaymentProto.getRate()));
          if (ABSENT_STRING.equals(fiatPaymentProto.getRate())) {
            fiatPayment.setRate(Optional.<String>absent());
          } else {
            fiatPayment.setRate(Optional.of(fiatPaymentProto.getRate()));
          }
        }
      }

      paymentRequestDatas.add(paymentRequestData);
    }


    List<MBHDPaymentsProtos.TransactionInfo> transactionInfoProtos = paymentsProto.getTransactionInfoList();
    if (transactionInfoProtos != null) {
      for (MBHDPaymentsProtos.TransactionInfo transactionInfoProto : transactionInfoProtos) {
        org.multibit.hd.core.store.TransactionInfo transactionInfo = new TransactionInfo();

        transactionInfo.setHash(transactionInfoProto.getHash());

        if (transactionInfoProto.hasNote()) {
          transactionInfo.setNote(transactionInfoProto.getNote());
        }

        if (transactionInfoProto.hasClientFee()) {
          long clientFee = transactionInfoProto.getClientFee();
          if (clientFee == ABSENT_VALUE) {
            transactionInfo.setClientFee(Optional.<Coin>absent());
          } else {
            transactionInfo.setClientFee(Optional.of(Coin.valueOf(clientFee)));
          }
        } else {
          transactionInfo.setClientFee(Optional.<Coin>absent());
        }

        if (transactionInfoProto.hasMinerFee()) {
          long minerFee = transactionInfoProto.getMinerFee();
          if (minerFee == ABSENT_VALUE) {
            transactionInfo.setMinerFee(Optional.<Coin>absent());
          } else {
            transactionInfo.setMinerFee(Optional.of(Coin.valueOf(minerFee)));
          }
        } else {
          transactionInfo.setMinerFee(Optional.<Coin>absent());
        }

        if (transactionInfoProto.hasAmountFiat()) {
          FiatPayment fiatPayment = new FiatPayment();
          transactionInfo.setAmountFiat(fiatPayment);
          MBHDPaymentsProtos.FiatPayment fiatPaymentProto = transactionInfoProto.getAmountFiat();
          if (fiatPaymentProto.hasCurrency()) {
            final String fiatCurrencyCode = fiatPaymentProto.getCurrency();
            final Optional<Currency> fiatCurrency;
            if (ABSENT_STRING.equals(fiatCurrencyCode)) {
              fiatCurrency = Optional.absent();
            } else {
              fiatCurrency = Optional.of(Currency.getInstance(fiatCurrencyCode));
            }
            fiatPayment.setCurrency(fiatCurrency);

            String fiatPaymentAmount = fiatPaymentProto.getAmount();
            Optional<BigDecimal> amountFiat;
            if (ABSENT_STRING.equals(fiatPaymentAmount)) {
              amountFiat = Optional.absent();
            } else {
              amountFiat = Optional.of(new BigDecimal(fiatPaymentAmount));
            }

            fiatPayment.setAmount(amountFiat);
          }
          if (fiatPaymentProto.hasExchange()) {
            if (ABSENT_STRING.equals(fiatPaymentProto.getExchange())) {
              fiatPayment.setExchangeName(Optional.<String>absent());
            } else {
              fiatPayment.setExchangeName(Optional.of(fiatPaymentProto.getExchange()));
            }
          }
          if (fiatPaymentProto.hasRate()) {
            fiatPayment.setRate(Optional.of(fiatPaymentProto.getRate()));
            if (ABSENT_STRING.equals(fiatPaymentProto.getRate())) {
              fiatPayment.setRate(Optional.<String>absent());
            } else {
              fiatPayment.setRate(Optional.of(fiatPaymentProto.getRate()));
            }
          }
        }

        if (transactionInfoProto.hasSentBySelf()) {
          transactionInfo.setSentBySelf(transactionInfoProto.getSentBySelf());
        }

        transactionInfos.add(transactionInfo);
      }
    }

    payments.setMBHDPaymentRequestDatas(MBHDPaymentRequestDatas);
    payments.setTransactionInfos(transactionInfos);
    payments.setPaymentRequestDatas(paymentRequestDatas);
  }

  /**
   * Returns the loaded protocol buffer from the given byte stream. This method is designed for low level work involving the
   * wallet file format itself.
   */

  public static MBHDPaymentsProtos.Payments parseToProto(InputStream input) throws IOException {
    return MBHDPaymentsProtos.Payments.parseFrom(input);
  }

  private static MBHDPaymentsProtos.MBHDPaymentRequest makeMbhdPaymentRequestProto(MBHDPaymentRequestData MBHDPaymentRequestData) {
    MBHDPaymentsProtos.MBHDPaymentRequest.Builder paymentRequestBuilder = MBHDPaymentsProtos.MBHDPaymentRequest.newBuilder();

    if (MBHDPaymentRequestData != null) {

      paymentRequestBuilder.setAddress(MBHDPaymentRequestData.getAddress() == null ? "" : MBHDPaymentRequestData.getAddress().toString());
      paymentRequestBuilder.setNote(MBHDPaymentRequestData.getNote() == null ? "" : MBHDPaymentRequestData.getNote());
      paymentRequestBuilder.setAmountBTC(MBHDPaymentRequestData.getAmountCoin() == null ? 0 : MBHDPaymentRequestData.getAmountCoin().longValue());

      if (MBHDPaymentRequestData.getDate() != null) {
        paymentRequestBuilder.setDate(MBHDPaymentRequestData.getDate().getMillis());
      }
      paymentRequestBuilder.setLabel(MBHDPaymentRequestData.getLabel() == null ? "" : MBHDPaymentRequestData.getLabel());

      FiatPayment fiatPayment = MBHDPaymentRequestData.getAmountFiat();
      if (fiatPayment != null) {

        MBHDPaymentsProtos.FiatPayment.Builder fiatPaymentBuilder = MBHDPaymentsProtos.FiatPayment.newBuilder();

        // Amount
        if (fiatPayment.getAmount() != null && fiatPayment.getAmount().isPresent()
                && fiatPayment.getCurrency() != null && fiatPayment.getCurrency().isPresent()) {
          fiatPaymentBuilder.setAmount(fiatPayment.getAmount().get().stripTrailingZeros().toPlainString());
          fiatPaymentBuilder.setCurrency(fiatPayment.getCurrency().get().getCurrencyCode());
        } else {
          fiatPaymentBuilder.setAmount(ABSENT_STRING);
          fiatPaymentBuilder.setCurrency(ABSENT_STRING);
        }

        if (fiatPayment.getExchangeName().isPresent()) {
          fiatPaymentBuilder.setExchange(fiatPayment.getExchangeName().get());
        } else {
          fiatPaymentBuilder.setExchange(ABSENT_STRING);
        }
        if (fiatPayment.getRate().isPresent()) {
          fiatPaymentBuilder.setRate(fiatPayment.getRate().get());
        } else {
          fiatPaymentBuilder.setRate(ABSENT_STRING);
        }

        paymentRequestBuilder.setAmountFiat(fiatPaymentBuilder);
      }
    }

    return paymentRequestBuilder.build();
  }

  private static MBHDPaymentsProtos.PaymentRequest makePaymentRequestProto(PaymentRequestData paymentRequestData) {
    MBHDPaymentsProtos.PaymentRequest.Builder paymentRequestBuilder = MBHDPaymentsProtos.PaymentRequest.newBuilder();

    if (paymentRequestData != null) {

      paymentRequestBuilder.setUuid(paymentRequestData.getUuid().toString());
      paymentRequestBuilder.setHash(paymentRequestData.getTransactionHashOptional().isPresent() ? paymentRequestData.getTransactionHashOptional().get().toString() : "");
      paymentRequestBuilder.setNote(paymentRequestData.getNote() == null ? "" : paymentRequestData.getNote());
      paymentRequestBuilder.setAmountBTC(paymentRequestData.getAmountCoin() == null ? 0 : paymentRequestData.getAmountCoin().longValue());
      paymentRequestBuilder.setIdentityDisplayName(paymentRequestData.getIdentityDisplayName() == null ? "" : paymentRequestData.getIdentityDisplayName());
      paymentRequestBuilder.setTrustErrorMessage(paymentRequestData.getTrustErrorMessage() == null ? "" : paymentRequestData.getTrustErrorMessage());

      if (paymentRequestData.getTrustStatus() != null) {
        paymentRequestBuilder.setTrustStatus(paymentRequestData.getTrustStatus().name());
      } else {
        paymentRequestBuilder.setTrustStatus(PaymentSessionStatus.UNKNOWN.name());
      }


      if (paymentRequestData.getDate() != null) {
        paymentRequestBuilder.setDate(paymentRequestData.getDate().getMillis());
      }
      if (paymentRequestData.getExpirationDate() != null) {
        paymentRequestBuilder.setExpirationDate(paymentRequestData.getExpirationDate().getMillis());
      }

      FiatPayment fiatPayment = paymentRequestData.getAmountFiat();
      if (fiatPayment != null) {

        MBHDPaymentsProtos.FiatPayment.Builder fiatPaymentBuilder = MBHDPaymentsProtos.FiatPayment.newBuilder();

        // Amount
        if (fiatPayment.getAmount() != null && fiatPayment.getAmount().isPresent()
                && fiatPayment.getCurrency() != null && fiatPayment.getCurrency().isPresent()) {
          fiatPaymentBuilder.setAmount(fiatPayment.getAmount().get().stripTrailingZeros().toPlainString());
          fiatPaymentBuilder.setCurrency(fiatPayment.getCurrency().get().getCurrencyCode());
        } else {
          fiatPaymentBuilder.setAmount(ABSENT_STRING);
          fiatPaymentBuilder.setCurrency(ABSENT_STRING);
        }

        if (fiatPayment.getExchangeName().isPresent()) {
          fiatPaymentBuilder.setExchange(fiatPayment.getExchangeName().get());
        } else {
          fiatPaymentBuilder.setExchange(ABSENT_STRING);
        }
        if (fiatPayment.getRate().isPresent()) {
          fiatPaymentBuilder.setRate(fiatPayment.getRate().get());
        } else {
          fiatPaymentBuilder.setRate(ABSENT_STRING);
        }

        paymentRequestBuilder.setAmountFiat(fiatPaymentBuilder);
      }
    }

    return paymentRequestBuilder.build();
  }

  private static MBHDPaymentsProtos.TransactionInfo makeTransactionInfoProto(TransactionInfo transactionInfo) {

    MBHDPaymentsProtos.TransactionInfo.Builder transactionInfoBuilder = MBHDPaymentsProtos.TransactionInfo.newBuilder();

    transactionInfoBuilder.setHash(transactionInfo.getHash());
    transactionInfoBuilder.setNote(transactionInfo.getNote());

    Optional<Coin> clientFee = transactionInfo.getClientFee();
    if (clientFee != null && clientFee.isPresent() && clientFee.get() != null) {
      transactionInfoBuilder.setClientFee(transactionInfo.getClientFee().get().longValue());
    } else {
      transactionInfoBuilder.setClientFee(ABSENT_VALUE);
    }

    Optional<Coin> minerFee = transactionInfo.getMinerFee();
    if (minerFee != null && minerFee.isPresent() && minerFee.get() != null) {
      transactionInfoBuilder.setMinerFee(minerFee.get().longValue());
    } else {
      transactionInfoBuilder.setMinerFee(ABSENT_VALUE);
    }

    FiatPayment fiatPayment = transactionInfo.getAmountFiat();
    if (fiatPayment != null) {
      MBHDPaymentsProtos.FiatPayment.Builder fiatPaymentBuilder = MBHDPaymentsProtos.FiatPayment.newBuilder();
      if (fiatPayment.getAmount() != null && fiatPayment.getAmount().isPresent()) {
        fiatPaymentBuilder.setAmount(fiatPayment.getAmount().get().stripTrailingZeros().toPlainString());
      } else {
        fiatPaymentBuilder.setAmount(ABSENT_STRING);
      }
      if (fiatPayment.getAmount() != null && fiatPayment.getAmount().isPresent() && fiatPayment.getCurrency() != null && fiatPayment.getCurrency().isPresent()) {
        fiatPaymentBuilder.setCurrency(fiatPayment.getCurrency().get().getCurrencyCode());
      } else {
        fiatPaymentBuilder.setCurrency(ABSENT_STRING);
      }
      if (fiatPayment.getExchangeName().isPresent()) {
        fiatPaymentBuilder.setExchange(fiatPayment.getExchangeName().get());
      } else {
        fiatPaymentBuilder.setExchange(ABSENT_STRING);
      }
      if (fiatPayment.getRate().isPresent()) {
        fiatPaymentBuilder.setRate(fiatPayment.getRate().get());
      } else {
        fiatPaymentBuilder.setRate(ABSENT_STRING);
      }

      transactionInfoBuilder.setAmountFiat(fiatPaymentBuilder);
    }

    transactionInfoBuilder.setSentBySelf(transactionInfo.isSentBySelf());

    return transactionInfoBuilder.build();
  }
}
