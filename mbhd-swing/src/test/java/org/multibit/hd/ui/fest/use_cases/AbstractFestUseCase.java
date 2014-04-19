package org.multibit.hd.ui.fest.use_cases;

import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.fixture.FrameFixture;

import java.util.Map;

/**
 * <p>Abstract base class to provide the following to FEST use case testing:</p>
 * <ul>
 * <li>Access to common methods</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractFestUseCase {

  protected final FrameFixture window;

  public AbstractFestUseCase(FrameFixture window) {
    this.window = window;
  }

  /**
   * Execute the use case
   *
   * @param parameters Any parameters that are useful to a particular use case (e.g. data carried between panels)
   */
  public abstract void execute(Map<String, Object> parameters);

  /**
   * @param name The label name
   *
   * @return The label if it is not showing
   */
  public JLabelMatcher newNotShowingJLabelFixture(String name) {

    return JLabelMatcher.withName(name);

  }

  /**
   * @param name The label name
   *
   * @return The button if it is not showing
   */
  public JButtonMatcher newNotShowingJButtonFixture(String name) {

    return JButtonMatcher.withName(name);

  }

}
