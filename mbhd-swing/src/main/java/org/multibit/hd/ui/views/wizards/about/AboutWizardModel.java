package org.multibit.hd.ui.views.wizards.about;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "exit" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class AboutWizardModel extends AbstractWizardModel<AboutState> {

  private static final Logger log = LoggerFactory.getLogger(AboutWizardModel.class);

  /**
   * @param state The state object
   */
  public AboutWizardModel(AboutState state) {
    super(state);
  }
}
