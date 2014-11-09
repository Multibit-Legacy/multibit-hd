package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.fsm.HardwareWalletContext;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.spongycastle.util.encoders.Hex;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Use Trezor progress report</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class UseTrezorReportPanelView extends AbstractWizardPanelView<UseTrezorWizardModel, Boolean> {

  private JLabel trezorWalletStatus;

  private ListeningExecutorService listeningExecutorService;

  boolean decryptHasBeenRequested = false;

  /**
   * @param wizard The wizard managing the states
   */
  public UseTrezorReportPanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.USE_TREZOR_REPORT_TITLE, AwesomeIcon.MAGIC);

  }

  @Override
  public void newPanelModel() {

    // Nothing to bind

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    HardwareWalletService.hardwareWalletEventBus.register(this);

    contentPanel.setLayout(
            new MigLayout(
                    Panels.migXYLayout(),
                    "[][][]", // Column constraints
                    "[]10[]10[]" // Row constraints
            ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    trezorWalletStatus = Labels.newCoreStatusLabel(Optional.of(CoreMessageKey.CHANGE_PASSWORD_WORKING), null, Optional.<Boolean>absent());

    contentPanel.add(trezorWalletStatus, "wrap");

    listeningExecutorService = SafeExecutors.newSingleThreadExecutor("decrypt-trezor-wallet");

    decryptHasBeenRequested = false;

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addCancelFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    // Run the decryption on a different thread
    listeningExecutorService.submit(
            new Runnable() {
              @Override
              public void run() {

                if (!decryptHasBeenRequested) {
                  decryptTrezorWallet();
                }

                decryptHasBeenRequested = true;

                // Enable the Finish button
                ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

              }
            });

  }

  public boolean beforeHide(boolean isExitCancel) {

    // Update everything
    // TODO- make more specific to only fire when new wallet has been switched to
    CoreEvents.fireConfigurationChangedEvent();
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * Attempt to decrypt the Trezor wallet and feedback to user
   */
  private void decryptTrezorWallet() {

    UseTrezorWizardModel model = getWizardModel();

    // Request the root node from the Trezor
    // This will fire a HardwareWalletEvent of type PUBLIC_KEY, picked up by the onHardwareWalletEvent below
    model.requestRootNode();
  }

  /**
   * <p>Handle the PUBLIC_KEY hardware wallet event </p>
   *
   * @param event The hardware wallet event indicating a state change
   */
  @Subscribe
  public void onHardwareWalletEvent(HardwareWalletEvent event) {

    log.debug("Received hardware event: '{}'.{}", event.getEventType().name(), event.getMessage());

    switch (event.getEventType()) {
      case SHOW_DEVICE_FAILED:
      case SHOW_DEVICE_DETACHED:
      case SHOW_DEVICE_READY:
      case ADDRESS:
      case SHOW_PIN_ENTRY:
      case SHOW_OPERATION_SUCCEEDED:
      case SHOW_OPERATION_FAILED:
      case PUBLIC_KEY:

        // Do nothing
        break;

      case DETERMINISTIC_HIERARCHY:

        Optional<HardwareWalletService> hardwareWalletServiceOptional = CoreServices.getOrCreateHardwareWalletService();
        if (hardwareWalletServiceOptional.isPresent()) {
          HardwareWalletService hardwareWalletService = hardwareWalletServiceOptional.get();
          if (hardwareWalletService.isWalletPresent()) {
            HardwareWalletContext hardwareWalletContext = hardwareWalletService.getContext();
            // Parent key should be M/44'/0'/0'
            final DeterministicKey parentKey = hardwareWalletContext.getDeterministicKey().get();
            log.info("Parent key path: {}", parentKey.getPathAsString());

            // Verify the deterministic hierarchy can derive child keys
            // In this case 0/0 from a parent of M/44'/0'/0'
            DeterministicHierarchy hierarchy = hardwareWalletContext.getDeterministicHierarchy().get();
            DeterministicKey childKey = hierarchy.deriveChild(
                    Lists.newArrayList(
                            ChildNumber.ZERO
                    ),
                    true,
                    true,
                    ChildNumber.ZERO
            );

            // Calculate the address
            ECKey seedKey = ECKey.fromPublicOnly(childKey.getPubKey());
            Address walletKeyAddress = new Address(MainNetParams.get(), seedKey.getPubKeyHash());

            log.info("Path {}/0/0 has address: '{}'", parentKey.getPathAsString(), walletKeyAddress.toString());

            // Get the label of the Trezor from the features to use as the wallet name
            Optional<Features> features = hardwareWalletContext.getFeatures();
            final String label;
            if (features.isPresent()) {
              label = features.get().getLabel();
            } else {
              label = "";
            }

            try {
              final UseTrezorWizardModel model = getWizardModel();
              if (!model.getEntropyOptional().isPresent()) {
                log.debug("No entropy - no wallet to load");
                // TODO Notify user
                return;
              }

              // The entropy is used as the password of the Trezor wallet (so the user does not need to remember it
              log.debug("Running decrypt of Trezor wallet with entropy of length {}", model.getEntropyOptional().get().length);

              // Locate the installation directory
              final File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

              // Must be OK to be here - run wallet creation off the hardware event thread
              SwingUtilities.invokeLater(
                      new Runnable() {
                        @Override
                        public void run() {

                          try {
                            WalletSummary walletSummary = WalletManager.INSTANCE.getOrCreateWalletSummary(applicationDataDirectory,
                                    parentKey, Dates.nowInSeconds(), Hex.toHexString(model.getEntropyOptional().get()),
                                    label, "");

                            log.debug("Wallet summary {}", walletSummary);

                            trezorWalletStatus.setText(Languages.safeText(MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS, true));
                            AccessibilityDecorator.apply(trezorWalletStatus, MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS);
                            AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, trezorWalletStatus, true, MultiBitUI.NORMAL_ICON_SIZE);

                          } catch (IOException ioe) {
                            ioe.printStackTrace();
                          }
                        }
                      });


            } catch (Exception e) {
              e.printStackTrace();
            }


          } else {
            log.debug("No wallet present");
          }
        } else {
          log.error("No hardware wallet service");
        }
    }
  }
}
