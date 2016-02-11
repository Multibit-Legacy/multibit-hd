package org.multibit.hd.core.store;

import org.bitcoinj.core.Coin;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.commons.files.SecureFiles;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.utils.Addresses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;

public class PaymentsProtobufSerializerTest {

  private PaymentsProtobufSerializer serializer;

  private File paymentsFile;

  @Before
  public void setUp() throws Exception {

    File temporaryDirectory = SecureFiles.createTemporaryDirectory();
    paymentsFile = new File(temporaryDirectory.getAbsolutePath() + File.separator + WalletService.PAYMENTS_DATABASE_NAME);

    serializer = new PaymentsProtobufSerializer();
  }



  @Test
  public void testRequests() throws Exception {
    // Test you can add some payment requests and read them back
    Collection<MBHDPaymentRequestData> mbhdPaymentRequestDataCollection = Lists.newArrayList();

    MBHDPaymentRequestData mbhdPaymentRequestData1 = new MBHDPaymentRequestData();

    mbhdPaymentRequestData1.setGlidera(true);
    mbhdPaymentRequestData1.setAddress(Addresses.parse("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty").get());
    mbhdPaymentRequestData1.setAmountCoin(Optional.of(Coin.valueOf(245)));
    DateTime date1 = new DateTime();
    mbhdPaymentRequestData1.setDate(date1);
    mbhdPaymentRequestData1.setLabel("label1");
    mbhdPaymentRequestData1.setNote("note1");

    FiatPayment fiatPayment1 = new FiatPayment();
    mbhdPaymentRequestData1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(Optional.of(new BigDecimal("12345.6")));
    fiatPayment1.setCurrency(Optional.of(Currency.getInstance("USD")));
    fiatPayment1.setRate(Optional.of("10.0"));
    fiatPayment1.setExchangeName(Optional.of("Bitstamp"));

    MBHDPaymentRequestData mbhdPaymentRequestData2 = new MBHDPaymentRequestData();
    mbhdPaymentRequestData2.setAddress(Addresses.parse("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty").get());
    mbhdPaymentRequestData2.setAmountCoin(Optional.of(Coin.valueOf(789)));
    DateTime date2 = date1.plusDays(7);
    mbhdPaymentRequestData2.setDate(date2);
    mbhdPaymentRequestData2.setLabel("label2");
    mbhdPaymentRequestData2.setNote("note2");

    FiatPayment fiatPayment2 = new FiatPayment();
    mbhdPaymentRequestData2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount(Optional.of(new BigDecimal("12345.678")));
    fiatPayment2.setCurrency(Optional.of(Currency.getInstance("GBP")));
    fiatPayment2.setRate(Optional.of("20.0"));
    fiatPayment2.setExchangeName(Optional.of("OER"));

    mbhdPaymentRequestDataCollection.add(mbhdPaymentRequestData1);
    mbhdPaymentRequestDataCollection.add(mbhdPaymentRequestData2);

    Payments payments = new Payments();
    payments.setMBHDPaymentRequestDataCollection(mbhdPaymentRequestDataCollection);

    Payments newPayments = roundTrip(payments);

    Collection<MBHDPaymentRequestData> newMBHDPaymentRequestDataCollection = newPayments.getMBHDPaymentRequestDataCollection();
    assertThat(newMBHDPaymentRequestDataCollection.size()).isEqualTo(2);

    Iterator<MBHDPaymentRequestData> iterator = newMBHDPaymentRequestDataCollection.iterator();
    MBHDPaymentRequestData newMBHDPaymentRequestData1 = iterator.next();
    MBHDPaymentRequestData newMBHDPaymentRequestData2 = iterator.next();

    checkPaymentRequest(mbhdPaymentRequestData1, newMBHDPaymentRequestData1);
    checkPaymentRequest(mbhdPaymentRequestData2, newMBHDPaymentRequestData2);
  }

