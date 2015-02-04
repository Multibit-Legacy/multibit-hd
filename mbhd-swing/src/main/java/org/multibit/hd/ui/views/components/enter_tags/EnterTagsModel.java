package org.multibit.hd.ui.views.components.enter_tags;

import com.google.common.collect.Lists;
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

  private final List<String> originalTags;
  private final List<String> newTags;

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public EnterTagsModel(String panelName, List<String> originalTags) {
    this.panelName = panelName;
    this.originalTags = Lists.newArrayList(originalTags);
    this.newTags = Lists.newArrayList(originalTags);
  }

  @Override
  public List<String> getValue() {
    return getNewTags();
  }

  @Override
  public void setValue(List<String> value) {

    newTags.clear();
    newTags.addAll(value);

  }

  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The original tags
   */
  public List<String> getOriginalTags() {

    return originalTags;

  }

  /**
   *
   * @return The modified tags
   */
  public List<String> getNewTags() {
    return newTags;
  }
}
