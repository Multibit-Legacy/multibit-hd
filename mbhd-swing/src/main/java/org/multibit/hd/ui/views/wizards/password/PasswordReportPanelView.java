package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.crypto.AESUtils;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show password recovery progress report</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PasswordReportPanelView extends AbstractWizardPanelView<PasswordWizardModel, PasswordReportPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(PasswordReportPanelView.class);

  private JLabel passwordRecoveryStatus;

  /**
   * @param wizard The wizard managing the states
   */
  public PasswordReportPanelView(AbstractWizard<PasswordWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PASSWORD_REPORT_TITLE, AwesomeIcon.MAGIC);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    PasswordReportPanelModel panelModel = new PasswordReportPanelModel(
            getPanelName()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setReportPanelModel(panelModel);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
            Panels.migXYLayout(),
            "[][][]", // Column constraints
            "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    passwordRecoveryStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());

    contentPanel.add(passwordRecoveryStatus, "wrap");

    recoverPassword();

  }

  private void recoverPassword() {
    try {
      PasswordWizardModel model = getWizardModel();

      // Locate the installation directory
      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

      // Work out the seed, wallet id and wallet directory
      List<String> seedPhrase = model.getEnterSeedPhrasePanelModel().getEnterSeedPhraseModel().getSeedPhrase();
      SeedPhraseGenerator seedPhraseGenerator = new Bip39SeedPhraseGenerator();
      byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);
      WalletId walletId = new WalletId(seed);
      String walletRoot = applicationDataDirectory.getAbsolutePath() + File.separator + WalletManager.createWalletRoot(walletId);
      WalletSummary walletSummary = WalletManager.getOrCreateWalletSummary(new File(walletRoot), walletId);

      //String walletName = walletSummary.getName();

      // Read the encrypted wallet password and decrypt with an AES key derived from the seed
      KeyParameter backupAESkey = AESUtils.createAESKey(seed, AESUtils.SEED_DERIVED_AES_KEY_SALT_USED_IN_SCRYPT);
      byte[] decryptedWalletPasswordBytes = org.multibit.hd.brit.crypto.AESUtils.decrypt(walletSummary.getEncryptedPassword(), backupAESkey, AESUtils.SEED_DERIVED_AES_INITIALISATION_VECTOR);
      String decryptedWalletPassword = new String(decryptedWalletPasswordBytes, "UTF8");
      passwordRecoveryStatus.setText(Languages.safeText(MessageKey.PASSWORD_REPORT_MESSAGE, decryptedWalletPassword));
    } catch (Exception e) {
      log.error("Could not recover password", e);
      passwordRecoveryStatus.setText(Languages.safeText(MessageKey.PASSWORD_REPORT_MESSAGE_FAIL));
    }
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PasswordWizardModel> wizard) {

    PanelDecorator.addExitCancelPreviousFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

}
