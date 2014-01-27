package org.multibit.hd.ui.views.components.enter_seed_phrase;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.wizards.WizardButton;

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
  private DateTime seedTimestamp = new DateTime(2014, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);

  // Start with the text displayed
  private boolean asClearText = true;

  private final String panelName;

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
      return Strings.repeat(String.valueOf(TextBoxes.getPasswordEchoChar()), TextBoxes.SEED_PHRASE_LENGTH);
    }

  }

  /**
   * @return The seed creation timestamp
   */
  public DateTime getSeedTimestamp() {
    return seedTimestamp;
  }

  /**
   * @param seedTimestamp The seed creation timestamp
   */
  public void setSeedTimestamp(DateTime seedTimestamp) {
    this.seedTimestamp = seedTimestamp;

    // Have a possible match so alert the panel model
    ViewEvents.fireWizardComponentModelChangedEvent(panelName, Optional.of(this));

  }

  /**
   * @return The computed seed phrase from the user
   */
  public List<String> getSeedPhrase() {
    return seedPhrase;
  }

  /**
   * @param text The text containing the seed phrase words
   */
  public void setSeedPhrase(String text) {

    seedPhrase = Lists.newArrayList(Splitter.on(" ").trimResults().split(text));

    if (SeedPhraseSize.isValid(seedPhrase.size())) {

      // Have a possible match so alert the panel model
      ViewEvents.fireWizardComponentModelChangedEvent(panelName, Optional.of(this));
    } else {

      // Ensure the "next" button is kept disabled
      ViewEvents.fireWizardButtonEnabledEvent(panelName, WizardButton.NEXT, false);
      ViewEvents.fireVerificationStatusChangedEvent(panelName, false);
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

  @Override
  public List<String> getValue() {
    return getSeedPhrase();
  }

  @Override
  public void setValue(List<String> value) {
    this.seedPhrase = value;
  }
}
