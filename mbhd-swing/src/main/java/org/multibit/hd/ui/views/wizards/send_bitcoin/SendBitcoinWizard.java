package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.collect.Maps;
import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin sequence</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SendBitcoinWizard extends AbstractWizard {

  public SendBitcoinWizard() {

    getContentPanel().setSize(400, 400);

    Map<String,Action> actionMap = Maps.newHashMap();
    getContentPanel().add(new SendBitcoinEnterAmountPanel(this), "Send Bitcoin");
    getContentPanel().add(new SendBitcoinConfirmSendPanel(this), "Confirm Send");
  }

}
