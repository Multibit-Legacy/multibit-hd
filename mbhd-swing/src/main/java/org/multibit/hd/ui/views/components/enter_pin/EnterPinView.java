package org.multibit.hd.ui.views.components.enter_pin;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.fonts.TitleFontDecorator;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of pin entry</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class EnterPinView extends AbstractComponentView<EnterPinModel> {

  /**
   * A read only indicator of the number of pin characters entered
   */
  private JTextField pinText;

  /**
   * A status indicator used to tell the user if PIN is incorrect
   */
  private JLabel pinStatus;

  private final JButton button7 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(7), getModel().get().getPanelName() + ".button_7");
  private final JButton button8 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(8), getModel().get().getPanelName() + ".button_8");
  private final JButton button9 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(9), getModel().get().getPanelName() + ".button_9");
  private final JButton button4 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(4), getModel().get().getPanelName() + ".button_4");
  private final JButton button5 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(5), getModel().get().getPanelName() + ".button_5");
  private final JButton button6 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(6), getModel().get().getPanelName() + ".button_6");
  private final JButton button1 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(1), getModel().get().getPanelName() + ".button_1");
  private final JButton button2 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(2), getModel().get().getPanelName() + ".button_2");
  private final JButton button3 = Buttons.newPinMatrixButton(getPinMatrixButtonAction(3), getModel().get().getPanelName() + ".button_3");

  private final JButton backspaceDeleteButton = Buttons.newBackspaceDeleteButton(getRemoveLastButtonPressedAction());

  /**
   * @param model The model backing this view
   */
  public EnterPinView(EnterPinModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    // Outer panel to align the inner panels
    panel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout(), // Layout
        "[]", // Columns
        "[][]" // Rows
      ));

    // PIN matrix display
    JPanel pinMatrixPanel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout(), // Layout
        "[]12[]12[]", // Columns
        "[][][]" // Rows
      ));

    // PIN display
    JPanel pinDisplayPanel = Panels.newPanel(
      new MigLayout(
        Panels.migXLayout(), // Layout
        "[]", // Columns
        "[][]" // Rows
      ));


    // Arrange PIN matrix buttons to mimic a numeric keypad (1 bottom left, 9 top right)
    pinMatrixPanel.add(button7, MultiBitUI.SMALL_BUTTON_MIG);
    pinMatrixPanel.add(button8, MultiBitUI.SMALL_BUTTON_MIG);
    pinMatrixPanel.add(button9, MultiBitUI.SMALL_BUTTON_MIG + ", wrap");
    pinMatrixPanel.add(button4, MultiBitUI.SMALL_BUTTON_MIG);
    pinMatrixPanel.add(button5, MultiBitUI.SMALL_BUTTON_MIG);
    pinMatrixPanel.add(button6, MultiBitUI.SMALL_BUTTON_MIG + ", wrap");
    pinMatrixPanel.add(button1, MultiBitUI.SMALL_BUTTON_MIG);
    pinMatrixPanel.add(button2, MultiBitUI.SMALL_BUTTON_MIG);
    pinMatrixPanel.add(button3, MultiBitUI.SMALL_BUTTON_MIG + ", wrap");

    pinText = TextBoxes.newReadOnlyTextField(10, MessageKey.ENTER_CURRENT_PIN, MessageKey.ENTER_CURRENT_PIN);
    pinText.setName(getModel().get().getPanelName() + ".textbox");
    TitleFontDecorator.apply(pinText, (float) (MultiBitUI.BALANCE_HEADER_LARGE_FONT_SIZE * 0.6));

    pinStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());

    // Provide a display of numbers entered so far with delete button
    pinDisplayPanel.add(pinText, "wmax 150,hmax 35");
    pinDisplayPanel.add(backspaceDeleteButton, "wrap");

    pinDisplayPanel.add(pinStatus, "wrap");

    panel.add(pinMatrixPanel, "align center,wrap");
    panel.add(pinDisplayPanel, "align center,wrap");

    // Ensure we hide the status display ensure the panel presents correctly
    setPinStatus(false, false);

    return panel;

  }

  @Override
  public void updateModelFromView() {
    // The view is driven from the model
  }

  @Override
  public void updateViewFromModel() {

    // Update the PIN indicator with the length of the entered PIN
    CharSequence pin = getModel().get().getValue();
    pinText.setText(Strings.repeat("*", pin.length()));

    // Ensure we hide the status display (entering new values)
    setPinStatus(true, false);

  }

  /**
   * @return An action that updates the underlying model to remove the last button pressed
   */
  private AbstractAction getRemoveLastButtonPressedAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        getModel().get().removeLastButtonPressed();
        updateViewFromModel();

      }
    };
  }

  /**
   * @param position The button position
   *
   * @return An action that updates the underlying model with the given position
   */
  private Action getPinMatrixButtonAction(final int position) {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        getModel().get().addButtonPressed(position);
        updateViewFromModel();

      }
    };
  }


  @Override
  public void requestInitialFocus() {
  }

  /**
   * @param status  True if successful (check mark), false for failure (cross)
   * @param visible True if the PIN status should be visible
   */
  public void setPinStatus(boolean status, boolean visible) {
    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    // Check if we had to provide a PIN
    if (pinStatus == null) {
      return;
    }

    pinStatus.setVisible(visible);

    if (status) {
      // Success
      pinStatus.setText(Languages.safeText(MessageKey.PIN_SUCCESS));
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, pinStatus, true, MultiBitUI.NORMAL_ICON_SIZE);
    } else {
      if (visible) {
        // Failure rather than default hide
        Sounds.playBeep();
      }
      pinStatus.setText(Languages.safeText(MessageKey.PIN_FAILURE));
      AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, pinStatus, true, MultiBitUI.NORMAL_ICON_SIZE);

      // Clear any previously entered PIN
      getModel().get().setValue("");
      pinText.setText("");
    }
  }

  /**
   * @param enabled True if all buttons should be enabled
   */
  public void setEnabled(final boolean enabled) {

    button1.setEnabled(enabled);
    button2.setEnabled(enabled);
    button3.setEnabled(enabled);
    button4.setEnabled(enabled);
    button5.setEnabled(enabled);
    button6.setEnabled(enabled);
    button7.setEnabled(enabled);
    button8.setEnabled(enabled);
    button9.setEnabled(enabled);

    backspaceDeleteButton.setEnabled(enabled);
  }
}
