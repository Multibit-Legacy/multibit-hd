package org.multibit.hd.ui.views.components.display_qrcode;

import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Store the QR code content</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayQRCodeModel implements Model<String> {

  private String value;

  // Supporting data
  private final String panelName;
  private String transactionLabel;

  public DisplayQRCodeModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  public void setTransactionLabel(String transactionLabel) {
    this.transactionLabel = transactionLabel;
  }

  public String getTransactionLabel() {
    return transactionLabel;
  }

  /**
   * @return The panel name over which this popover is being displayed
   */
  public String getPanelName() {
    return panelName;
  }

}
