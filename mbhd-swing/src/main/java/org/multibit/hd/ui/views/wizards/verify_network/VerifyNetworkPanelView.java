package org.multibit.hd.ui.views.wizards.verify_network;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
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
 *  
 */
public class VerifyNetworkPanelView extends AbstractWizardPanelView<VerifyNetworkWizardModel, VerifyNetworkPanelModel> {

  // Panel specific components
  private JLabel peerCount;
  private JLabel blocksLeft;

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
      "[][]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(Labels.newVerifyNetworkNote(), "grow,push,span 2,wrap");

    peerCount = Labels.newValueLabel("0");
    blocksLeft = Labels.newValueLabel("0");

    contentPanel.add(Labels.newPeerCount(), "shrink");
    contentPanel.add(peerCount, "alignx left, push,wrap");

    contentPanel.add(Labels.newBlocksLeft(), "shrink");
    contentPanel.add(blocksLeft, "alignx left, push,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<VerifyNetworkWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    registerDefaultButton(getFinishButton());

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

  }

  @Subscribe
  public void onBitcoinNetworkChangedEvent(BitcoinNetworkChangedEvent event) {

    BitcoinNetworkSummary summary = event.getSummary();

    peerCount.setText(String.valueOf(summary.getPeerCount()));
    blocksLeft.setText(String.valueOf(summary.getBlocksLeft()));

  }


}