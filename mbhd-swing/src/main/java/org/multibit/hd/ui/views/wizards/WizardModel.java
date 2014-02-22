package org.multibit.hd.ui.views.wizards;

/**
 * <p>Interface to provide the following to wizard model:</p>
 * <ul>
 * <li>Standard methods</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface WizardModel {

  /**
   * <p>Show the next view (usually in response to a "next" button click)</p>
   */
  void showNext();

  /**
   * <p>Show the previous view (usually in response to a "previous" button click)</p>
   */
  void showPrevious();

  /**
   * @return The name of the panel to show (decouples from a specific wizard's internal state enum)
   */
  String getPanelName();

}
