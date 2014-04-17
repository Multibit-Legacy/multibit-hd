package org.multibit.hd.ui.fest.use_cases;

import org.fest.swing.fixture.FrameFixture;

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
   */
  public abstract void execute();
}
