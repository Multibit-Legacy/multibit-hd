package org.multibit.hd.ui.views.components.enter_seed_phrase;

import com.google.common.base.*;
import com.google.common.collect.Lists;
import org.multibit.hd.brit.seed_phrase.SeedPhraseSize;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show/hide the seed phrase (initially hidden)</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EnterSeedPhraseModel implements Model<List<String>> {

  private List<String> seedPhrase = Lists.newArrayList();

  /**
   * Initialise to earliest possible HD wallet seed to provide a default during restore operation
   */
  private String seedTimestamp = "1826/80";

  // Start with the text displayed
  private boolean asClearText = true;

  private final String panelName;

  /**
   * The wallet type that has to be restored
   */
  private WalletType restoreWalletType = WalletType.MBHD_SOFT_WALLET_BIP32;

  /**
   * @param panelName The panel name to identify the "verification status" and "next" buttons
   */
  public EnterSeedPhraseModel(String panelName) {
    this.panelName = panelName;
  }

  /**
   * @return The seed phrase in either clear or obscured text
   */
  public String displaySeedPhrase() {

    if (asClearText) {
      return Joiner.on(" ").join(seedPhrase);
    } else {
      return Strings.repeat(String.valueOf(TextBoxes.getPasswordEchoChar()), MultiBitUI.SEED_PHRASE_LENGTH);
    }

  }

  /**
   * @return The seed creation timestamp (e.g. "1850/2")
   */
  public String getSeedTimestamp() {
    return seedTimestamp;
  }

  /**
   * @param seedTimestamp The seed creation timestamp
   */
  public void setSeedTimestamp(String seedTimestamp) {

    this.seedTimestamp = seedTimestamp;

    // Have a possible match so alert the panel model
    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));

  }

  /**
   * @return The computed seed phrase from the user
   */
  public List<String> getSeedPhrase() {
    if (seedPhrase == null) {
      seedPhrase = Lists.newArrayList();
    }
    return seedPhrase;
  }

  /**
   * @param text The text containing the seed phrase words
   */
  public void setSeedPhrase(String text) {

    Preconditions.checkNotNull(text, "'text' must be present");

    seedPhrase = Lists.newArrayList(Splitter
        .on(" ")
        .omitEmptyStrings()
        .trimResults()
        .split(text)
    );

    final String componentName = seedPhrase.size() > 0 ? ".seedphrase" : ".timestamp";

    // Perform a basic verification of the seed phrase
    if (SeedPhraseSize.isValid(seedPhrase.size())) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          // Have a possible match so alert the panel model to do more detailed checking
          ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
        }
      });
    } else {

      // Definitely a fail so don't bother the panel model with it

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          // Ensure the "next" button is kept disabled and no "verified" message
          ViewEvents.fireWizardButtonEnabledEvent(panelName, WizardButton.NEXT, false);

          // Fire "seed phrase verification" event
          ViewEvents.fireVerificationStatusChangedEvent(panelName + componentName, false);
        }
      });

    }

  }

  /**
   * @param asClearText True if the seed phrase should be presented as clear text
   */
  public void setAsClearText(boolean asClearText) {
    this.asClearText = asClearText;
  }

  /**
   * @return The current state of the clear text display
   */
  public boolean asClearText() {
    return asClearText;
  }

  /**
   * @return The panel name that this component is associated with
   */
  public String getPanelName() {
    return panelName;
  }

  public boolean isRestoreAsTrezor() {
    return WalletType.TREZOR_HARD_WALLET == restoreWalletType;
  }

  public void setRestoreWalletType(WalletType restoreWalletType) {
    this.restoreWalletType = restoreWalletType;
  }

  public WalletType getRestoreWalletType(){
    return restoreWalletType;
  }

  @Override
  public List<String> getValue() {
    return getSeedPhrase();
  }

  @Override
  public void setValue(List<String> value) {
    this.seedPhrase = value;
  }
}
