package org.multibit.hd.ui.views.wizards.send_bitcoin;

/**
 * Created by Keepkey on 8/25/16.
 */

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Addresses;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.services.ShapeShiftService;
import org.multibit.hd.ui.utils.ClipboardUtils;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.text_fields.FormattedDecimalField;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 */

public class SendBitCoinShapeShiftEnterAmountView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitCoinShapeShiftEnterAmountModel> {

    // Panel specific components

    JTextField recipient = TextBoxes.newAltcoinAddress();
    ShapeShiftService service = new ShapeShiftService();
    JLabel altcoinSymbolLabel = new JLabel();
    Double altcoinPerBitcoin = 0.0;
    JLabel exhangeRateLabel = new JLabel();
    private boolean networkOk = false;
    FormattedDecimalField bitcoinAmount  = TextBoxes.newBitcoinAmount(BitcoinSymbol.maxSymbolicAmount().doubleValue());
    FormattedDecimalField altcoinAmount  = TextBoxes.newBitcoinAmount(BitcoinSymbol.maxSymbolicAmount().doubleValue());

    /**
     * @param wizard    The wizard managing the states
     * @param panelName The panel name
     */
    public SendBitCoinShapeShiftEnterAmountView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {
        super(wizard, panelName, AwesomeIcon.CLOUD_UPLOAD, MessageKey.SEND_BITCOIN_TITLE);
    }

    @Override
    public void newPanelModel() {


        // Configure the panel model
        final SendBitCoinShapeShiftEnterAmountModel panelModel = new SendBitCoinShapeShiftEnterAmountModel(
                getPanelName(),recipient,service,altcoinSymbolLabel,altcoinPerBitcoin,exhangeRateLabel,networkOk

        );
        setPanelModel(panelModel);

        // Bind it to the wizard model
        getWizardModel().setSendBitCoinShapeShiftEnterAmountView(this);

        // Register components
        //registerComponents(enterAmountMaV, enterRecipientMaV);
    }

