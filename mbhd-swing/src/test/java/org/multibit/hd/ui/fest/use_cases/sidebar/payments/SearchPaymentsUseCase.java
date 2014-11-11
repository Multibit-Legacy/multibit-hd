package org.multibit.hd.ui.fest.use_cases.sidebar.payments;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "payment" screen search</li>
 * </ul>
 * <p>Requires the "payment" screen to be showing</p>
 * <p>Requires standard transactions to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class SearchPaymentsUseCase extends AbstractFestUseCase {

  public SearchPaymentsUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.PAYMENTS.getKey())
      .rowCount();

    // Verify that payments are present
    assertThat(rowCount1).isGreaterThanOrEqualTo(5);

    // Verify searches
    verifySearch("Beer", 2);
    verifySearch("10", 1);
    verifySearch("13vdKy", 1);

  }

  /**
   * @param query            The query
   * @param expectedRowCount The expected row count
   */
  private void verifySearch(String query, int expectedRowCount) {

    window
      .textBox(MessageKey.SEARCH.getKey())
      .setText("")
      .enterText(query);

    // Click search
    window
      .button(MessageKey.SEARCH.getKey())
      .click();

    // Get an updated row count
    int rowCount = window
      .table(MessageKey.PAYMENTS.getKey())
      .rowCount();

    // Expect only a couple of requests for query
    assertThat(rowCount).isGreaterThanOrEqualTo(expectedRowCount);

  }

}
