package org.multibit.hd.ui.views.components.display_amount;

/**
 * <p>Enum to provide the following to "display amount" component:</p>
 * <ul>
 * <li>Encoding of different display styles</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum DisplayAmountStyle {

  /**
   * Iconography and fonts for the main header
   */
  HEADER,
  /**
   * Iconography and fonts for a transaction detail (e.g. confirm or detail screen)
   */
  TRANSACTION_DETAIL_AMOUNT,
  /**
   * Iconography and fonts for a discreet presentation (e.g. fees)
   */
  FEE_AMOUNT

}
