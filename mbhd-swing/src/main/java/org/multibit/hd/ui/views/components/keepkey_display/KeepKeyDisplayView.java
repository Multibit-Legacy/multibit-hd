package org.multibit.hd.ui.views.components.keepkey_display;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AbstractHardwareWalletComponentView;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a read only KeepKey device display</li>
 * <li>Accompanying descriptive operation text</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class KeepKeyDisplayView extends AbstractHardwareWalletComponentView<KeepKeyDisplayModel> {

  // View components
  private JLabel operationText;
  private JLabel recoveryText;
  private JTextArea deviceDisplayTextArea;
  private JLabel spinner;

  /**
   * @param model The model backing this view
   */
  public KeepKeyDisplayView(KeepKeyDisplayModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout(), // Layout
        "[]", // Columns
        "[]10[]10[]" // Rows
      ));

    // Initialise the components
    operationText = Labels.newCommunicatingWithHardware();
    recoveryText = Labels.newBlankLabel();
    deviceDisplayTextArea = TextBoxes.newKeepKeyV1Display(getModel().get().getPanelName());

    // Provide an invisible tar pit spinner
    spinner = Labels.newSpinner(Themes.currentTheme.fadedText(), MultiBitUI.NORMAL_PLUS_ICON_SIZE);
    spinner.setVisible(false);

    // Start the KeepKey display as invisible until text is set
    deviceDisplayTextArea.setVisible(false);

    // Add to the panel
    panel.add(operationText, "align center,wrap");
    panel.add(recoveryText, "align center,wrap");
    panel.add(deviceDisplayTextArea, "align center,w 170,wrap");
    panel.add(spinner, "align center,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    // Do nothing - components are read only
  }

  @Override
  public void updateModelFromView() {
    // Do nothing - the model is driving the view
  }


  @Override
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
   * @param key    The message key defining the KeepKey text
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