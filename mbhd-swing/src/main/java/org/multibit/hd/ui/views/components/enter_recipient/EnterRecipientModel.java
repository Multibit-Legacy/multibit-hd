package org.multibit.hd.ui.views.components.enter_recipient;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Storage of state for the recipient information</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EnterRecipientModel implements Model<Recipient> {

  private Optional<Recipient> recipient = Optional.absent();
  private final String panelName;

  /**
   * @param panelName The panel name to identify the "verification status" and "next" buttons
   */
  public EnterRecipientModel(String panelName) {
    this.panelName = panelName;
  }

  /**
   * @return The panel name that this component is associated with
   */
  public String getPanelName() {
    return panelName;
  }

  @Override
  public Recipient getValue() {

    throw new IllegalStateException("This method should be called. Use getRecipient() instead.");

  }

  @Override
  public void setValue(Recipient value) {

    this.recipient = Optional.fromNullable(value);

    // Fire a component model updated event
    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
  }

  /**
   * @return The recipient
   */
  public Optional<Recipient> getRecipient() {
    return recipient;
  }
}
