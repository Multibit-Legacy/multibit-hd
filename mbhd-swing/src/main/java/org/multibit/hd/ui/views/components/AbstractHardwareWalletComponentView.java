package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.Model;

/**
 * <p>Abstract base class to provide the following to components:</p>
 * <ul>
 * <li>Standard implementations of lifecycle methods between a component view and its model</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public abstract class AbstractHardwareWalletComponentView<M extends Model> extends AbstractComponentView<M> {

  /**
   * @param model The model backing this view
   */
  public AbstractHardwareWalletComponentView(M model) {
    super(model);
  }

  /**
   * <p>Update the operation label with suitable text</p>
   *
   * @param key    The message key defining the operation text (e.g. "Communicating with Trezor")
   * @param values The message key values
   */
  public abstract void setOperationText(MessageKey key, Object... values);

  public abstract void setDisplayText(MessageKey key, Object... values);

}