    @Override
    public void initialiseContent(JPanel contentPanel) {
        contentPanel.setLayout(
                new MigLayout(
                        Panels.migXYLayout(),
                        "[]", // Column constraints
                        "[]10[]" // Row constraints
                ));



        //Initializing different panels and setting background transparent in Content Panel
        JPanel convertPanel = new JPanel();
        JPanel recipientPanel = new JPanel();
        JPanel amountPanel = new JPanel();
        recipientPanel.setOpaque(false);
        convertPanel.setOpaque(false);
        amountPanel.setOpaque(false);



        // Creating and adding components to dropdown panel
        JLabel sendAltcoin = new JLabel("Make a Payment in");
        JComboBox coinList = new JComboBox(service.getCoinList());
        //Item change Listener for Dropdown
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                if(state == itemEvent.SELECTED){
                    System.out.println("Item: " + itemEvent.getItem());
                    ItemSelectable is = itemEvent.getItemSelectable();
                    String symbol = service.CurrencyTable.get(itemEvent.getItem());
                    System.out.println(symbol);
                    altcoinSymbolLabel.setText(symbol);
                    service.currentAltCoinMarketInfo = service.getMarketInfo((String)itemEvent.getItem());
                    altcoinPerBitcoin = (Double)service.currentAltCoinMarketInfo.get("rate");
                    String exchangeRate = "1 BTC ="+ altcoinPerBitcoin.toString()+" "+ altcoinSymbolLabel.getText();
                    exhangeRateLabel.setText(exchangeRate);
                }

            }
        };
        coinList.addItemListener(itemListener);
        convertPanel.add(sendAltcoin);
        convertPanel.add(coinList);

        //Creating and Adding components to recipientPanel
        JLabel altcoinAddress = new JLabel("Recipient");
        recipientPanel.add(altcoinAddress);
        recipientPanel.add(recipient);
        recipientPanel.add(Buttons.newPasteButton(getPasteAction()));

        //Creating and Adding components to amountPanel
        JLabel bitcoinSymbolLabel = new JLabel();
        Font font = bitcoinSymbolLabel.getFont().deriveFont(Font.PLAIN, (float) MultiBitUI.NORMAL_ICON_SIZE);
        bitcoinSymbolLabel.setFont(font);
        altcoinSymbolLabel.setFont(font);
        altcoinSymbolLabel.setText(service.CurrencyTable.get(coinList.getSelectedItem()));
        service.currentAltCoinMarketInfo = service.getMarketInfo((String)coinList.getSelectedItem());
        altcoinPerBitcoin = (Double)service.currentAltCoinMarketInfo.get("rate");
        String exchangeRate = "1 BTC ="+ altcoinPerBitcoin.toString()+" "+ altcoinSymbolLabel.getText();
        exhangeRateLabel.setText(exchangeRate);
        convertPanel.add(exhangeRateLabel);
        // Use the current Bitcoin configuration
        LabelDecorator.applyBitcoinSymbolLabel(bitcoinSymbolLabel);
        bitcoinAmount.setColumns(10);
        altcoinAmount.setColumns(10);
        amountPanel.add(Labels.newAmount(),"span 4,grow,push,wrap");
        amountPanel.add(bitcoinSymbolLabel);
        amountPanel.add(bitcoinAmount);
        amountPanel.add(altcoinSymbolLabel,"pushy,baseline");
        amountPanel.add(altcoinAmount,"wrap");




        //Adding panels to mainpanel
        contentPanel.add(convertPanel,"wrap");
        contentPanel.add(recipientPanel,"wrap");
        contentPanel.add(amountPanel,"wrap");

    }

    @Override
    protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {
        PanelDecorator.addExitCancelNext(this, wizard);
    }

    @Override
    public void fireInitialStateViewEvents() {
        // Next button starts off disabled
        ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);
    }

    @Override
    public boolean beforeShow() {
        // Ensure the pay request button is kept up to date
        Optional<BitcoinNetworkChangedEvent> changedEvent = CoreServices.getApplicationEventService().getLatestBitcoinNetworkChangedEvent();
        if (changedEvent.isPresent()) {
            updateNextButton(changedEvent.get());
        }
        return true;
    }

    @Override
    public void afterShow() {
        recipient.requestFocus();

        // Determine any events (we may have a fully populated screen right at the start)
        ViewEvents.fireWizardButtonEnabledEvent(
                getPanelName(),
                WizardButton.NEXT,
                isNextEnabled()
        );
    }

    @Override
    public void updateFromComponentModels(Optional componentModel) {
        // No need to update the panel model it already has the references

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
        return  true;
    }

    /**
     * @param event The "Bitcoin network changed" event - one per block downloaded during synchronization
     */
    @Subscribe
    public void onBitcoinNetworkChangeEvent(final BitcoinNetworkChangedEvent event) {
        if (!isInitialised()) {
            return;
        }

        Preconditions.checkNotNull(event, "'event' must be present");
        Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

        BitcoinNetworkSummary summary = event.getSummary();

        Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");

        // Keep the UI response to a minimum due to the volume of these events
        updateNextButton(event);
    }

    private void updateNextButton(BitcoinNetworkChangedEvent event) {
        boolean newEnabled;
        boolean canChange = true;

        // Cannot pay a request until synced as you don't know how much is in the wallet
        switch (event.getSummary().getSeverity()) {
            case RED:
            case AMBER:
                // Enable on RED or AMBER only if unrestricted (allows FEST tests without a network)
                newEnabled = InstallationManager.unrestricted;
                networkOk = newEnabled;
                break;
            case GREEN:
                // Always enable on GREEN if data is valid
                newEnabled = isNextEnabled();
                networkOk = true;
                break;
            case PINK:
            case EMPTY:
                // Maintain the status quo
                newEnabled = getNextButton().isEnabled();
                canChange = false;
                break;
            default:
                // Unknown status
                throw new IllegalStateException("Unknown event severity " + event.getSummary().getSeverity());
        }

        if (canChange) {
            final boolean finalNewEnabled = newEnabled;

            // If button is not enabled and the newEnabled is false don't do anything
            // This cuts down the number of events
            if (getNextButton().isEnabled() || newEnabled) {
                SwingUtilities.invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, finalNewEnabled);
                            }
                        });
            }
        }
    }
    private Action getPasteAction() {

        // Paste the recipient information
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                performPaste();
            }
        };
    }

    private void performPaste() {
       Optional <String> pastedText = ClipboardUtils.pasteStringFromClipboard();
      if(pastedText.isPresent()){
          this.recipient.setText(pastedText.get());
      }
      else
          this.recipient.setText("");
    }
}

