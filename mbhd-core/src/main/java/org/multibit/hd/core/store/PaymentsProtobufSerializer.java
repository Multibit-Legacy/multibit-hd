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
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.exceptions.PaymentsLoadException;
import org.multibit.hd.core.protobuf.MBHDPaymentsProtos;
import org.multibit.hd.core.utils.Numbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
   * <p>A Payments db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
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
   * <p>A payments db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
   * inconsistent data, a wallet extension marked as mandatory that cannot be handled and so on. You should always
   * handle {@link org.multibit.hd.core.exceptions.PaymentsLoadException} and communicate failure to the user in an appropriate manner.</p>
   *
   * @throws org.multibit.hd.core.exceptions.PaymentsLoadException thrown in various error conditions (see description).
   */
  private void readPayments(MBHDPaymentsProtos.Payments paymentsProto, Payments payments) throws PaymentsLoadException {
    Collection<PaymentRequestData> paymentRequestDatas = Lists.newArrayList();
    Collection<TransactionInfo> transactionInfos = Lists.newArrayList();

    List<MBHDPaymentsProtos.PaymentRequest> paymentRequestProtos = paymentsProto.getPaymentRequestList();
    if (paymentRequestProtos != null) {
      for (MBHDPaymentsProtos.PaymentRequest paymentRequestProto : paymentRequestProtos) {
        PaymentRequestData paymentRequestData = new PaymentRequestData();
        paymentRequestData.setAddress(paymentRequestProto.getAddress());
        if (paymentRequestProto.hasLabel()) {
          paymentRequestData.setLabel(paymentRequestProto.getLabel());
        }
        if (paymentRequestProto.hasNote()) {
          paymentRequestData.setNote(paymentRequestProto.getNote());
        }
        if (paymentRequestProto.hasDate()) {
          paymentRequestData.setDate(new DateTime(paymentRequestProto.getDate()));
        }
        if (paymentRequestProto.hasAmountBTC()) {
          paymentRequestData.setAmountBTC(BigInteger.valueOf(paymentRequestProto.getAmountBTC()));
        }

        if (paymentRequestProto.hasAmountFiat()) {

          FiatPayment fiatPayment = new FiatPayment();
          paymentRequestData.setAmountFiat(fiatPayment);
          MBHDPaymentsProtos.FiatPayment fiatPaymentProto = paymentRequestProto.getAmountFiat();

          if (fiatPaymentProto.hasCurrency()) {
            final BigMoney amountFiatAsBigMoney;
            final String fiatCurrencyCode = fiatPaymentProto.getCurrency();
            final CurrencyUnit fiatCurrencyUnit = CurrencyUnit.getInstance(fiatCurrencyCode);

            // May need to adjust the fiat payment amount to include a decimal
            String fiatPaymentAmount = fiatPaymentProto.getAmount();

            amountFiatAsBigMoney = toBigMoney(fiatCurrencyUnit, fiatPaymentAmount);

            fiatPayment.setAmount(amountFiatAsBigMoney);
          }
          if (fiatPaymentProto.hasExchange()) {
            fiatPayment.setExchange(fiatPaymentProto.getExchange());
          }
          if (fiatPaymentProto.hasRate()) {
            fiatPayment.setRate(fiatPaymentProto.getRate());
          }
        }

        paymentRequestDatas.add(paymentRequestData);
      }
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
            transactionInfo.setClientFee(Optional.<BigInteger>absent());
          } else {
            transactionInfo.setClientFee(Optional.of(BigInteger.valueOf(clientFee)));
          }
        } else {
          transactionInfo.setClientFee(Optional.<BigInteger>absent());
        }

        if (transactionInfoProto.hasMinerFee()) {
          long minerFee = transactionInfoProto.getMinerFee();
          if (minerFee == ABSENT_VALUE) {
            transactionInfo.setMinerFee(Optional.<BigInteger>absent());
          } else {
            transactionInfo.setMinerFee(Optional.of(BigInteger.valueOf(minerFee)));
          }
        } else {
          transactionInfo.setMinerFee(Optional.<BigInteger>absent());
        }

        if (transactionInfoProto.hasAmountFiat()) {
          FiatPayment fiatPayment = new FiatPayment();
          transactionInfo.setAmountFiat(fiatPayment);
          MBHDPaymentsProtos.FiatPayment fiatPaymentProto = transactionInfoProto.getAmountFiat();
          if (fiatPaymentProto.hasCurrency()) {

            final BigMoney amountFiatAsBigMoney;
            final String fiatCurrencyCode = fiatPaymentProto.getCurrency();
            final CurrencyUnit fiatCurrencyUnit = CurrencyUnit.getInstance(fiatCurrencyCode);

            // May need to adjust the fiat payment amount to include a decimal
            String fiatPaymentAmount = fiatPaymentProto.getAmount();

            amountFiatAsBigMoney = toBigMoney(fiatCurrencyUnit, fiatPaymentAmount);

            fiatPayment.setAmount(amountFiatAsBigMoney);
          }
          if (fiatPaymentProto.hasExchange()) {
            fiatPayment.setExchange(fiatPaymentProto.getExchange());
          }
          if (fiatPaymentProto.hasRate()) {
            fiatPayment.setRate(fiatPaymentProto.getRate());
          }
        }

        transactionInfos.add(transactionInfo);
      }
    }

    payments.setPaymentRequestDatas(paymentRequestDatas);
    payments.setTransactionInfos(transactionInfos);
  }

  /**
   * @param fiatCurrencyUnit  The fiat currency unit (e.g. "USD")
   * @param fiatPaymentAmount The fiat payment amount (e.g. "1234.56" or "USD 1234.56")
   * @return An appropriate BigMoney representing the amount in the given currency
   */
  private BigMoney toBigMoney(CurrencyUnit fiatCurrencyUnit, String fiatPaymentAmount) {

    BigMoney amountFiatAsBigMoney;

    // Check if string is a number - amount is always persisted in UK locale
    if (Numbers.isNumeric(fiatPaymentAmount, Locale.UK)) {
      // Treat as amount with fiat payment provided currency ("1234.56")
      amountFiatAsBigMoney = BigMoney.of(fiatCurrencyUnit, new BigDecimal(fiatPaymentAmount));
    } else {
      // Treat as money with currency provided in the amount itself ("USD 1234.56")
      if (fiatPaymentAmount.length() > 4) {
        String shortMoney = fiatPaymentAmount.substring(4);
        if (Numbers.isNumeric(shortMoney, Locale.UK)) {
          amountFiatAsBigMoney = BigMoney.of(fiatCurrencyUnit, new BigDecimal(shortMoney));
        } else {
          amountFiatAsBigMoney = null;
        }
      } else {
        // don't know what format this is in - no fiat amount available
        amountFiatAsBigMoney = null;
      }
    }

    return amountFiatAsBigMoney;
  }

  /**
   * Returns the loaded protocol buffer from the given byte stream. This method is designed for low level work involving the
   * wallet file format itself.
   */
  public static MBHDPaymentsProtos.Payments parseToProto(InputStream input) throws IOException {
    return MBHDPaymentsProtos.Payments.parseFrom(input);
  }

  private static MBHDPaymentsProtos.PaymentRequest makePaymentRequestProto(PaymentRequestData paymentRequestData) {
    MBHDPaymentsProtos.PaymentRequest.Builder paymentRequestBuilder = MBHDPaymentsProtos.PaymentRequest.newBuilder();

    if (paymentRequestData != null) {
      paymentRequestBuilder.setAddress(paymentRequestData.getAddress() == null ? "" : paymentRequestData.getAddress());
      paymentRequestBuilder.setNote(paymentRequestData.getNote() == null ? "" : paymentRequestData.getNote());
      paymentRequestBuilder.setAmountBTC(paymentRequestData.getAmountBTC() == null ? 0 : paymentRequestData.getAmountBTC().longValue());
      if (paymentRequestData.getDate() != null) {
        paymentRequestBuilder.setDate(paymentRequestData.getDate().getMillis());
      }
      paymentRequestBuilder.setLabel(paymentRequestData.getLabel() == null ? "" : paymentRequestData.getLabel());

      FiatPayment fiatPayment = paymentRequestData.getAmountFiat();
      if (fiatPayment != null) {
        MBHDPaymentsProtos.FiatPayment.Builder fiatPaymentBuilder = MBHDPaymentsProtos.FiatPayment.newBuilder();
        if (fiatPayment.getAmount() != null && fiatPayment.getAmount().getAmount() != null) {
          fiatPaymentBuilder.setAmount(fiatPayment.getAmount().getAmount().stripTrailingZeros().toPlainString());
        }
        if (fiatPayment.getAmount() != null && fiatPayment.getAmount().getCurrencyUnit() != null) {
          fiatPaymentBuilder.setCurrency(fiatPayment.getAmount().getCurrencyUnit().toString());
        }
        fiatPaymentBuilder.setExchange(fiatPayment.getExchange() == null ? "" : fiatPayment.getExchange());
        fiatPaymentBuilder.setRate(fiatPayment.getRate() == null ? "" : fiatPayment.getRate());

        paymentRequestBuilder.setAmountFiat(fiatPaymentBuilder);
      }
    }

    return paymentRequestBuilder.build();
  }

  private static MBHDPaymentsProtos.TransactionInfo makeTransactionInfoProto(TransactionInfo transactionInfo) {
    MBHDPaymentsProtos.TransactionInfo.Builder transactionInfoBuilder = MBHDPaymentsProtos.TransactionInfo.newBuilder();

    transactionInfoBuilder.setHash(transactionInfo.getHash());
    transactionInfoBuilder.setNote(transactionInfo.getNote());

    Optional<BigInteger> clientFee = transactionInfo.getClientFee();
    if (clientFee != null && clientFee.isPresent() && clientFee.get() != null) {
      transactionInfoBuilder.setClientFee(transactionInfo.getClientFee().get().longValue());
    } else {
      transactionInfoBuilder.setClientFee(ABSENT_VALUE);
    }

    Optional<BigInteger> minerFee = transactionInfo.getMinerFee();
    if (minerFee != null && minerFee.isPresent() && transactionInfo.getMinerFee().get() != null) {
      transactionInfoBuilder.setMinerFee(minerFee.get().longValue());
    } else {
      transactionInfoBuilder.setMinerFee(ABSENT_VALUE);
    }

    FiatPayment fiatPayment = transactionInfo.getAmountFiat();
    if (fiatPayment != null) {
      MBHDPaymentsProtos.FiatPayment.Builder fiatPaymentBuilder = MBHDPaymentsProtos.FiatPayment.newBuilder();
      if (fiatPayment.getAmount() != null && fiatPayment.getAmount().getAmount() != null) {
        fiatPaymentBuilder.setAmount(fiatPayment.getAmount().getAmount().stripTrailingZeros().toPlainString());
      }
      if (fiatPayment.getAmount() != null && fiatPayment.getAmount().getCurrencyUnit() != null) {
        fiatPaymentBuilder.setCurrency(fiatPayment.getAmount().getCurrencyUnit().toString());
      }
      fiatPaymentBuilder.setExchange(fiatPayment.getExchange() == null ? "" : fiatPayment.getExchange());
      fiatPaymentBuilder.setRate(fiatPayment.getRate() == null ? "" : fiatPayment.getRate());

      transactionInfoBuilder.setAmountFiat(fiatPaymentBuilder);
    }

    return transactionInfoBuilder.build();
  }
}
