package org.multibit.hd.ui.views.screens;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.WalletMode;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;

/**
 * <p>Abstract base class wizard models:</p>
 * <ul>
 * <li>Access to standard implementations of required methods</li>
 * </ul>
 *
 * @since 0.0.1
 */
public abstract class AbstractScreenModel implements ScreenModel {

  /**
   * The detail view
   */
  protected final Screen detailView;

  private final WalletMode walletMode;

  protected AbstractScreenModel(Screen detailView) {

    Preconditions.checkNotNull(detailView, "'detailView' must be present");

    this.detailView = detailView;

    // Register for Core events
    CoreEvents.subscribe(this);
    ViewEvents.subscribe(this);

    // Establish the wallet mode
    walletMode = WalletMode.of(CoreServices.getCurrentHardwareWalletService());

  }

  @Override
  public Screen getScreen() {

    return detailView;

  }

  /**
   * @return The wallet mode of the current hardware wallet
   */
  public WalletMode getWalletMode() {
    return walletMode;
  }
}
