package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.math.BigInteger;
import java.util.List;

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

  private final Optional<BitcoinURI> bitcoinUri;

  // TODO (GR) Inject this
  private final NetworkParameters networkParameters = BitcoinNetwork.current().get();

  /**
   * @param wizard     The wizard managing the states
   * @param panelName  The panel name
   * @param bitcoinUri The optional Bitcoin URI providing an initial population of data
   */
  public SendBitcoinEnterAmountPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName, Optional<BitcoinURI> bitcoinUri) {

    super(wizard, panelName, MessageKey.SEND_BITCOIN_TITLE, AwesomeIcon.CLOUD_UPLOAD);

    this.bitcoinUri = bitcoinUri;

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
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    // Apply any Bitcoin URI parameters
    if (bitcoinUri.isPresent()) {
      handleBitcoinURI();
    }

    contentPanel.add(enterRecipientMaV.getView().newComponentPanel(), "wrap");
    contentPanel.add(enterAmountMaV.getView().newComponentPanel(), "wrap");

  }

  /**
   * TODO (GR) Consider the Bitcoin URI handling response enum
   * Fill in the Bitcoin URI details
   */
  private void handleBitcoinURI() {

    BitcoinURI uri = bitcoinUri.get();
    Optional<Address> address = Optional.fromNullable(uri.getAddress());
    Optional<BigInteger> amount = Optional.fromNullable(uri.getAmount());

    if (address.isPresent()) {

      final Recipient recipient;

      // Get the current wallet
      Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

      if (currentWalletSummary.isPresent()) {

        // Attempt to locate a contact with the address in the Bitcoin URI to reassure user
        List<Contact> contacts = CoreServices
          .getOrCreateContactService(currentWalletSummary.get().getWalletId())
          .filterContactsByBitcoinAddress(address.get());

        if (!contacts.isEmpty()) {
          // Offer the first contact with the matching address
          try {
            Address bitcoinAddress = new Address(networkParameters, contacts.get(0).getBitcoinAddress().get());
            recipient = new Recipient(bitcoinAddress);
            recipient.setContact(contacts.get(0));
          } catch (AddressFormatException e) {
            // This would indicate a failed filter in the ContactService
            throw new IllegalArgumentException("Contact has an malformed Bitcoin address: " + contacts.get(0), e);
          }
        } else {
          // No matching contact so make an anonymous Recipient
          recipient = new Recipient(address.get());
        }

      } else {
        // No current wallet so make an anonymous Recipient
        recipient = new Recipient(address.get());
      }

      enterRecipientMaV.getModel().setValue(recipient);

      // Only if the address is present will an amount be entered
      if (amount.isPresent()) {
        enterAmountMaV.getModel().setSatoshis(amount.get());
      }

    }
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
  public void afterShow() {

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

