package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.services.ShapeShiftService;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

import javax.swing.*;

/**
 * Created by Keepkey on 8/25/16.
 */
public class SendBitCoinShapeShiftEnterAmountModel  extends AbstractWizardPanelModel {
    private JTextField recipient;
    ShapeShiftService service ;
    JLabel altcoinSymbolLabel ;
    Double altcoinPerBitcoin ;
    JLabel exhangeRateLabel;
    private boolean networkOk = false;

    /**
     * @param panelName           The panel name

     */
    public SendBitCoinShapeShiftEnterAmountModel(
            String panelName,
            JTextField recipient,
            ShapeShiftService service,
            JLabel altcoinSymbolLabel,
            Double altcoinPerBitcoin,
            JLabel exhangeRateLabel,
             boolean networkOk

    ) {
        super(panelName);
        this.recipient = recipient;
        this.service = service;
        this.altcoinPerBitcoin = altcoinPerBitcoin;
        this.altcoinSymbolLabel = altcoinSymbolLabel;
        this.exhangeRateLabel = exhangeRateLabel;
        this.networkOk = networkOk;

    }


}
