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

  private CardLayout cardLayout = new CardLayout();
  private final JPanel contentPanel = new JPanel(cardLayout);

  public SendBitcoinWizard() {

    contentPanel.setSize(400, 400);

    Map<String,Action> actionMap = Maps.newHashMap();
    contentPanel.add(new SendBitcoinEnterAmountPanel(), "Send Bitcoin");
    contentPanel.add(new SendBitcoinConfirmSendPanel(), "Confirm Send");
  }

  public JPanel getContentPanel() {

    return contentPanel;

  }

}
