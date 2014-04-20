package org.multibit.hd.ui.fest.use_cases.payments;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "payments" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ShowPaymentsScreenUseCase extends AbstractFestUseCase {

  public ShowPaymentsScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(2);

    // Expect the Payments screen to show
    window
      .textBox(MessageKey.SEARCH.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.SEARCH.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.DETAILS.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.DELETE_PAYMENT_REQUEST.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.UNDO.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.EXPORT.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .table(MessageKey.PAYMENTS.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
