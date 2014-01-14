package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.ui.events.view.WizardPanelModelChangedEvent;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

import static org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState.*;

/**
 * <p>Model object to provide the following to "send bitcoin wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinWizardModel extends AbstractWizardModel<SendBitcoinState> {

  /**
   * The current state
   */
  private SendBitcoinState state = ENTER_AMOUNT;

  /**
   * The Bitcoin amount
   */
  private BigMoney btcAmount = BigMoney.zero(CurrencyUnit.of("BTC"));
  private String password;

  /**
   * @param state The state object
   */
  public SendBitcoinWizardModel(SendBitcoinState state) {
    super(state);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void update(Optional panelModel) {

    // No state transitions occur in this method

    // TODO Consider migrating state into dedicated objects

    switch (state) {
      case ENTER_AMOUNT:
        btcAmount = (BigMoney) panelModel.get();
        break;
      case CONFIRM_AMOUNT:
        password = (String) panelModel.get();
        break;
      case SEND_BITCOIN_REPORT:
        break;
    }

  }

  @Override
  public void next() {

    switch (state) {
      case ENTER_AMOUNT:
        state = CONFIRM_AMOUNT;
        break;
      case CONFIRM_AMOUNT:
        state = SEND_BITCOIN_REPORT;
        break;
    }

  }

  @Override
  public void previous() {

    switch (state) {
      case ENTER_AMOUNT:
        state = ENTER_AMOUNT;
        break;
      case CONFIRM_AMOUNT:
        state = ENTER_AMOUNT;
        break;
    }

  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  @Override
  public void onWizardPanelModelChangedEvent(WizardPanelModelChangedEvent event) {
    // Do nothing
  }

  /**
   * @return The Bitcoin amount to send
   */
  public BigMoney getBtcAmount() {
    return btcAmount;
  }

  /**
   * @return The password the user entered
   */
  public String getPassword() {
    return password;
  }
}
