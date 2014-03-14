package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.components.renderers.*;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;

import javax.swing.table.DefaultTableCellRenderer;

/**
 *  <p>Factory to provide renderers to
 * <br>
 *  <ul>
 *  <li>Tables</li>
 *  </ul>
 *
 *  </p>
 *  
 */
public class Renderers {
  /**
   * Utilities have no public constructor
   */
  private Renderers() {
  }

  public static DefaultTableCellRenderer newRAGStatusRenderer(PaymentTableModel paymentTableModel) {
    return new RAGStatusTableCellRenderer(paymentTableModel);
  }

  public static DefaultTableCellRenderer newTrailingJustifiedDateRenderer() {
    return new TrailingJustifiedDateTableCellRenderer();
  }

  public static DefaultTableCellRenderer newTrailingJustifiedNumericRenderer() {
    return new AmountBTCTableCellRenderer();
  }
  public static DefaultTableCellRenderer newTrailingJustifiedFiatRenderer() {
    return new AmountFiatTableCellRenderer();
  }

  public static DefaultTableCellRenderer newPaymentTypeRenderer() {
     return new PaymentTypeTableCellRenderer();
  }
}
