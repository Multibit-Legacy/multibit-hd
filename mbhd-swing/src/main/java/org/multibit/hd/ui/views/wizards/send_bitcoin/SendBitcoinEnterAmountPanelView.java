package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.math.BigInteger;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class SendBitcoinEnterAmountPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinEnterAmountPanelModel> {

  // Panel specific components
  private ModelAndView<EnterRecipientModel, EnterRecipientView> enterRecipientMaV;
  private ModelAndView<EnterAmountModel, EnterAmountView> enterAmountMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public SendBitcoinEnterAmountPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SEND_BITCOIN_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    enterRecipientMaV = Components.newEnterRecipientMaV(getPanelName());
    enterAmountMaV = Components.newEnterAmountMaV(getPanelName());

    // Configure the panel model
    final SendBitcoinEnterAmountPanelModel panelModel = new SendBitcoinEnterAmountPanelModel(
      getPanelName(),
      enterRecipientMaV.getModel(),
      enterAmountMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterAmountPanelModel(panelModel);

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.CLOUD_UPLOAD);

    panel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(enterRecipientMaV.getView().newComponentPanel(), "wrap");
    panel.add(enterAmountMaV.getView().newComponentPanel(), "wrap");

    return panel;
  }

  @Override
  public void afterShow() {

    registerDefaultButton(getNextButton());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        enterRecipientMaV.getView().requestInitialFocus();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      isNextEnabled()
    );

  }

  /**
   * @return True if the "next" button should be enabled
   */
  private boolean isNextEnabled() {

    boolean bitcoinAmountOK = !getPanelModel().get()
      .getEnterAmountModel()
      .getSatoshis()
      .equals(BigInteger.ZERO);


    boolean recipientOK = getPanelModel().get()
      .getEnterRecipientModel()
      .getRecipient()
      .isPresent();

    return bitcoinAmountOK && recipientOK;

  }


}

