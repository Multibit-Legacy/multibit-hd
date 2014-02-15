package org.multibit.hd.ui.views.components.enter_search;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
public class EnterSearchView extends AbstractComponentView<EnterSearchModel> {

  // View components
  private JTextField enterSearchTextField;

  /**
   * @param model The model backing this view
   */
  public EnterSearchView(EnterSearchModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    EnterSearchModel model = getModel().get();

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[][]", // Columns
      "[]10[]" // Rows
    ));

    enterSearchTextField = TextBoxes.newEnterSearch();

    // Provide an initial search
    enterSearchTextField.setText(model.getValue());

    // Bind a key listener to allow instant update of UI to entered data
    enterSearchTextField.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        updateModelFromView();
      }

    });

    // Add to the panel
    panel.add(enterSearchTextField, "grow,push");
    panel.add(Buttons.newSearchButton(getEnterSearchAction()), "shrink,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    enterSearchTextField.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {
    getModel().get().setValue(enterSearchTextField.getText());
  }

  /**
   * @return A new action for toggling the display of the seed phrase
   */
  private Action getEnterSearchAction() {
    // Show or hide the seed phrase
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        getModel().get().setValue(enterSearchTextField.getText());

      }

    };
  }
}
