package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "empty wallet report" wizard:</p>
 * <ul>
 * <li>Storage of state for the "empty wallet report " panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EmptyWalletReportPanelModel extends AbstractWizardPanelModel {

  // The id of the transaction that this model is describing.
  // May be null
  private String transactionId;

  // Was the transaction created and stored in the wallet successfully ?

  // Absent = undecided, true = created ok, false = failure
  private Optional<Boolean> transactionCreatedSuccessfully;

  private Optional<MessageKey> transactionCreatedSummaryMessageKey;
  private Optional<MessageKey> transactionCreatedDetailMessageKey;


  // Was the transaction broadcast and relayed successfully ?

  // Absent = undecided, true = broadcast ok, false = failure
  private Optional<Boolean> transactionBroadcastSuccessfully;
  private Optional<MessageKey> transactionBroadcastSummaryMessageKey;
  private Optional<MessageKey> transactionBroadcastDetailMessageKey;


  // Was the transaction confirmed on the blockchain ok ?

   // Absent = undecided, true = confirmed ok, false = failure
  private Optional<Boolean> transactionConfirmedSuccessfully;
  private Optional<MessageKey> transactionConfirmedSummaryMessageKey;
  private Optional<MessageKey> transactionConfirmedDetailMessageKey;

  // Number of confirmations on the blockchain. -1 = unknown, 0 = unconfirmed, 1 = one confirmation etc.
  public static final int UNKNOWN_NUMBER_OF_CONFIRMATIONS = -1;
  private int numberOfConfirmations;

  /**
   * @param panelName The panel name
   */
  public EmptyWalletReportPanelModel(
    String panelName
  ) {
    super(panelName);

    // At construction time the model is completely blank.
    transactionId = null;

    // Subscribe methods then transition the state to reflect progress of the send
    transactionCreatedSuccessfully = Optional.absent();
    transactionCreatedSummaryMessageKey = Optional.absent();
    transactionCreatedDetailMessageKey = Optional.absent();

    transactionBroadcastSuccessfully = Optional.absent();
    transactionBroadcastSummaryMessageKey = Optional.absent();
    transactionBroadcastDetailMessageKey = Optional.absent();

    transactionConfirmedSuccessfully = Optional.absent();
    transactionConfirmedSummaryMessageKey = Optional.absent();
    transactionConfirmedDetailMessageKey = Optional.absent();
    numberOfConfirmations = UNKNOWN_NUMBER_OF_CONFIRMATIONS;

  }

  public Optional<Boolean> getTransactionCreatedSuccessfully() {
    return transactionCreatedSuccessfully;
  }

  public void setTransactionCreatedSuccessfully(Optional<Boolean> transactionCreatedSuccessfully) {
    this.transactionCreatedSuccessfully = transactionCreatedSuccessfully;
  }

  public Optional<MessageKey> getTransactionCreatedSummaryMessageKey() {
    return transactionCreatedSummaryMessageKey;
  }

  public void setTransactionCreatedSummaryMessageKey(Optional<MessageKey> transactionCreatedSummaryMessageKey) {
    this.transactionCreatedSummaryMessageKey = transactionCreatedSummaryMessageKey;
  }

  public Optional<MessageKey> getTransactionCreatedDetailMessageKey() {
    return transactionCreatedDetailMessageKey;
  }

  public void setTransactionCreatedDetailMessageKey(Optional<MessageKey> transactionCreatedDetailMessageKey) {
    this.transactionCreatedDetailMessageKey = transactionCreatedDetailMessageKey;
  }

  public Optional<Boolean> getTransactionBroadcastSuccessfully() {
    return transactionBroadcastSuccessfully;
  }

  public void setTransactionBroadcastSuccessfully(Optional<Boolean> transactionBroadcastSuccessfully) {
    this.transactionBroadcastSuccessfully = transactionBroadcastSuccessfully;
  }

  public Optional<MessageKey> getTransactionBroadcastSummaryMessageKey() {
    return transactionBroadcastSummaryMessageKey;
  }

  public void setTransactionBroadcastSummaryMessageKey(Optional<MessageKey> transactionBroadcastSummaryMessageKey) {
    this.transactionBroadcastSummaryMessageKey = transactionBroadcastSummaryMessageKey;
  }

  public Optional<MessageKey> getTransactionBroadcastDetailMessageKey() {
    return transactionBroadcastDetailMessageKey;
  }

  public void setTransactionBroadcastDetailMessageKey(Optional<MessageKey> transactionBroadcastDetailMessageKey) {
    this.transactionBroadcastDetailMessageKey = transactionBroadcastDetailMessageKey;
  }

  public Optional<Boolean> getTransactionConfirmedSuccessfully() {
    return transactionConfirmedSuccessfully;
  }

  public void setTransactionConfirmedSuccessfully(Optional<Boolean> transactionConfirmedSuccessfully) {
    this.transactionConfirmedSuccessfully = transactionConfirmedSuccessfully;
  }

  public Optional<MessageKey> getTransactionConfirmedSummaryMessageKey() {
    return transactionConfirmedSummaryMessageKey;
  }

  public void setTransactionConfirmedSummaryMessageKey(Optional<MessageKey> transactionConfirmedSummaryMessageKey) {
    this.transactionConfirmedSummaryMessageKey = transactionConfirmedSummaryMessageKey;
  }

  public Optional<MessageKey> getTransactionConfirmedDetailMessageKey() {
    return transactionConfirmedDetailMessageKey;
  }

  public void setTransactionConfirmedDetailMessageKey(Optional<MessageKey> transactionConfirmedDetailMessageKey) {
    this.transactionConfirmedDetailMessageKey = transactionConfirmedDetailMessageKey;
  }

  public int getNumberOfConfirmations() {
    return numberOfConfirmations;
  }

  public void setNumberOfConfirmations(int numberOfConfirmations) {
    this.numberOfConfirmations = numberOfConfirmations;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }
}
