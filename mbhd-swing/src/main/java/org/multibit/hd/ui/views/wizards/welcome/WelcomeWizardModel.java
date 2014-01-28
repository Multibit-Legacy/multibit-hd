package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;

import java.util.List;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.*;

/**
 * <p>Model object to provide the following to welcome wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeWizardModel extends AbstractWizardModel<WelcomeWizardState> {

  /**
   * The "select wallet" radio button choice (as a state)
   */
  private String localeCode = Languages.currentLocale().getLanguage();

  /**
   * The "select wallet" radio button choice (as a state)
   */
  private WelcomeWizardState selectWalletChoice = WelcomeWizardState.CREATE_WALLET_SEED_PHRASE;

  /**
   * The "select restore" radio button choice (as a state)
   */
  private WelcomeWizardState selectRestoreMethod = WelcomeWizardState.RESTORE_WALLET_BACKUP;

  /**
   * The seed phrase generator
   */
  private final SeedPhraseGenerator seedPhraseGenerator;

  /**
   * The confirm password model
   */
  private ConfirmPasswordModel confirmPasswordModel;
  private EnterPasswordModel restoreWalletPasswordModel;
  private SelectFileModel backupLocationSelectFileModel;
  private SelectFileModel restoreLocationSelectFileModel;
  private EnterSeedPhraseModel createWalletEnterSeedPhraseModel;
  private EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel;
  private List<String> createWalletSeedPhrase;
  private List<String> restoreWalletSeedPhrase;
  private String actualSeedTimestamp;

  /**
   * @param state The state object
   */
  public WelcomeWizardModel(WelcomeWizardState state) {
    super(state);

    this.seedPhraseGenerator = CoreServices.newSeedPhraseGenerator();

  }

  @Override
  public void showNext() {

    switch (state) {
      case WELCOME_SELECT_LANGUAGE:
        state = WELCOME_SELECT_WALLET;
        break;
      case WELCOME_SELECT_WALLET:
        state = selectWalletChoice;
        break;
      case CREATE_WALLET_SELECT_BACKUP_LOCATION:
        state = CREATE_WALLET_SEED_PHRASE;
        break;
      case CREATE_WALLET_SEED_PHRASE:
        state = CREATE_WALLET_CONFIRM_SEED_PHRASE;
        // Fail safe to ensure that the generator hasn't gone screwy
        Preconditions.checkState(SeedPhraseSize.isValid(getCreateWalletSeedPhrase().size()), "'actualSeedPhrase' is not a valid length");
        break;
      case CREATE_WALLET_CONFIRM_SEED_PHRASE:
        state = CREATE_WALLET_CREATE_PASSWORD;
        break;
      case CREATE_WALLET_CREATE_PASSWORD:
        state = CREATE_WALLET_REPORT;
        break;
      case CREATE_WALLET_REPORT:
        throw new IllegalStateException("'Next' is not permitted here");
      case RESTORE_WALLET_SELECT_METHOD:
        state = selectRestoreMethod;
        break;
      case RESTORE_WALLET_SEED_PHRASE:
        state = RESTORE_WALLET_REPORT;
        break;
      case RESTORE_WALLET_BACKUP:
        state = RESTORE_WALLET_REPORT;
        break;
      case RESTORE_WALLET_REPORT:
        throw new IllegalStateException("'Next' is not permitted here");
      case SELECT_WALLET_HARDWARE:
        // TODO Requires implementation
        state = selectWalletChoice;
        break;
      case SELECT_WALLET_SWITCH:
        // TODO Requires implementation
        state = selectWalletChoice;
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }

  }

  @Override
  public void showPrevious() {

    switch (state) {
      case WELCOME_SELECT_LANGUAGE:
        state = WELCOME_SELECT_LANGUAGE;
        break;
      case WELCOME_SELECT_WALLET:
        state = WELCOME_SELECT_LANGUAGE;
        break;
      case CREATE_WALLET_SELECT_BACKUP_LOCATION:
        state = WELCOME_SELECT_WALLET;
        break;
      case CREATE_WALLET_SEED_PHRASE:
        state = CREATE_WALLET_SELECT_BACKUP_LOCATION;
        break;
      case CREATE_WALLET_REPORT:
        throw new IllegalStateException("'Previous' is not permitted here");
      case RESTORE_WALLET_SELECT_METHOD:
        state = WELCOME_SELECT_WALLET;
        break;
      case RESTORE_WALLET_BACKUP:
        state = RESTORE_WALLET_SELECT_METHOD;
        break;
      case RESTORE_WALLET_SEED_PHRASE:
        state = RESTORE_WALLET_SELECT_METHOD;
        break;
      case RESTORE_WALLET_REPORT:
        state = selectRestoreMethod;
      case SELECT_WALLET_HARDWARE:
        state = WELCOME_SELECT_WALLET;
        break;
      default:
        throw new IllegalStateException("Unknown state: " + state.name());
    }
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  @Subscribe
  public void onPasswordStatusChangedEvent(VerificationStatusChangedEvent event) {

    ViewEvents.fireWizardButtonEnabledEvent(CREATE_WALLET_CONFIRM_SEED_PHRASE.name(), WizardButton.NEXT, event.isOK());

  }

  /**
   * @return The user selection for the locale
   */
  public String getLocaleCode() {
    return localeCode;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param localeCode The locale code
   */
  public void setLocaleCode(String localeCode) {
    this.localeCode = localeCode;
  }

  /**
   * @return The "select wallet" radio button choice
   */
  public WelcomeWizardState getSelectWalletChoice() {
    return selectWalletChoice;
  }

  /**
   * @return The "create wallet" seed phrase (generated)
   */
  public List<String> getCreateWalletSeedPhrase() {
    return createWalletSeedPhrase;
  }

  /**
   * @return The "restore wallet" seed phrase (user entered)
   */
  public List<String> getRestoreWalletSeedPhrase() {
    return restoreWalletSeedPhrase;
  }

  /**
   * @return The actual generated seed timestamp (e.g. "1850/2")
   */
  public String getActualSeedTimestamp() {
    return actualSeedTimestamp;
  }

  /**
   * @return The user entered password for the creation process
   */
  public String getCreateWalletUserPassword() {
    return confirmPasswordModel.getValue();
  }

  /**
   * @return The user entered password for the restore process
   */
  public String getRestoreWalletUserPassword() {
    return restoreWalletPasswordModel.getValue();
  }

  /**
   * @return The user entered backup location
   */
  public String getBackupLocation() {
    return backupLocationSelectFileModel.getValue();
  }

  /**
   * @return The user entered restore location
   */
  public String getRestoreLocation() {
    return restoreLocationSelectFileModel.getValue();
  }

  /**
   * @return The seed phrase generator
   */
  public SeedPhraseGenerator getSeedPhraseGenerator() {
    return seedPhraseGenerator;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param confirmPasswordModel The "confirm password" model
   */
  void setConfirmPasswordModel(ConfirmPasswordModel confirmPasswordModel) {
    this.confirmPasswordModel = confirmPasswordModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param backupLocationSelectFileModel The "backup location" select file model
   */
  void setBackupLocationSelectFileModel(SelectFileModel backupLocationSelectFileModel) {
    this.backupLocationSelectFileModel = backupLocationSelectFileModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param restoreLocationSelectFileModel The "restore location" select file model
   */
  void setRestoreLocationSelectFileModel(SelectFileModel restoreLocationSelectFileModel) {
    this.restoreLocationSelectFileModel = restoreLocationSelectFileModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param selectWalletChoice The wallet selection from the radio buttons
   */
  void setSelectWalletChoice(WelcomeWizardState selectWalletChoice) {
    this.selectWalletChoice = selectWalletChoice;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param selectRestoreMethod The restore method selection from the radio buttons
   */
  void setSelectRestoreMethod(WelcomeWizardState selectRestoreMethod) {
    this.selectRestoreMethod = selectRestoreMethod;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param createWalletEnterSeedPhraseModel The "create wallet enter seed phrase" model
   */
  void setCreateWalletEnterSeedPhraseModel(EnterSeedPhraseModel createWalletEnterSeedPhraseModel) {
    this.createWalletEnterSeedPhraseModel = createWalletEnterSeedPhraseModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param restoreWalletEnterSeedPhraseModel The "restore wallet enter seed phrase" model
   */
  void setRestoreWalletEnterSeedPhraseModel(EnterSeedPhraseModel restoreWalletEnterSeedPhraseModel) {
    this.restoreWalletEnterSeedPhraseModel = restoreWalletEnterSeedPhraseModel;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param createWalletSeedPhrase The actual seed phrase generated by the panel model
   */
  void setCreateWalletSeedPhrase(List<String> createWalletSeedPhrase) {
    this.createWalletSeedPhrase = createWalletSeedPhrase;
  }

  /**
   * <p>Reduced visibility for panel models</p>
   *
   * @param actualSeedTimestamp The actual seed timestamp generated by the panel model
   */
  void setActualSeedTimestamp(String actualSeedTimestamp) {
    this.actualSeedTimestamp = actualSeedTimestamp;
  }

}
