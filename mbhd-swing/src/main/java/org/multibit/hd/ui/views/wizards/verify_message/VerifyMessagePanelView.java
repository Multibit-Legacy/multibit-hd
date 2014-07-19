package org.multibit.hd.ui.views.wizards.verify_message;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.VerifyMessageResult;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.BitcoinMessages;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.ClipboardUtils;
import org.multibit.hd.ui.utils.WhitespaceTrimmer;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.text_fields.FormattedBitcoinAddressField;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Verify message: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class VerifyMessagePanelView extends AbstractWizardPanelView<VerifyMessageWizardModel, String> {

  // View components
  FormattedBitcoinAddressField verifyingAddress;
  JTextArea signatureTextArea;
  JTextArea messageTextArea;

  JLabel reportLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public VerifyMessagePanelView(AbstractWizard<VerifyMessageWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.VERIFY_MESSAGE_TITLE, AwesomeIcon.CHECK);

  }

  @Override
  public void newPanelModel() {

    setPanelModel("");

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][][]", // Column constraints
      "[]5[][100][30][30][30]" // Row constraints
    ));

    verifyingAddress = TextBoxes.newEnterBitcoinAddress(getWizardModel(), false);

    messageTextArea = TextBoxes.newEnterMessage();

    // The message is a wall of text so needs scroll bars in many cases
    messageTextArea.setBorder(null);

    // Message requires its own scroll pane
    JScrollPane messageScrollPane = new JScrollPane();
    messageScrollPane.setOpaque(true);
    messageScrollPane.setBackground(Themes.currentTheme.dataEntryBackground());
    messageScrollPane.setBorder(null);
    messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    messageScrollPane.setViewportView(messageTextArea);
    messageScrollPane.getViewport().setBackground(Themes.currentTheme.dataEntryBackground());
    messageScrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));

    // Ensure we maintain the overall theme
    ScrollBarUIDecorator.apply(messageScrollPane, true);

    signatureTextArea = TextBoxes.newTextArea(5, 40);
    AccessibilityDecorator.apply(signatureTextArea, MessageKey.SIGNATURE);

    contentPanel.add(Labels.newVerifyMessageNote(), "growx,span 4,wrap");

    contentPanel.add(Labels.newBitcoinAddress());
    contentPanel.add(verifyingAddress, "growx,span 3,push,wrap");

    contentPanel.add(Labels.newMessage());
    contentPanel.add(messageScrollPane, "grow,span 3,push,wrap");

    contentPanel.add(Labels.newSignature());
    contentPanel.add(signatureTextArea, "grow,span 3,push,wrap");

    contentPanel.add(Buttons.newVerifyMessageButton(getSignMessageAction()), "align right,cell 2 4,");
    contentPanel.add(Buttons.newPasteAllButton(getPasteAllAction()), "align right,cell 2 4,");
    contentPanel.add(Buttons.newClearAllButton(getClearAllAction()), "cell 3 4,wrap");

    reportLabel = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(reportLabel, MessageKey.NOTES);
    contentPanel.add(reportLabel, "growx,span 4");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<VerifyMessageWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        verifyingAddress.requestFocusInWindow();

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Ensure the wizard model correctly reflects the contents of the components
      updateFromComponentModels(Optional.absent());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Update models from component values

  }


  /**
   * @return A new "verify message" action
   */
  private Action getSignMessageAction() {

    // Sign the message
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        verifyMessage();

      }

    };
  }

  /**
   * @return A new "clear all" action
   */
  private Action getClearAllAction() {

    // Sign the message
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        verifyingAddress.setText("");
        messageTextArea.setText("");
        signatureTextArea.setText("");
        reportLabel.setText("");
        reportLabel.setIcon(null);
      }

    };
  }

  /**
   * @return A new "paste all" action
   */
  private Action getPasteAllAction() {

    // Sign the message
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        BitcoinMessages.SignedMessage signedMessage = BitcoinMessages.parseSignedMessage(ClipboardUtils.pasteStringFromClipboard());

        messageTextArea.setText(signedMessage.getMessage());
        verifyingAddress.setText(signedMessage.getAddress());
        signatureTextArea.setText(signedMessage.getSignature());

        reportLabel.setText("");
        reportLabel.setIcon(null);

      }

    };
  }

  /**
   * Verify the message text against the address specified and update UI
   */
  private void verifyMessage() {
    String addressText = WhitespaceTrimmer.trim(verifyingAddress.getText());
    String messageText = messageTextArea.getText();
    String signatureText = signatureTextArea.getText();

    VerifyMessageResult verifyMessageResult = WalletManager.INSTANCE.verifyMessage(addressText, messageText, signatureText);

    reportLabel.setText(Languages.safeText(verifyMessageResult.getVerifyKey(), verifyMessageResult.getVerifyData()));
    Labels.decorateStatusLabel(reportLabel, Optional.of(verifyMessageResult.isVerifyWasSuccessful()));
  }
}