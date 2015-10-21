package org.multibit.hd.ui.fest.requirements.standard;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.payments.SearchPaymentsUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.payments.ShowDetailPaymentsUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.sidebar.payments.ShowPaymentsScreenUseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "payments" screen to verify its wizards show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class PaymentsScreenRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Select the payments screen
    new ShowPaymentsScreenUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Verify the transaction details wizard works ok
    new ShowDetailPaymentsUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Verify the standard wallet contains some payments
    new SearchPaymentsUseCase(window).execute(parameters);
  }
}
