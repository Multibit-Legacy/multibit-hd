package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.Model;

import javax.swing.*;

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

  // View components
  protected JLabel operationText;
  protected JLabel recoveryText;
  protected JTextArea deviceDisplayTextArea;
  protected JLabel spinner;

  /**
   * @param model The model backing this view
   */
  public AbstractHardwareWalletComponentView(M model) {
    super(model);
  }

  @Override
  public void requestInitialFocus() {
    // Do nothing - components are read only
  }

  @Override
  public void updateModelFromView() {
    // Do nothing - the model is driving the view
  }

  public void setOperationText(final MessageKey key, final Object... values) {

    if (SwingUtilities.isEventDispatchThread()) {
      operationText.setText(Languages.safeText(key, values));
    } else {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            operationText.setText(Languages.safeText(key, values));
          }
        });
    }

  }

  /**
   * <p>Update the recovery label with suitable text</p>
   *
   * @param key    The message key defining the recovery text (e.g. "Click next to continue" etc)
   * @param values The message key values
   */
  public void setRecoveryText(final MessageKey key, final Object... values) {

    if (SwingUtilities.isEventDispatchThread()) {
      recoveryText.setText(Languages.safeText(key, values));
    } else {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            recoveryText.setText(Languages.safeText(key, values));
          }
        });
    }

  }

  /**
   * <p>Update the display with suitable text</p>
   *
   * @param key    The message key defining the Trezor text
   * @param values Any supporting values (such as addresses and values)
   */
  public void setDisplayText(final MessageKey key, final Object... values) {

    if (SwingUtilities.isEventDispatchThread()) {
      setDisplayVisible(true);
      deviceDisplayTextArea.setText(Languages.safeText(key, values));
    } else {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            setDisplayVisible(true);
            deviceDisplayTextArea.setText(Languages.safeText(key, values));
          }
        });
    }

  }

  /**
   * <p>Set the visibility of the display text area</p>
   *
   * @param visible True if the display should be visible
   */
  public void setDisplayVisible(final boolean visible) {

    if (SwingUtilities.isEventDispatchThread()) {
      deviceDisplayTextArea.setVisible(visible);
    } else {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            deviceDisplayTextArea.setVisible(visible);
          }
        });
    }

  }

  /**
   * <p>Set the visibility of the spinner control</p>
   *
   * @param visible True if the spinner should be visible (such as when a prolonged operation has been invoked)
   */
  public void setSpinnerVisible(final boolean visible) {

    if (SwingUtilities.isEventDispatchThread()) {
      spinner.setVisible(visible);
    } else {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            spinner.setVisible(visible);
          }
        });
    }

  }

  /**
   * <p>The device has presented incorrect entropy</p>
   */
  public void incorrectEntropy() {

    setOperationText(MessageKey.HARDWARE_FAILURE_OPERATION);

    setDisplayVisible(false);
    setSpinnerVisible(false);

  }
}
