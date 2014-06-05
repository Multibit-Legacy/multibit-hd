package org.multibit.hd.ui.views.wizards.repair_wallet;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show report wallet progress report</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RepairWalletReportPanelView extends AbstractWizardPanelView<RepairWalletWizardModel, RepairWalletReportPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(RepairWalletReportPanelView.class);

  private JLabel cacertsInstalledStatus;

  private JLabel transactionCount;

  private TransactionSeenEvent lastTransactionSeenEvent;

  private boolean initialised = false;

  /**
   * @param wizard The wizard managing the states
   */
  public RepairWalletReportPanelView(AbstractWizard<RepairWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.REPAIR_WALLET_PROGRESS_TITLE, AwesomeIcon.MEDKIT);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    RepairWalletReportPanelModel panelModel = new RepairWalletReportPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    //getWizardModel().setReportPanelModel(panelModel);

    lastTransactionSeenEvent = null;

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "10[20!]10[20!]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    cacertsInstalledStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(cacertsInstalledStatus, MessageKey.TRANSACTION_CONSTRUCTION_STATUS_SUMMARY);

    transactionCount = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(transactionCount, MessageKey.TRANSACTION_CONFIRMATION_STATUS);

    contentPanel.add(cacertsInstalledStatus, "wrap");
    contentPanel.add(transactionCount, "wrap");

    initialised = true;
  }

  @Override
  protected void initialiseButtons(AbstractWizard<RepairWalletWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public boolean beforeShow() {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        cacertsInstalledStatus.setText(Languages.safeText(MessageKey.CACERTS_INSTALLED_STATUS));
        transactionCount.setText(Languages.safeText(MessageKey.TRANSACTION_COUNT));
      }
    });
    return true;
  }
  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();
        if (lastTransactionSeenEvent != null) {
          onTransactionSeenEvent(lastTransactionSeenEvent);
        }
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Subscribe
  public void onTransactionSeenEvent(TransactionSeenEvent transactionSeenEvent) {

    System.out.println("Seen transaction");

    lastTransactionSeenEvent = transactionSeenEvent;
    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

  }
}
