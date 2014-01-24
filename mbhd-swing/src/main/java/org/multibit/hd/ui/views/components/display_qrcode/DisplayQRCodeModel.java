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

  private String content;

  @Override
  public String getValue() {
    return content;
  }

  @Override
  public void setValue(String value) {
    this.content = value;
  }

}
