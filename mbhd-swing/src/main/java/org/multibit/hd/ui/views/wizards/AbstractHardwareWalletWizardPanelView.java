package org.multibit.hd.ui.views.wizards;

import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AbstractHardwareWalletComponentModel;
import org.multibit.hd.ui.views.components.AbstractHardwareWalletComponentView;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;

/**
 * <p>Abstract base class providing the following to wizard panel views:</p>
 * <ul>
 * <li>Standard methods common to hardware wallet wizard panel views</li>
 * </ul>
 * <p>A wizard panel view contains three sections: title, content and buttons. It relies on
 * its implementers to provide the panel containing the specific components for the
 * user interaction.</p>
 *
 * <p>A hardware wallet panel view often includes a display component</p>
 *
 * @param <M> the wizard model
 * @param <P> the wizard panel model
 *
 * @since 0.1.4
 */
public abstract class AbstractHardwareWalletWizardPanelView<M extends AbstractHardwareWalletWizardModel, P> extends AbstractWizardPanelView<M, P> {

  /**
   * The hardware display MaV
   */
  protected ModelAndView<? extends AbstractHardwareWalletComponentModel, ? extends AbstractHardwareWalletComponentView> hardwareDisplayMaV;

  /**
   * @param wizard         The wizard
   * @param panelName      The panel name to filter events from components
   * @param backgroundIcon The icon for the content section background
   * @param titleKey       The key for the title section text
   * @param values         The values to merge into the title section text
   */
  public AbstractHardwareWalletWizardPanelView(
    AbstractHardwareWalletWizard<M> wizard,
    String panelName,
    AwesomeIcon backgroundIcon,
    MessageKey titleKey,
    Object... values
  ) {
    super(wizard, panelName, backgroundIcon, titleKey, values);
  }

  /**
   * <p>Add the current hardware wallet display component to the content panel</p>
   *
   * @param contentPanel The content panel
   */
  protected void addCurrentHardwareDisplay(JPanel contentPanel) {

    // Identify the wallet mode
    switch (getWizardModel().getWalletMode()) {
      case TREZOR:
        hardwareDisplayMaV = Components.newTrezorDisplayMaV(getPanelName());
        break;
      case KEEP_KEY:
        hardwareDisplayMaV = Components.newKeepKeyDisplayMaV(getPanelName());
        break;
      default:
        // Unexpected value
        throw new IllegalStateException("Showing PIN for unknown wallet mode: " + getWizardModel().getWalletMode().name());
    }

    // Need some text here in case device fails just as we being the process
    contentPanel.add(hardwareDisplayMaV.getView().newComponentPanel(), "align center,wrap");

    // Ensure we register the components to avoid memory leaks
    registerComponents(hardwareDisplayMaV);

  }
}
