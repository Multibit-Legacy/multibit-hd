package org.multibit.hd.ui.views.components.enter_tags;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
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

  private static final Logger log = LoggerFactory.getLogger(EnterTagsView.class);

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
      Panels.migXLayout(),
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

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(listScrollPane, true);

    addTagButton = Buttons.newAddButton(null);
    addTagButton.setName(getModel().get().getPanelName() + "." + MessageKey.ADD.getKey());

    AddTagListener addTagListener = new AddTagListener();
    addTagButton.addActionListener(addTagListener);
    addTagButton.setEnabled(false);

    tagText = TextBoxes.newEnterTag();
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

        // Test if the mouse click was on the selected tag for smooth UX
        Rectangle cellBounds = tagsList.getCellBounds(tagsList.getSelectedIndex(), tagsList.getSelectedIndex());
        if (cellBounds.contains(mouseEvent.getPoint())) {

          // User is not randomly clicking around
          removeTag(index);

          // Need to select index and scroll to maintain smooth UX
          tagsList.setSelectedIndex(index);
          tagsList.ensureIndexIsVisible(index);

        } else {
          log.debug("Mouse click was not on selected tag. Selected index {}, cell bounds: {}, mouse point: {}",
            tagsList.getSelectedIndex(),
            cellBounds,
            mouseEvent.getPoint()
          );

        }
      }

    };
  }

  /**
   * <p>A key listener to only perform the tag removal on an action</p>
   *
   * @return The key listener
   */
  private KeyListener getKeyListener() {

    return new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        // People have a lot of ways of making a choice to delete with the keyboard
        if (e.getKeyCode() == KeyEvent.VK_ENTER
          || e.getKeyCode() == KeyEvent.VK_SPACE
          || e.getKeyCode() == KeyEvent.VK_DELETE
          || e.getKeyCode() == KeyEvent.VK_BACK_SPACE
          ) {

          // Determine the currently selected index
          int index = tagsList.getSelectedIndex();
          if (index == -1) {

            // Nothing selected
            return;
          }

          removeTag(index);

        } else {
          log.debug("Key code was not a recognised click");
        }
      }

    };
  }

  /**
   * <p>Inner class to handle adding a new tag</p>
   */
  private void removeTag(int index) {

    String tag = tagsListModel.getElementAt(index);

    log.trace("Removing '{}' at {}", tag, index);

    // Remove it from the model
    getModel().get().getNewTags().remove(tag);

    // User wants to remove this entry
    tagsListModel.remove(index);

    int sizeAfterRemoval = tagsListModel.getSize();

    // Ensure selection focus changes to follow new size for smooth UX
    if (sizeAfterRemoval > 0) {

      if (index == sizeAfterRemoval) {
        // Removed item at end of list
        index--;
      }

      tagsList.setSelectedIndex(index);
      tagsList.ensureIndexIsVisible(index);

      log.trace("Set focus to tag index: {} with count: {}", index, sizeAfterRemoval);

    }
  }

  class AddTagListener implements ActionListener, DocumentListener {

    @Override
    public void actionPerformed(ActionEvent e) {

      // User wants to add a tag but may have relied on the keyboard method
      if (!addTagButton.isEnabled()) {
        // Ignore
        return;
      }

      String tag = tagText.getText();

      log.trace("Adding tag '{}'", tag);

      // Add it to the model
      getModel().get().getNewTags().add(tag);

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

      // Might be white space

      // Check tag list size and avoid duplications
      if (isTagListSizeExceeded() || isPresent(e) || isEmptyTextField(e)) {
        addTagButton.setEnabled(false);
      } else {
        addTagButton.setEnabled(true);
      }

    }

    @Override
    public void removeUpdate(DocumentEvent e) {

      // Might be empty

      // Check tag list size and avoid empty or duplicated
      if (isTagListSizeExceeded() || isPresent(e) || isEmptyTextField(e)) {
        addTagButton.setEnabled(false);
      } else {
        addTagButton.setEnabled(true);
      }

    }

    @Override
    public void changedUpdate(DocumentEvent e) {

      // Might be empty

      // Check tag list size and avoid empty or duplicated
      if (isTagListSizeExceeded() || isPresent(e) || isEmptyTextField(e)) {
        addTagButton.setEnabled(false);
      } else {
        addTagButton.setEnabled(true);
      }

    }

    /**
     * @param e The document event
     *
     * @return The document contents
     */
    private String getDocumentText(DocumentEvent e) {

      try {
        return e.getDocument().getText(0, e.getDocument().getLength());
      } catch (BadLocationException e1) {
        ExceptionHandler.handleThrowable(e1);
        return "";
      }

    }

    /**
     * @param e The document event
     *
     * @return True if the document is empty
     */
    private boolean isEmptyTextField(DocumentEvent e) {

      return getDocumentText(e).trim().length() <= 0;

    }

    /**
     * @param e The document event
     *
     * @return True if the tags list model contains the document
     */
    private boolean isPresent(DocumentEvent e) {

      return tagsListModel.contains(getDocumentText(e));

    }

    /**
     * @return True if the tags list model has reached its maximum size
     */
    private boolean isTagListSizeExceeded() {

      return tagsListModel.size() >= 8;

    }
  }

}
