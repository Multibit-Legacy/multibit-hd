package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

import java.math.BigDecimal;

/**
 * <p>Model object to provide the following to "exit" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ReceiveBitcoinWizardModel extends AbstractWizardModel<ReceiveBitcoinState> {

  private BigDecimal bitcoinAmount = BigDecimal.ZERO;
  private Optional<String> recipientAddress = Optional.absent();
  private Optional<String> transactionLabel = Optional.absent();

  private EnterAmountModel enterAmountModel;
  private String label;

  /**
   * @param state The state object
   */
  public ReceiveBitcoinWizardModel(ReceiveBitcoinState state) {
    super(state);
  }

  /**
   * @return The plain
   */
  public BigDecimal getBitcoinAmount() {
    return bitcoinAmount;
  }

  public void setBitcoinAmount(BigDecimal bitcoinAmount) {
    this.bitcoinAmount = bitcoinAmount;
  }

  public Optional<String> getRecipientAddress() {
    return recipientAddress;
  }

  public void setRecipientAddress(String recipientAddress) {
    this.recipientAddress = Optional.of(recipientAddress);
  }

  public Optional<String> getTransactionLabel() {
    return transactionLabel;
  }

  public void setTransactionLabel(String transactionLabel) {
    this.transactionLabel = Optional.of(transactionLabel);
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param enterAmountModel The "enter amount" model
   */
  void setEnterAmountModel(EnterAmountModel enterAmountModel) {
    this.enterAmountModel = enterAmountModel;
  }
}
