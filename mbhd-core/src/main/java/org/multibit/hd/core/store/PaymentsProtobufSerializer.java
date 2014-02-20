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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import org.joda.time.DateTime;
import org.multibit.hd.core.exceptions.PaymentsLoadException;
import org.multibit.hd.core.protobuf.MBHDContactsProtos;
import org.multibit.hd.core.protobuf.MBHDPaymentsProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * Serialize and de-serialize payments to a byte stream containing a
 * <a href="http://code.google.com/apis/protocolbuffers/docs/overview.html">protocol buffer</a>. Protocol buffers are
 * a data interchange format developed by Google with an efficient binary representation, a type safe specification
 * language and compilers that generate code to work with those data structures for many languages. Protocol buffers
 * can have their format evolved over time: conceptually they represent data using (tag, length, value) tuples. The
 * format is defined by the <tt>bitcoin.proto</tt> file in the bitcoinj source distribution.<p>
 * <p/>
 * This class is used through its static methods. The most common operations are writePayments and readPayments, which do
 * the obvious operations on Output/InputStreams. You can use a {@link java.io.ByteArrayInputStream} and equivalent
 * {@link java.io.ByteArrayOutputStream} if you'd like byte arrays instead. The protocol buffer can also be manipulated
 * in its object form if you'd like to modify the flattened data structure before serialization to binary.<p>
 *
 * @author Miron Cuperman
 * @author Jim Burton
 */
public class PaymentsProtobufSerializer {
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

    paymentsBuilder.setLastAddressIndex(payments.getLastIndexUsed());

