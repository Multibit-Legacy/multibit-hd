package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.uri.BitcoinURI;
import org.multibit.hd.brit.core.services.FeeService;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Confirm</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendBitCoinShapeShiftPaymentAckView extends AbstractWizardPanelView<SendBitcoinWizardModel,SendBitCoinShapeshiftPaymentAckModel> {

    private static final Logger log = LoggerFactory.getLogger(SendBitcoinConfirmPanelView.class);

    // View components
    private JTextArea notesTextArea;

    private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

    private JLabel recipientSummaryLabel;
    Coin finalAmount = Coin.valueOf(0);
    Address recieveAddress;

    private SendBitCoinShapeshiftPaymentAckModel panelModel;

    /**
     * @param wizard    The wizard managing the states
     * @param panelName The panel name for filtering component events
     */
    public SendBitCoinShapeShiftPaymentAckView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {
        super(wizard, panelName, AwesomeIcon.CLOUD_UPLOAD, MessageKey.CONFIRM_SEND_TITLE);
    }

    @Override
    public void newPanelModel() {
        // Require a reference for the model
        enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());

        // Configure the panel model
        panelModel = new SendBitCoinShapeshiftPaymentAckModel(
                getPanelName(),
                enterPasswordMaV.getModel()
        );
        setPanelModel(panelModel);

        // Bind it to the wizard model
       // getWizardModel().setConfirmPanelModel(panelModel);

        // Register components
        registerComponents(enterPasswordMaV);
    }

    @Override
    public void initialiseContent(JPanel contentPanel) {



        // User entered text
        notesTextArea = TextBoxes.newEnterPrivateNotes(getWizardModel());



        contentPanel.setLayout(
                new MigLayout(
                        Panels.migXYLayout(),
                        "[]10[]4[]0[200]10[120]10[][]", // Column constraints
                        "[]10[]10[][][][][][]10[][]" // Row constraints
                ));

        BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
        String recipientAddress = getWizardModel().getEnterAmountShapeshiftView().recipient.getText();
        Double amount = Double.valueOf(getWizardModel().getEnterAmountShapeshiftView().bitcoinAmount.getText());
        HashMap<String,Object> transactionInfo = getWizardModel().getEnterAmountShapeshiftView().service.createTransaction(recipientAddress,amount,bitcoinNetworkService.getNextChangeAddress().toString());
        System.out.println(transactionInfo);
        JLabel recipientLabel = new JLabel(recipientAddress);
        String amountinAltcoin = "Sending "+ (String)transactionInfo.get("withdrawalAmount") +" "+ getWizardModel().getEnterAmountShapeshiftView().altcoinSymbolLabel.getText()+ " @ "+getWizardModel().getEnterAmountShapeshiftView().altcoinPerBitcoin.toString() +" "+ getWizardModel().getEnterAmountShapeshiftView().altcoinSymbolLabel.getText()+"/BTC";
        JLabel amountLabel = new JLabel(amountinAltcoin);
        Font font = amountLabel.getFont().deriveFont(Font.PLAIN, (float) MultiBitUI.NORMAL_ICON_SIZE);
        amountLabel.setFont(font);
        JLabel bitcoinAmountLabel = new JLabel(amount.toString());

        //String depositAmount = transactionInfo.get()

        contentPanel.add(amountLabel, "span 7,push,wrap");
        Long fee = Configurations.currentConfiguration.getWallet().getFeePerKB();
        Double fees = Double.parseDouble(fee.toString())/100000000;
        DecimalFormat df = new DecimalFormat("#######.###########");
        JLabel feeLabel = new JLabel(df.format(fees));
        contentPanel.add(Labels.newRecipient());
        contentPanel.add(recipientLabel,"span 2,wrap");
        contentPanel.add(Labels.newAmount());
        contentPanel.add(bitcoinAmountLabel, "span 2,wrap");
        contentPanel.add(Labels.newTransactionFee());
        contentPanel.add(feeLabel,"span 2,wrap");
        contentPanel.add(Labels.newNotes());
        contentPanel.add(notesTextArea, "span 6,growx,push,wrap");

        if (!isHardwareWallet()) {
            contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "span 7,align right,wrap");
        }

        // Register components
    }

    @Override
    protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {
        PanelDecorator.addCancelPreviousSend(this, wizard);
    }

    @Override
    public void fireInitialStateViewEvents() {
        // Send button starts off disabled for regular wallets, enabled for Trezor hard wallets
        ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, isHardwareWallet());
    }

    /**
     * @return True if this is a hard wallet
     */
    private boolean isHardwareWallet() {

        return WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() &&
                WalletType.TREZOR_HARD_WALLET.equals(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletType())
                ;
    }

    @Override
    public boolean beforeShow() {
        Configuration configuration = Configurations.currentConfiguration;


        return true;
    }

    @Override
    public void afterShow() {
        notesTextArea.requestFocusInWindow();
    }

    @Override
    public void updateFromComponentModels(Optional componentModel) {
        panelModel.setNotes(notesTextArea.getText());

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
        return true;
    }
}