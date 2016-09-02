package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "confirm" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SendBitCoinShapeshiftPaymentAckModel extends AbstractWizardPanelModel {

    private String notes;
    private final EnterPasswordModel passwordModel;

    public SendBitCoinShapeshiftPaymentAckModel(String panelName, EnterPasswordModel passwordModel) {
        super(panelName);

        this.passwordModel = passwordModel;

    }

    /**
     * @return The notes associated with the transaction
     */
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return The "enter credentials" model
     */
    public EnterPasswordModel getPasswordModel() {
        return passwordModel;
    }

}