    Collection<PaymentRequest> paymentRequests = payments.getPaymentRequests();
    if (paymentRequests != null) {
      for (PaymentRequest paymentRequest : paymentRequests) {
        MBHDPaymentsProtos.PaymentRequest paymentRequestProto = makePaymentRequestProto(paymentRequest);
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

  private static MBHDPaymentsProtos.PaymentRequest makePaymentRequestProto(PaymentRequest paymentRequest) {
    MBHDPaymentsProtos.PaymentRequest.Builder paymentRequestBuilder = MBHDPaymentsProtos.PaymentRequest.newBuilder();
    paymentRequestBuilder.setAddress(paymentRequest.getAddress());
    paymentRequestBuilder.setNote(paymentRequest.getNote());
    paymentRequestBuilder.setAmountBTC(paymentRequest.getAmountBTC().longValue());
    paymentRequestBuilder.setDate(paymentRequest.getDate().getMillis());
    paymentRequestBuilder.setLabel(paymentRequest.getLabel());

    FiatPayment fiatPayment = paymentRequest.getAmountFiat();
    if (fiatPayment != null) {
      MBHDPaymentsProtos.FiatPayment.Builder fiatPaymentBuilder = MBHDPaymentsProtos.FiatPayment.newBuilder();
      fiatPaymentBuilder.setAmount(fiatPayment.getAmount());
      fiatPaymentBuilder.setCurrency(fiatPayment.getCurrency());
      fiatPaymentBuilder.setExchange(fiatPayment.getExchange());
      fiatPaymentBuilder.setRate(fiatPayment.getRate());

      paymentRequestBuilder.setAmountFiat(fiatPaymentBuilder);
    }

    return paymentRequestBuilder.build();
  }

  private static MBHDPaymentsProtos.TransactionInfo makeTransactionInfoProto(TransactionInfo transactionInfo) {
    MBHDPaymentsProtos.TransactionInfo.Builder transactionInfoBuilder = MBHDPaymentsProtos.TransactionInfo.newBuilder();

    transactionInfoBuilder.setHash(ByteString.copyFrom(transactionInfo.getHash()));
    transactionInfoBuilder.setNote(transactionInfo.getNote());
    Collection<String> requestAddresses = transactionInfo.getRequestAddresses();

    if (requestAddresses != null) {
      for (String requestAddress : requestAddresses) {
        transactionInfoBuilder.addRequestAddress(requestAddress);
      }
    }

    FiatPayment fiatPayment = transactionInfo.getAmountFiat();
    if (fiatPayment != null) {
      MBHDPaymentsProtos.FiatPayment.Builder fiatPaymentBuilder = MBHDPaymentsProtos.FiatPayment.newBuilder();
      fiatPaymentBuilder.setAmount(fiatPayment.getAmount());
      fiatPaymentBuilder.setCurrency(fiatPayment.getCurrency());
      fiatPaymentBuilder.setExchange(fiatPayment.getExchange());
      fiatPaymentBuilder.setRate(fiatPayment.getRate());

      transactionInfoBuilder.setAmountFiat(fiatPaymentBuilder);
    }

    return transactionInfoBuilder.build();
  }

  private static MBHDContactsProtos.Tag makeTagProto(String tag) {
    MBHDContactsProtos.Tag.Builder tagBuilder = MBHDContactsProtos.Tag.newBuilder();
    tagBuilder.setTagValue(tag);
    return tagBuilder.build();
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
      Payments payments = new Payments(paymentsProto.getLastAddressIndex());
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
    Collection<PaymentRequest> paymentRequests = Lists.newArrayList();
    Collection<TransactionInfo> transactionInfos = Lists.newArrayList();

    List<MBHDPaymentsProtos.PaymentRequest> paymentRequestProtos = paymentsProto.getPaymentRequestList();
    if (paymentRequestProtos != null) {
      for (MBHDPaymentsProtos.PaymentRequest paymentRequestProto : paymentRequestProtos) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAddress(paymentRequestProto.getAddress());
        if (paymentRequestProto.hasLabel()) {
          paymentRequest.setLabel(paymentRequestProto.getLabel());
        }
        if (paymentRequestProto.hasNote()) {
          paymentRequest.setNote(paymentRequestProto.getNote());
        }
        if (paymentRequestProto.hasDate()) {
          paymentRequest.setDate(new DateTime(paymentRequestProto.getDate()));
        }
        if (paymentRequestProto.hasAmountBTC()) {
          paymentRequest.setAmountBTC(BigInteger.valueOf(paymentRequestProto.getAmountBTC()));
        }

        if (paymentRequestProto.hasAmountFiat()) {
          FiatPayment fiatPayment = new FiatPayment();
          paymentRequest.setAmountFiat(fiatPayment);
          MBHDPaymentsProtos.FiatPayment fiatPaymentProto = paymentRequestProto.getAmountFiat();
          fiatPayment.setAmount(fiatPaymentProto.getAmount());
          if (fiatPaymentProto.hasCurrency()) {
            fiatPayment.setCurrency(fiatPaymentProto.getCurrency());
          }
          if (fiatPaymentProto.hasExchange()) {
            fiatPayment.setExchange(fiatPaymentProto.getExchange());
          }
          if (fiatPaymentProto.hasRate()) {
            fiatPayment.setRate(fiatPaymentProto.getRate());
          }
        }

        paymentRequests.add(paymentRequest);
      }
    }

    List<MBHDPaymentsProtos.TransactionInfo> transactionInfoProtos = paymentsProto.getTransactionInfoList();
    if (transactionInfoProtos != null) {
      for (MBHDPaymentsProtos.TransactionInfo transactionInfoProto : transactionInfoProtos) {
        org.multibit.hd.core.store.TransactionInfo transactionInfo = new TransactionInfo();

        transactionInfo.setHash(transactionInfoProto.getHash().toByteArray());

        if (transactionInfoProto.hasNote()) {
          transactionInfo.setNote(transactionInfoProto.getNote());
        }

        transactionInfo.setRequestAddresses(transactionInfoProto.getRequestAddressList());

        if (transactionInfoProto.hasAmountFiat()) {
          FiatPayment fiatPayment = new FiatPayment();
          transactionInfo.setAmountFiat(fiatPayment);
          MBHDPaymentsProtos.FiatPayment fiatPaymentProto = transactionInfoProto.getAmountFiat();
          fiatPayment.setAmount(fiatPaymentProto.getAmount());
          if (fiatPaymentProto.hasCurrency()) {
            fiatPayment.setCurrency(fiatPaymentProto.getCurrency());
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

    payments.setPaymentRequests(paymentRequests);
    payments.setTransactionInfos(transactionInfos);
  }

  /**
   * Returns the loaded protocol buffer from the given byte stream. This method is designed for low level work involving the
   * wallet file format itself.
   */
  public static MBHDPaymentsProtos.Payments parseToProto(InputStream input) throws IOException {
    return MBHDPaymentsProtos.Payments.parseFrom(input);
  }
}
