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
public interface WizardPanelModel {

  /**
   * @return The name of the panel to show (decouples from a specific wizard's internal state enum)
   */
  String getPanelName();

}
