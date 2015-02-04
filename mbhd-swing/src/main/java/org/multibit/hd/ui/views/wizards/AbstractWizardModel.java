package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.events.view.ViewEvents;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * <p>Abstract base class wizard models:</p>
 * <ul>
 * <li>Access to standard implementations of required methods</li>
 * </ul>
 *
 * @param <S> The state object type
 *
 * @since 0.0.1
 */
public abstract class AbstractWizardModel<S> implements DocumentListener {

  /**
   * The current state
   */
  protected S state;

  /**
   * Flags if the model data has changed from when the wizard was shown
   */
  private boolean isDirty;

  protected AbstractWizardModel(S state) {

    Preconditions.checkNotNull(state, "'state' must be present");

    this.state = state;

    // All wizard models can receive ViewEvents
    ViewEvents.subscribe(this);

  }

  /**
   * Called when the wizard is closing (hide)
   */
  public void unsubscribe() {
    ViewEvents.unsubscribe(this);
  }

  /**
   * @return The current wizard model state
   */
  public S getState() {
    return state;
  }

  /**
   * Handles state transition to next panel (if applicable)
   */
  public void showNext() {
    // Do nothing
  }

  /**
   * Handles state transition to previous panel (if applicable)
   */
  public void showPrevious() {
    // Do nothing
  }

  /**
   * @return The panel name (usually the wizard state name)
   */
  public String getPanelName() {

    // Enums are commonly used for state and toString is equivalent to name()
    return state.toString();

  }

  /**
   * @return True if the model has changed from when the wizard was shown
   */
  public boolean isDirty() {
    return isDirty;
  }

  /**
   * @param isDirty True if the model has changed from when the wizard was shown
   */
  public void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    setDirty(true);
  }


  @Override
  public void removeUpdate(DocumentEvent e) {
    setDirty(true);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    setDirty(true);
  }
}
