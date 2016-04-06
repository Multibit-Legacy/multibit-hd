package org.multibit.hd.ui.fest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.multibit.hd.ui.fest.test_cases.AbstractFestTest;
import org.multibit.hd.ui.fest.test_cases.StandardFestTest;

/**
 * <p>Test suite for Swing UI functional tests</p>
 *
 * <p>This defines the overall test sequence, starting with simple tests and
 * progressing to more complex ones</p>
 *
 * @since 0.0.1
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  StandardFestTest.class /*,
  TrezorFestTest.class,
  KeepKeyFestTest.class */
})
public class MultiBitHDFestTest extends AbstractFestTest {

  // Refer to the base class for common "arrangement" methods
  // and to the suite classes for the order of the tests

}
