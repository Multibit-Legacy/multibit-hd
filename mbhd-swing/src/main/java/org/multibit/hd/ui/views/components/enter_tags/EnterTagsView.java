package org.multibit.hd.ui.views.components.enter_tags;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.views.components.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>User entry of multiple tags</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterTagsView extends AbstractComponentView<EnterTagsModel> {

  // View components
  private JList tagsList;
  private JButton addTagButton;
  private JTextField tagText;
  private DefaultListModel<String> tagsListModel = new DefaultListModel<>();

  /**
   * @param model The model backing this view
   */
  public EnterTagsView(EnterTagsModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    EnterTagsModel model = getModel().get();

    panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[][]", // Columns
      "[][]" // Rows
    ));

    // Use a DefaultListModel to give more flexibility over add/remove operations
    for (String item : model.getValue()) {
      tagsListModel.addElement(item);
    }
    tagsList = ListBoxes.newTagPillList(tagsListModel);
    tagsList.addMouseListener(getMouseListener());
    tagsList.addKeyListener(getKeyListener());

    // Require scroll pane to prevent excessively long tags from overflowing the wizard
    JScrollPane listScrollPane = new JScrollPane(tagsList);

    // Require the scroll pane + viewport to not be opaque
    listScrollPane.setOpaque(false);
    listScrollPane.getViewport().setOpaque(false);

    // Ensure the scroll pane + viewport have no border
    listScrollPane.setBorder(null);
    listScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

    // No scrolling when the space runs outs
    listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    addTagButton = Buttons.newAddButton(null);

    AddTagListener addTagListener = new AddTagListener();
    addTagButton.addActionListener(addTagListener);
    addTagButton.setEnabled(false);

    tagText = TextBoxes.newTextField(20);
    tagText.addActionListener(addTagListener);
    tagText.getDocument().addDocumentListener(addTagListener);

    // Add to the panel
    panel.add(tagText, "grow,push");
    panel.add(addTagButton, "shrink,wrap");
    panel.add(listScrollPane, "grow,span 2,push,height 100");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    tagsList.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {

    // Handled by events
  }

  private MouseListener getMouseListener() {

    return new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent mouseEvent) {

        // Determine the currently selected index
        int index = tagsList.getSelectedIndex();
        if (index == -1) {

          // Nothing selected
          return;
        }

        // Test if the mouse click was on the selected tag
        if (tagsList.getCellBounds(tagsList.getSelectedIndex(), tagsList.getSelectedIndex()).contains(mouseEvent.getPoint())) {

          removeTag(index);

        }

      }

    };
  }

  /**
   * <p>A key listener to only perform the tag removal on an action</p>
   * @return The key listener
   */
  private KeyListener getKeyListener() {

    return new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        // People have a lot of ways of making a choice to delete with the keyboard
        if (e.getKeyCode()== KeyEvent.VK_ENTER
          || e.getKeyCode()==KeyEvent.VK_SPACE
          || e.getKeyCode()==KeyEvent.VK_DELETE
          || e.getKeyCode()==KeyEvent.VK_BACK_SPACE
          ) {

          // Determine the currently selected index
          int index = tagsList.getSelectedIndex();
          if (index == -1) {

            // Nothing selected
            return;
          }

          removeTag(index);

        }
      }

    };
  }

  /**
   * <p>Inner class to handle adding a new tag</p>
   */
  private void removeTag(int index) {

    String tag = tagsListModel.getElementAt(index);

    // Remove it from the model
    getModel().get().getTags().remove(tag);

    // User wants to remove this entry
    tagsListModel.remove(index);

    int size = tagsListModel.getSize();

    if (size != 0) {

      // Ensure selection focus changes
      if (index == tagsListModel.getSize()) {
        // Removed item in last position
        index--;
      }

      tagsList.setSelectedIndex(index);
      tagsList.ensureIndexIsVisible(index);

    }
  }

  class AddTagListener implements ActionListener, DocumentListener {

    @Override
    public void actionPerformed(ActionEvent e) {

      String tag = tagText.getText();

      // Check if this has already been taken
      if (tag.equals("") || tagsListModel.contains(tag) || tagsListModel.size() >= 8) {
        Sounds.playBeep();
        tagText.requestFocusInWindow();
        tagText.selectAll();
        return;
      }

      // Must be unique to be here

      // Add it to the model
      getModel().get().getTags().add(tag);

      // Add to the end
      tagsListModel.addElement(tag);

      // Reset the text field
      tagText.requestFocusInWindow();
      tagText.setText("");

      // Select the new item and make it visible
      tagsList.setSelectedIndex(tagsListModel.size());
      tagsList.ensureIndexIsVisible(tagsListModel.size());

    }

    @Override
    public void insertUpdate(DocumentEvent e) {

      // Cannot be empty
      addTagButton.setEnabled(true);

    }

    @Override
    public void removeUpdate(DocumentEvent e) {

      // Might be empty
      addTagButton.setEnabled(!isEmptyTextField(e));
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

      // Might be empty
      addTagButton.setEnabled(!isEmptyTextField(e));

    }

    /**
     * @param e The document event
     *
     * @return True if the document is empty
     */
    private boolean isEmptyTextField(DocumentEvent e) {

      return e.getDocument().getLength() <= 0;

    }
  }

}
