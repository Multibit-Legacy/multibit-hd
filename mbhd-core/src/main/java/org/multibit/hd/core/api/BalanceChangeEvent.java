package org.multibit.hd.core.api;

import java.math.BigDecimal;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of a balance change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BalanceChangeEvent {

  private final BigDecimal localAmount;
  private final String exchangeName;

  /**
   * @param localAmount  The amount in the local currency (e.g. USD)
   * @param exchangeName The exchange name
   */
  public BalanceChangeEvent(BigDecimal localAmount, String exchangeName) {
    this.localAmount = localAmount;
    this.exchangeName = exchangeName;
  }

  public BigDecimal getLocalAmount() {
    return localAmount;
  }

  public String getExchangeName() {
    return exchangeName;
  }

  @Override
  public String toString() {
    return "BalanceChangeEvent{" +
      "bitcoinAmount=" + localAmount +
      ", exchangeName='" + exchangeName + '\'' +
      '}';
  }
}
