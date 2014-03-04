package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.components.renderers.AmountBTCRenderer;
import org.multibit.hd.ui.views.components.renderers.PaymentTypeRenderer;
import org.multibit.hd.ui.views.components.renderers.RAGStatusRenderer;
import org.multibit.hd.ui.views.components.renderers.TrailingJustifiedDateRenderer;
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
    return new RAGStatusRenderer(paymentTableModel);
  }

  public static DefaultTableCellRenderer newTrailingJustifiedDateRenderer() {
    return new TrailingJustifiedDateRenderer();
  }

  public static DefaultTableCellRenderer newTrailingJustifiedNumericRenderer() {
    return new AmountBTCRenderer();
  }

  public static DefaultTableCellRenderer newPaymentTypeRenderer() {
     return new PaymentTypeRenderer();
   }
}
