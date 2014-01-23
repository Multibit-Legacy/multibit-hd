package org.multibit.hd.ui.views.components;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordView;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressModel;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressView;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseModel;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseView;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountModel;
import org.multibit.hd.ui.views.components.enter_amount.EnterAmountView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientView;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseView;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileView;

import javax.swing.*;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Creation of complex components requiring a model and view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Components {

  /**
   * <p>A wallet detail panel provides a summary of the information contained within the wallet</p>
   *
   * @return A new wallet detail panel
   */
  public static JPanel newWalletDetailPanel() {

    MigLayout layout = new MigLayout(
      "fillx", // Layout
      "[]10[grow]", // Columns
      "[][][][]"  // Rows
    );

    JPanel panel = Panels.newPanel(layout);

    panel.add(new JLabel("Summary"), "wrap");
    panel.add(new JLabel("Location:"));
    panel.add(new JLabel("/Users/<someone>/Library/Application Support/MultiBitHD/mbhd-2412897490823174231947"), "push,wrap");
    panel.add(new JLabel("Contacts:"));
    panel.add(new JLabel("357"), "push,wrap");
    panel.add(new JLabel("Transactions:"));
    panel.add(new JLabel("165"), "push,wrap");

    return panel;
  }

  /**
   * <p>A "recipient" panel provides a means of finding a recipient either by their contact name or a direct Bitcoin address</p>
   *
   * @return A new "recipient" panel
   */
  public static ModelAndView<EnterRecipientModel, EnterRecipientView> newEnterRecipientMaV(String panelName) {

    EnterRecipientModel model = new EnterRecipientModel(panelName);
    EnterRecipientView view = new EnterRecipientView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "Bitcoin amount" panel provides a means of entering either a Bitcoin or fiat amount and seeing an immediate rate conversion</p>
   *
   * @return A new Bitcoin amount panel
   */
  public static ModelAndView<EnterAmountModel, EnterAmountView> newEnterAmountMaV(String panelName) {

    EnterAmountModel model = new EnterAmountModel(panelName);
    EnterAmountView view = new EnterAmountView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "confirm password" model and view handles a password with confirmation and reveal</p>
   *
   * @param panelName The panel name to identify "verification status" and "next" buttons
   *
   * @return A new "confirm password" model and view
   */
  public static ModelAndView<ConfirmPasswordModel, ConfirmPasswordView> newConfirmPasswordMaV(String panelName) {

    ConfirmPasswordModel model = new ConfirmPasswordModel(panelName);
    ConfirmPasswordView view = new ConfirmPasswordView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>An "enter password" model and view handles password entry with reveal</p>
   *
   * @param panelName The panel name to identify "next" buttons
   *
   * @return A new "enter password" model and view
   */
  public static ModelAndView<EnterPasswordModel, EnterPasswordView> newEnterPasswordMaV(String panelName) {

    EnterPasswordModel model = new EnterPasswordModel(panelName);
    EnterPasswordView view = new EnterPasswordView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "display QR" model and view displays a QR code with the following features:</p>
   * <ul>
   * <li>Image field showing a QR code</li>
   * <li>Button to copy the QR code image to the Clipboard</li>
   * <li>Button to close the light box popover</li>
   * <li></li>
   * </ul>
   *
   * @param content The text to present in the QR code
   *
   * @return A new "display Bitcoin address" model and view
   */
  public static ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> newDisplayQRCodeMaV(final String content) {

    DisplayQRCodeModel model = new DisplayQRCodeModel(content);
    DisplayQRCodeView view = new DisplayQRCodeView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "display Bitcoin address" model and view displays a Bitcoin address with the following features:</p>
   * <ul>
   * <li>Non-editable text field showing the address</li>
   * <li>Button to copy the address to the Clipboard</li>
   * </ul>
   *
   * @param bitcoinAddress The Bitcoin address
   *
   * @return A new "display Bitcoin address" model and view
   */
  public static ModelAndView<DisplayBitcoinAddressModel, DisplayBitcoinAddressView> newDisplayBitcoinAddressMaV(final String bitcoinAddress) {

    DisplayBitcoinAddressModel model = new DisplayBitcoinAddressModel(bitcoinAddress);
    DisplayBitcoinAddressView view = new DisplayBitcoinAddressView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "seed phrase" model and view displays the words used in a BIP0039 seed (no edit/copy/paste etc)</p>
   *
   * @param generator The seed phrase generator
   *
   * @return A new "seed phrase" model and view
   */
  public static ModelAndView<DisplaySeedPhraseModel, DisplaySeedPhraseView> newDisplaySeedPhraseMaV(final SeedPhraseGenerator generator) {

    DisplaySeedPhraseModel model = new DisplaySeedPhraseModel(generator);
    DisplaySeedPhraseView view = new DisplaySeedPhraseView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "seed phrase" model and view handles user data entry of the words used in a BIP0039 seed</p>
   *
   * @param panelName The panel name to identify "verification status" and "next" buttons
   *
   * @return A new "seed phrase" model and view
   */
  public static ModelAndView<EnterSeedPhraseModel, EnterSeedPhraseView> newEnterSeedPhraseMaV(String panelName) {

    EnterSeedPhraseModel model = new EnterSeedPhraseModel(panelName);
    EnterSeedPhraseView view = new EnterSeedPhraseView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "select file" model and view handles user data entry of a file or path</p>
   *
   * @param panelName The panel name to identify "next" button
   *
   * @return A new "select file" model and view
   */
  public static ModelAndView<SelectFileModel, SelectFileView> newSelectFileMaV(String panelName) {

    SelectFileModel model = new SelectFileModel(panelName);
    SelectFileView view = new SelectFileView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "display amount" model and view handles presentation of a Bitcoin and local currency amount</p>
   *
   * @param style The display amount style
   *
   * @return A new "display amount" model and view
   */
  public static ModelAndView<DisplayAmountModel, DisplayAmountView> newDisplayAmountMaV(DisplayAmountStyle style) {

    DisplayAmountModel model = new DisplayAmountModel(style);
    DisplayAmountView view = new DisplayAmountView(model);

    return new ModelAndView<>(model, view);

  }

}
