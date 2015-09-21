package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Empty wallet: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class EmptyWalletEnterDetailsPanelView extends AbstractWizardPanelView<EmptyWalletWizardModel, EmptyWalletEnterDetailsPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(EmptyWalletEnterDetailsPanelView.class);

  // Panel specific components
  private ModelAndView<EnterRecipientModel, EnterRecipientView> enterRecipientMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

  private JLabel isAddressMineStatusLabel;

  /**
   * True if this is a hard wallet so has no password to unlock it
   */
  private final boolean isTrezorHardWallet;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public EmptyWalletEnterDetailsPanelView(AbstractWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.FIRE, MessageKey.EMPTY_WALLET_TITLE);

    // Work out the wallet type (may not require a password entry)
    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    isTrezorHardWallet = currentWalletSummary.isPresent()
      && WalletType.TREZOR_HARD_WALLET.equals(currentWalletSummary.get().getWalletType());

  }

  @Override
  public void newPanelModel() {

    // Require a reference for the model
    enterRecipientMaV = Components.newEnterRecipientMaV(getPanelName());
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());

    // Configure the panel model
    final EmptyWalletEnterDetailsPanelModel panelModel = new EmptyWalletEnterDetailsPanelModel(
      getPanelName(),
      enterRecipientMaV.getModel(),
      enterPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterDetailsPanelModel(panelModel);

    // Register components
    registerComponents(enterRecipientMaV, enterPasswordMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]10[]" // Row constraints
      ));

    isAddressMineStatusLabel = Labels.newAddressIsMineStatusLabel(getPanelModel().get().isAddressMine());
    // Only show if the address shown is in the wallet
    isAddressMineStatusLabel.setVisible(getPanelModel().get().isAddressMine());

    contentPanel.add(enterRecipientMaV.getView().newComponentPanel(), "wrap");
    // Only add the password MaV if using a soft wallet
    if (!isTrezorHardWallet) {
      contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");
    }
    contentPanel.add(isAddressMineStatusLabel, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<EmptyWalletWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Next button starts off disabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);
  }

  @Override
  public void afterShow() {

    // Only show if the address shown is in the wallet
    isAddressMineStatusLabel.setVisible(getPanelModel().get().isAddressMine());
    enterRecipientMaV.getView().requestInitialFocus();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Only show if the address shown is in the wallet
    isAddressMineStatusLabel.setVisible(getPanelModel().get().isAddressMine());

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      isNextEnabled()
    );

  }

  @Subscribe
  public void onComponentChangedEvent(ComponentChangedEvent event) {
    if (getPanelName().equals(event.getPanelName())) {
      // Only show if the address shown is in the wallet
      isAddressMineStatusLabel.setVisible(getPanelModel().get().isAddressMine());

      ViewEvents.fireWizardButtonEnabledEvent(
           getPanelName(),
           WizardButton.NEXT,
           isNextEnabled()
         );
    }
  }

  /**
   * @return True if the "next" button should be enabled
   */
  private boolean isNextEnabled() {

    // Recipient must be present
    boolean recipientOK = getPanelModel().get()
      .getEnterRecipientModel()
      .getRecipient()
      .isPresent();

    // No password required for a hard wallet
    if (isTrezorHardWallet) {
      return recipientOK;
    }

    // Password only has to contain something to qualify
    boolean passwordOK = !Strings.isNullOrEmpty(
      getPanelModel().get()
        .getEnterPasswordModel()
        .getValue());

    return recipientOK && passwordOK;
  }

}