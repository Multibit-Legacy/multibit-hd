package org.multibit.hd.ui.views.components.select_file;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>User entry of a seed phrase </li>
 * <li>Support for refresh and reveal operations</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SelectFileView extends AbstractComponentView<SelectFileModel> {

  // View components
  private JTextField selectedFileTextField;
  private JFileChooser fileChooser = new JFileChooser();

  /**
   * @param model The model backing this view
   */
  public SelectFileView(SelectFileModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    SelectFileModel model = getModel().get();

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[][]", // Columns
      "[]10[]" // Rows
    ));

    selectedFileTextField = TextBoxes.newSelectFile();

    // Fill the text area with appropriate content
    selectedFileTextField.setText(model.getValue());

    // Bind a document listener to allow instant update of UI to entered data
    selectedFileTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateModelFromView();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateModelFromView();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateModelFromView();
      }
    });

    // Configure the actions
    Action openSelectFileAction = getOpenSelectFileAction();

    // Add to the panel
    panel.add(selectedFileTextField, "grow,push");
    panel.add(Buttons.newSelectFileButton(openSelectFileAction), "shrink,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    selectedFileTextField.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {
    getModel().get().setValue(selectedFileTextField.getText());
  }

  /**
   * @return A new action for toggling the display of the seed phrase
   */
  private Action getOpenSelectFileAction() {
    // Show or hide the seed phrase
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        SelectFileModel model = getModel().get();

        // Only require a directory
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(currentComponentPanel());

        if (result == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();

          getModel().get().setValue(file.getAbsolutePath());
          getModel().get().setSelected(true);

        } else {
          getModel().get().setSelected(false);
        }

        selectedFileTextField.setText(model.getValue());

      }

    };
  }
}
