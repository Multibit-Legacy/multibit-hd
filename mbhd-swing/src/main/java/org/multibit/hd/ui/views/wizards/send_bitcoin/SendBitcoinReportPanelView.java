package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.eventbus.Subscribe;
import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show send Bitcoin progress report</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinReportPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinReportPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(SendBitcoinReportPanelView.class);

  private JLabel transactionConstructionStatus;
  private JLabel transactionBroadcastStatus;
  private JLabel transactionConfirmationStatus;

  private static JLabel resultText1 = new JLabel();
  private static JLabel resultText2 = new JLabel();

  /**
   * @param wizard The wizard managing the states
   */
  public SendBitcoinReportPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SEND_PROGRESS_TITLE);

    PanelDecorator.addFinish(this, wizard);

    CoreServices.uiEventBus.register(this);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    SendBitcoinReportPanelModel panelModel = new SendBitcoinReportPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setReportPanelModel(panelModel);

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    transactionConstructionStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    transactionBroadcastStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    transactionConfirmationStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());

    panel.add(transactionConstructionStatus, "wrap");
    panel.add(transactionBroadcastStatus, "wrap");
    panel.add(transactionConfirmationStatus, "wrap");
    panel.add(resultText1, "wrap");
    panel.add(resultText2, "wrap");

    // When first displayed bitcoins are in the process of being sent
    resultText1.setText(Languages.safeText("sendBitcoinNowAction.sendingBitcoin"));
    resultText2.setText("");

    return panel;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Subscribe
  public void onBitcoinSentEvent(BitcoinSentEvent bitcoinSentEvent) {
    log.debug("Received the BitcoinSentEvent: " + bitcoinSentEvent.toString());
    // TODO the localisation needs standardising between MessageKey and just using the key string
    if (bitcoinSentEvent.isSendWasSuccessful()) {
      resultText1.setText(Languages.safeText("sendBitcoinNowAction.bitcoinSentOk"));
      resultText2.setText("");
    } else {
      resultText1.setText(Languages.safeText("sendBitcoinNowAction.bitcoinSendFailed"));
      resultText2.setText(Languages.safeText(bitcoinSentEvent.getSendFailureReasonKey(), (Object[])bitcoinSentEvent.getSendFailureReasonData()));
    }

  }

}
