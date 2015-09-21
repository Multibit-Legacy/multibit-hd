package org.multibit.hd.ui.views.components.keepkey_display;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
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

}