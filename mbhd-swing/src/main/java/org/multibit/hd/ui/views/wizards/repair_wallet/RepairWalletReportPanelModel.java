package org.multibit.hd.ui.views.wizards.repair_wallet;

import com.google.common.base.Optional;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "repair wallet report" wizard:</p>
 * <ul>
 * <li>Storage of state for the "repair wallet report " panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RepairWalletReportPanelModel extends AbstractWizardPanelModel {

  // SSL certificates repaired ?

  // Absent = undecided, true = created ok, false = failure
  private Optional<Boolean> cacertsCreatedSuccessfully;

  private Optional<MessageKey> cacertsCreatedSummaryMessageKey;
  private Optional<MessageKey> cacertsCreatedDetailMessageKey;

  // Number of transactions discovered

  private int numberOfTransactions;

  /**
   * @param panelName The panel name
   */
  public RepairWalletReportPanelModel(
    String panelName
  ) {
    super(panelName);

    // Subscribe methods then transition the state to reflect progress of the send
    cacertsCreatedSuccessfully = Optional.absent();
    cacertsCreatedSummaryMessageKey = Optional.absent();
    cacertsCreatedDetailMessageKey = Optional.absent();

    numberOfTransactions = -1;

  }

  public Optional<Boolean> getCacertsCreatedSuccessfully() {
    return cacertsCreatedSuccessfully;
  }

  public void setCacertsCreatedSuccessfully(Optional<Boolean> cacertsCreatedSuccessfully) {
    this.cacertsCreatedSuccessfully = cacertsCreatedSuccessfully;
  }

  public Optional<MessageKey> getCacertsCreatedSummaryMessageKey() {
    return cacertsCreatedSummaryMessageKey;
  }

  public void setCacertsCreatedSummaryMessageKey(Optional<MessageKey> cacertsCreatedSummaryMessageKey) {
    this.cacertsCreatedSummaryMessageKey = cacertsCreatedSummaryMessageKey;
  }

  public Optional<MessageKey> getCacertsCreatedDetailMessageKey() {
    return cacertsCreatedDetailMessageKey;
  }

  public void setCacertsCreatedDetailMessageKey(Optional<MessageKey> cacertsCreatedDetailMessageKey) {
    this.cacertsCreatedDetailMessageKey = cacertsCreatedDetailMessageKey;
  }

  public int getNumberOfTransactions() {
    return numberOfTransactions;
  }

  public void setNumberOfTransactions(int numberOfTransactions) {
    this.numberOfTransactions = numberOfTransactions;
  }

}
