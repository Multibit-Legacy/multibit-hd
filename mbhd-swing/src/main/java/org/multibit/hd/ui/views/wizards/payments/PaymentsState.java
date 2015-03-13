package org.multibit.hd.ui.views.wizards.payments;

/**
 * <p>Enum to provide the following to "payments" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum PaymentsState {

  /**
   * Show overview of transaction
   */
  TRANSACTION_OVERVIEW,

  /**
   * Show the bitcoin and fiat amounts for the transaction
   */
  TRANSACTION_AMOUNT,

  /**
   * Show technical information about the transaction
   */
  TRANSACTION_DETAIL,

  /**
   * Choose between 0, 1 or more MBHD payment requests
   */
  CHOOSE_PAYMENT_REQUEST,

  /**
   * Details of a MBHD payment request
   */
  PAYMENT_REQUEST_DETAILS,

  /**
   * Details of a BIP70 payment request
   */
  BIP70_PAYMENT_REQUEST_DETAILS

  // End of enum
}
