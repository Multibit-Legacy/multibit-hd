package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.WizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class WelcomeWizardModel implements WizardModel {

  private static final Logger log = LoggerFactory.getLogger(WelcomeWizardModel.class);

  /**
   * The current state
   */
  private WelcomeWizardState state = WELCOME;

  /**
   * The "select wallet" radio button choice (as a state)
   */
  private String localeCode = Languages.currentLocale().getLanguage();

  /**
   * The "select wallet" radio button choice (as a state)
   */
  private WelcomeWizardState selectWalletChoice = WelcomeWizardState.CREATE_WALLET_SEED_PHRASE;

  /**
   * The seed phrase for the wallet as generated
   */
  private List<String> actualSeedPhrase = Lists.newArrayList();

  /**
   * The seed phrase for the wallet from the user
   */
  private List<String> userSeedPhrase = Lists.newArrayList();

  public WelcomeWizardModel(WelcomeWizardState state) {
    this.state = state;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P> void update(Optional<P> panelModel) {

    // No state transitions occur in this method

    // TODO Consider migrating state into dedicated objects

    switch (state) {
      case WELCOME:
        localeCode = (String) panelModel.get();
        break;
      case SELECT_WALLET:
        selectWalletChoice = (WelcomeWizardState) panelModel.get();
        break;
      case RESTORE_WALLET:
        break;
      case HARDWARE_WALLET:
        break;
      case CREATE_WALLET_SEED_PHRASE:
        actualSeedPhrase = (List<String>) panelModel.get();
        // TODO remove this
        for (String word : actualSeedPhrase) {
          System.out.print(word + " ");
        }
        System.out.println();
        break;
      case CONFIRM_WALLET_SEED_PHRASE:
        userSeedPhrase = (List<String>) panelModel.get();
        if (userSeedPhrase.equals(actualSeedPhrase)) {
          ViewEvents.fireWizardEnableButton(CONFIRM_WALLET_SEED_PHRASE.name(), WizardButton.NEXT, true);
        }
        break;
    }

  }

  @Override
  public void next() {

    switch (state) {
      case WELCOME:
        state = SELECT_WALLET;
        break;
      case SELECT_WALLET:
        state = selectWalletChoice;
        break;
      case CREATE_WALLET_SEED_PHRASE:
        state = CONFIRM_WALLET_SEED_PHRASE;
        ViewEvents.fireWizardEnableButton(CONFIRM_WALLET_SEED_PHRASE.name(), WizardButton.NEXT, false);
        break;
      case RESTORE_WALLET:
        state = CONFIRM_WALLET_SEED_PHRASE;
        break;
      case HARDWARE_WALLET:
        state = CONFIRM_WALLET_SEED_PHRASE;
        break;
      case CONFIRM_WALLET_SEED_PHRASE:
        state = CREATE_WALLET_PASSWORD;
        break;
    }

  }

  @Override
  public void previous() {

    switch (state) {
      case WELCOME:
        state = WELCOME;
        break;
      case SELECT_WALLET:
        state = WELCOME;
        break;
      case CREATE_WALLET_SEED_PHRASE:
        state = SELECT_WALLET;
        break;
      case RESTORE_WALLET:
        state = SELECT_WALLET;
        break;
      case HARDWARE_WALLET:
        state = SELECT_WALLET;
        break;
    }
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The user selection for the locale
   */
  public String getLocaleCode() {
    return localeCode;
  }

  /**
   * @return The "select wallet" radio button choice
   */
  public WelcomeWizardState getSelectWalletChoice() {
    return selectWalletChoice;
  }

  /**
   * @return The actual generated seed phrase
   */
  public List<String> getActualSeedPhrase() {
    return actualSeedPhrase;
  }

  /**
   * @return The user entered seed phrase
   */
  public List<String> getUserSeedPhrase() {
    return userSeedPhrase;
  }
}