  private void checkPaymentRequest(MBHDPaymentRequestData MBHDPaymentRequestData, MBHDPaymentRequestData other) {
    assertThat(other.isGlidera() == MBHDPaymentRequestData.isGlidera()).isTrue();
    assertThat(other.getAddress()).isEqualTo(MBHDPaymentRequestData.getAddress());
    assertThat(other.getLabel()).isEqualTo(MBHDPaymentRequestData.getLabel());
    assertThat(other.getNote()).isEqualTo(MBHDPaymentRequestData.getNote());
    assertThat(other.getAmountCoin()).isEqualTo(MBHDPaymentRequestData.getAmountCoin());
    assertThat(other.getDate()).isEqualTo(MBHDPaymentRequestData.getDate());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = MBHDPaymentRequestData.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchangeName()).isEqualTo(otherFiatPayment.getExchangeName());
  }

  @Test
  public void testTransactionInfos() throws Exception {
    Collection<TransactionInfo> transactionInfos = Lists.newArrayList();

    TransactionInfo transactionInfo1 = new TransactionInfo();
    transactionInfos.add(transactionInfo1);
    transactionInfo1.setHash("010203");
    transactionInfo1.setNote("notes1");
    transactionInfo1.setSentBySelf(false);

    FiatPayment fiatPayment1 = new FiatPayment();
    transactionInfo1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(Optional.of(new BigDecimal("99.9")));
    fiatPayment1.setCurrency(Optional.of(Currency.getInstance("EUR")));
    fiatPayment1.setRate(Optional.of("30.0"));
    fiatPayment1.setExchangeName(Optional.of("Bitstamp"));

    transactionInfo1.setClientFee(Optional.<Coin>absent());
    transactionInfo1.setMinerFee(Optional.of(Coin.valueOf(123)));

    TransactionInfo transactionInfo2 = new TransactionInfo();
    transactionInfos.add(transactionInfo2);
    transactionInfo2.setHash("010203");
    transactionInfo2.setNote("notes1");
    transactionInfo1.setSentBySelf(true);

    FiatPayment fiatPayment2 = new FiatPayment();
    transactionInfo2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount(Optional.of(new BigDecimal("11.1")));
    fiatPayment2.setCurrency(Optional.of(Currency.getInstance("JPY")));
    fiatPayment2.setRate(Optional.of("50.0"));
    fiatPayment2.setExchangeName(Optional.of("BitstampJunior"));

    transactionInfo2.setClientFee(Optional.of(Coin.valueOf(456)));
    transactionInfo2.setMinerFee(Optional.<Coin>absent());

    TransactionInfo transactionInfo3 = new TransactionInfo();
     transactionInfos.add(transactionInfo3);
     transactionInfo3.setHash("01020304");
     transactionInfo3.setNote("notes3");

     FiatPayment fiatPayment3 = new FiatPayment();
     transactionInfo3.setAmountFiat(fiatPayment3);

     transactionInfo3.setClientFee(Optional.of(Coin.valueOf(456)));
     transactionInfo3.setMinerFee(Optional.<Coin>absent());


    Payments payments = new Payments();
    payments.setTransactionInfoCollection(transactionInfos);

    Payments newPayments = roundTrip(payments);


    Collection<TransactionInfo> newTransactionInfos = newPayments.getTransactionInfoCollection();
    assertThat(newTransactionInfos.size()).isEqualTo(3);

    Iterator<TransactionInfo> iterator = newTransactionInfos.iterator();
    TransactionInfo newTransactionInfo1 = iterator.next();
    TransactionInfo newTransactionInfo2 = iterator.next();
    TransactionInfo newTransactionInfo3 = iterator.next();

    checkTransactionInfo(transactionInfo1, newTransactionInfo1);
    checkTransactionInfo(transactionInfo2, newTransactionInfo2);
    checkTransactionInfo(transactionInfo3, newTransactionInfo3);
  }

  private void checkTransactionInfo(TransactionInfo transactionInfo, TransactionInfo other) throws Exception {
    assertThat(other.getHash().equals(transactionInfo.getHash())).isTrue();

    assertThat(other.getNote()).isEqualTo(transactionInfo.getNote());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = transactionInfo.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchangeName()).isEqualTo(otherFiatPayment.getExchangeName());

    assertThat(transactionInfo.getClientFee()).isEqualTo(other.getClientFee());
    assertThat(transactionInfo.getMinerFee()).isEqualTo(other.getMinerFee());
  }

  /**
   * Round trip the payments i.e. writePayments to disk and read back in
   *
   * @throws Exception
   */
  public Payments roundTrip(Payments payments) throws Exception {

    // Store the payments to the backing writeContacts
    serializer.writePayments(payments, new FileOutputStream(paymentsFile));

    // Reload it
    return serializer.readPayments(new FileInputStream((paymentsFile)));
  }
}
