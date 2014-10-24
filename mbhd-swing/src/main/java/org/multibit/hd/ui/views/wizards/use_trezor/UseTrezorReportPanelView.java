package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
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

import javax.swing.*;
import java.io.File;

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

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[][][]", // Column constraints
        "[]10[]10[]" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    trezorWalletStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());

    contentPanel.add(trezorWalletStatus, "wrap");

    listeningExecutorService = SafeExecutors.newSingleThreadExecutor("decrypt-trezor-wallet");

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

          decryptTrezorWallet();

          // Enable the Finish button
          ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

        }
      });

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

    if (!model.getEntropyOptional().isPresent()) {
      log.debug("No entropy - no wallet to load");
      // TODO Notify user
      return;
    }

    log.debug("Running decrypt of Trezor wallet with entropy of length {}", model.getEntropyOptional().get().length);

    // Locate the installation directory
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Using the entropy as the seed, work out the wallet id and wallet directory

    WalletId walletId = new WalletId(model.getEntropyOptional().get());

    String walletRoot = applicationDataDirectory.getAbsolutePath() + File.separator + WalletManager.createWalletRoot(walletId);
    File walletDirectory = new File(walletRoot);

    WalletSummary walletSummary = WalletManager.getOrCreateWalletSummary(walletDirectory, walletId);

    log.debug("Wallet summary {}", walletSummary);
//
//    // Check for present but empty wallet directory
//    if (walletSummary.getEncryptedPassword() == null) {
//      SwingUtilities.invokeLater(
//        new Runnable() {
//          @Override
//          public void run() {
//            // Failed
//            trezorWalletStatus.setText(Languages.safeText(MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL));
//            AccessibilityDecorator.apply(trezorWalletStatus, MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL);
//            AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, trezorWalletStatus, true, MultiBitUI.NORMAL_ICON_SIZE);
//          }
//        });
//      return;
//    }
//
//    // Read the encrypted wallet credentials and decrypt with an AES key derived from the seed
//    KeyParameter backupAESKey;
//    try {
//      backupAESKey = AESUtils.createAESKey(seed, WalletManager.SCRYPT_SALT);
//    } catch (final NoSuchAlgorithmException e) {
//      SwingUtilities.invokeLater(
//        new Runnable() {
//          @Override
//          public void run() {
//            // Failed
//            log.error(e.getMessage(), e);
//            trezorWalletStatus.setText(Languages.safeText(MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL));
//            AccessibilityDecorator.apply(trezorWalletStatus, MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL);
//            AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, trezorWalletStatus, true, MultiBitUI.NORMAL_ICON_SIZE);
//
//          }
//        });
//      return;
//    }
//
//    byte[] decryptedPaddedWalletPasswordBytes = org.multibit.hd.brit.crypto.AESUtils.decrypt(
//      // Get the padded credentials out of the wallet summary. This is put in when a wallet is created.
//      walletSummary.getEncryptedPassword(),
//      backupAESKey,
//      WalletManager.AES_INITIALISATION_VECTOR
//    );
//
//    try {
//      final byte[] decryptedWalletPasswordBytes = WalletManager.unpadPasswordBytes(decryptedPaddedWalletPasswordBytes);
//
//      // Check the result
//      if (decryptedWalletPasswordBytes == null || decryptedWalletPasswordBytes.length == 0) {
//        SwingUtilities.invokeLater(
//          new Runnable() {
//            @Override
//            public void run() {
//              // Failed
//              trezorWalletStatus.setText(Languages.safeText(MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL));
//              AccessibilityDecorator.apply(trezorWalletStatus, MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL);
//              AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, trezorWalletStatus, true, MultiBitUI.NORMAL_ICON_SIZE);
//            }
//          });
//        return;
//      }

      // Must be OK to be here
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            trezorWalletStatus.setText(Languages.safeText(MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS, true));
            AccessibilityDecorator.apply(trezorWalletStatus, MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS);
            AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, trezorWalletStatus, true, MultiBitUI.NORMAL_ICON_SIZE);
          }
        });

//    } catch (IllegalStateException ise) {
//      // Probably the unpad failed
//      SwingUtilities.invokeLater(
//        new Runnable() {
//          @Override
//          public void run() {
//            trezorWalletStatus.setText(Languages.safeText(MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL));
//            AccessibilityDecorator.apply(trezorWalletStatus, MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_FAIL);
//            AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, trezorWalletStatus, true, MultiBitUI.NORMAL_ICON_SIZE);
//          }
//        });
//    }
  }

}
