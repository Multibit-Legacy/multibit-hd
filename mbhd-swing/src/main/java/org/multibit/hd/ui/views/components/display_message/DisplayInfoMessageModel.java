package org.multibit.hd.ui.views.components.display_message;

import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Store the info message text</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayInfoMessageModel implements Model<String> {

  private String message;

  @Override
  public String getValue() {
    return message;
  }

  @Override
  public void setValue(String value) {
    this.message = value;
  }

}
