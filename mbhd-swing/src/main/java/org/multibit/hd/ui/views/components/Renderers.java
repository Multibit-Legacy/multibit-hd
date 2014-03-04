package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.components.renderers.AmountBTCTableCellRenderer;
import org.multibit.hd.ui.views.components.renderers.PaymentTypeTableCellRenderer;
import org.multibit.hd.ui.views.components.renderers.RAGStatusTableCellRenderer;
import org.multibit.hd.ui.views.components.renderers.TrailingJustifiedDateTableCellRenderer;
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

  public static DefaultTableCellRenderer newPaymentTypeRenderer() {
     return new PaymentTypeTableCellRenderer();
   }
}
