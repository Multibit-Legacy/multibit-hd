package org.multibit.hd.ui.views.components.enter_tags;

import org.multibit.hd.ui.models.Model;

import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Store the tags</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterTagsModel implements Model<List<String>> {

  private final List<String> tags;

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public EnterTagsModel(String panelName, List<String> tags) {
    this.panelName = panelName;
    this.tags = tags;
  }

  @Override
  public List<String> getValue() {
    return tags;
  }

  @Override
  public void setValue(List<String> value) {

    tags.addAll(value);

  }

  public String getPanelName() {
    return panelName;
  }

  public String[] getTagArray() {

    // Avoid empty array creation
    if (tags.isEmpty()) {
      return new String[] {};
    }

    return tags.toArray(new String[tags.size()]);

  }
}
