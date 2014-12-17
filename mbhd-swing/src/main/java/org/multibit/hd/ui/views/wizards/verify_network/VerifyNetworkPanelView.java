package org.multibit.hd.ui.views.wizards.verify_network;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Verify network: Show report</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class VerifyNetworkPanelView extends AbstractWizardPanelView<VerifyNetworkWizardModel, VerifyNetworkPanelModel> {

  // Panel specific components
  private JLabel peerCountLabel;
  private JLabel peerCountStatusLabel;

  private JLabel blocksLeftLabel;
  private JLabel blocksLeftStatusLabel;

  /**
   * @param wizard The wizard managing the states
   */
  public VerifyNetworkPanelView(AbstractWizard<VerifyNetworkWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.VERIFY_NETWORK_TITLE, AwesomeIcon.SITEMAP);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    final VerifyNetworkPanelModel panelModel = new VerifyNetworkPanelModel(
            getPanelName());
    setPanelModel(panelModel);

    // Bind it to the wizard model

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
            Panels.migXYLayout(),
            "[]10[][]", // Column constraints
            "[]10[]10[]" // Row constraints
    ));

    contentPanel.add(Labels.newVerifyNetworkNote(), "span 3," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    int currentPeerCount = CoreServices.getOrCreateBitcoinNetworkService().getNumberOfConnectedPeers();
    peerCountLabel = Labels.newValueLabel(String.valueOf(currentPeerCount));
    peerCountStatusLabel = Labels.newPeerCount();
    decoratePeerCountStatusLabel(currentPeerCount);

    blocksLeftLabel = Labels.newValueLabel("");
    blocksLeftStatusLabel = Labels.newBlocksLeft();

    contentPanel.add(peerCountStatusLabel, "");
    contentPanel.add(peerCountLabel, "");
    contentPanel.add(Labels.newValueLabel(""), "growx,push,wrap");

    contentPanel.add(blocksLeftStatusLabel, "");
    contentPanel.add(blocksLeftLabel, "");
    contentPanel.add(Labels.newValueLabel(""), "growx,push,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<VerifyNetworkWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    // Use the latest values from the event service
    Optional<BitcoinNetworkChangedEvent> event = CoreServices.getApplicationEventService().getLatestBitcoinNetworkChangedEvent();

    if (event.isPresent()) {

      onBitcoinNetworkChangedEvent(event.get());

    }

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

  }

  @Subscribe
  public void onBitcoinNetworkChangedEvent(BitcoinNetworkChangedEvent event) {

    BitcoinNetworkSummary summary = event.getSummary();

    // Peer count
    Optional<Integer> peerCount = event.getSummary().getPeerCount();
    if (peerCount.isPresent()) {
      peerCountLabel.setText(String.valueOf(summary.getPeerCount().get()));
      decoratePeerCountStatusLabel(summary.getPeerCount().get());
    }

    // Blocks left
    int blocksLeft = event.getSummary().getBlocksLeft();
    if (blocksLeft == 0) {
      AwesomeDecorator.applyIcon(
              AwesomeIcon.CHECK,
              blocksLeftStatusLabel,
              true,
              MultiBitUI.NORMAL_ICON_SIZE
      );
      blocksLeftLabel.setText(String.valueOf(summary.getBlocksLeft()));
    } else if (blocksLeft < 0) {
      // No block info - do nothing (this could be a peer count event
//      AwesomeDecorator.applyIcon(
//              AwesomeIcon.TIMES,
//              blocksLeftStatusLabel,
//              true,
//              MultiBitUI.NORMAL_ICON_SIZE
//      );
//      blocksLeftLabel.setText("");
    } else {
      AwesomeDecorator.applyIcon(
              AwesomeIcon.EXCHANGE,
              blocksLeftStatusLabel,
              true,
              MultiBitUI.NORMAL_ICON_SIZE
      );
      blocksLeftLabel.setText(String.valueOf(summary.getBlocksLeft()));
    }
  }

  private void decoratePeerCountStatusLabel(int peerCount) {
    if (peerCount > 0) {
      AwesomeDecorator.applyIcon(
              AwesomeIcon.CHECK,
              peerCountStatusLabel,
              true,
              MultiBitUI.NORMAL_ICON_SIZE
      );
    } else {
      AwesomeDecorator.applyIcon(
              AwesomeIcon.TIMES,
              peerCountStatusLabel,
              true,
              MultiBitUI.NORMAL_ICON_SIZE
      );
    }
  }
}