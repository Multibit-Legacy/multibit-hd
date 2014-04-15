package org.multibit.hd.ui.views.wizards.bitcoin_settings;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.joda.money.BigMoney;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Settings: Bitcoin display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class BitcoinSettingsPanelView extends AbstractWizardPanelView<BitcoinSettingsWizardModel, BitcoinSettingsPanelModel> implements ActionListener {

  private JComboBox<String> localSymbolComboBox;

  private JLabel decimalErrorStatus;
  private JLabel groupingErrorStatus;

  private ModelAndView<DisplayAmountModel, DisplayAmountView> displayAmountMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public BitcoinSettingsPanelView(AbstractWizard<BitcoinSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SHOW_BITCOIN_WIZARD, AwesomeIcon.BITCOIN);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new BitcoinSettingsPanelModel(
      getPanelName(),
      configuration
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[][][][][][][][]" // Row constraints
    ));

    LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage().deepCopy();
    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin().deepCopy();
    Locale locale = languageConfiguration.getLocale();

    Preconditions.checkNotNull(locale, "'locale' cannot be empty");

    displayAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT, true);
    displayAmountMaV.getModel().setSatoshis(BigInteger.valueOf(123_456_789_012L)); // 1.23... million bitcoins
    displayAmountMaV.getModel().setLocalAmount(BigMoney.of(bitcoinConfiguration.getLocalCurrencyUnit(), new BigDecimal("1234567.89")));
    displayAmountMaV.getModel().setRateProvider(Optional.of(Languages.safeText(MessageKey.EXAMPLE)));
    displayAmountMaV.getModel().setLocalAmountVisible(true);

    JComboBox<String> decimalComboBox = ComboBoxes.newDecimalComboBox(this, bitcoinConfiguration);
    JComboBox<String> groupingComboBox = ComboBoxes.newGroupingComboBox(this, bitcoinConfiguration);

    localSymbolComboBox = ComboBoxes.newLocalSymbolComboBox(this, bitcoinConfiguration);
    JComboBox<String> placementComboBox = ComboBoxes.newPlacementComboBox(this, bitcoinConfiguration);
    JComboBox<BitcoinSymbol> bitcoinSymbolComboBox = ComboBoxes.newBitcoinSymbolComboBox(this, bitcoinConfiguration);

    decimalErrorStatus = Labels.newErrorStatus(false);
    decimalErrorStatus.setVisible(false);
    groupingErrorStatus = Labels.newErrorStatus(false);
    groupingErrorStatus.setVisible(false);

    contentPanel.add(Labels.newBitcoinSettingsNote(), "growx,push,span 3,wrap");

    contentPanel.add(Labels.newBitcoinSymbolLabel(), "shrink");
    contentPanel.add(bitcoinSymbolComboBox, "growx,push,wrap");

    contentPanel.add(Labels.newLocalSymbolLabel(), "shrink");
    contentPanel.add(localSymbolComboBox, "growx,push,wrap");

    contentPanel.add(Labels.newPlacementLabel(), "shrink");
    contentPanel.add(placementComboBox, "growx,push,wrap");

    contentPanel.add(Labels.newSelectGroupingLabel(), "shrink");
    contentPanel.add(groupingComboBox, "growx,push");
    contentPanel.add(groupingErrorStatus, "grow,push,wrap");

    contentPanel.add(Labels.newSelectDecimalLabel(), "shrink");
    contentPanel.add(decimalComboBox, "growx,push");
    contentPanel.add(decimalErrorStatus, "grow,push,wrap");

    contentPanel.add(Labels.newExampleLabel(), "shrink,wrap");
    contentPanel.add(displayAmountMaV.getView().newComponentPanel(), "push,span 3,align center,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<BitcoinSettingsWizardModel> wizard) {

    PanelDecorator.addCancelApply(this, wizard);

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

        localSymbolComboBox.requestFocusInWindow();

        displayAmountMaV.getView().updateView(getPanelModel().get().getConfiguration());

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Switch the main configuration over to the new one
      Configurations.switchConfiguration(getWizardModel().getConfiguration());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {


  }


  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    if (ComboBoxes.LOCAL_SYMBOL_COMMAND.equals(e.getActionCommand())) {
      handleLocalSymbolSelection(e);
    }
    if (ComboBoxes.BITCOIN_SYMBOL_COMMAND.equals(e.getActionCommand())) {
      handleBitcoinSymbolSelection(e);
    }
    if (ComboBoxes.PLACEMENT_COMMAND.equals(e.getActionCommand())) {
      handlePlacementSelection(e);
    }
    if (ComboBoxes.GROUPING_COMMAND.equals(e.getActionCommand())) {
      handleGroupingSelection(e);
    }
    if (ComboBoxes.DECIMAL_COMMAND.equals(e.getActionCommand())) {
      handleDecimalSelection(e);
    }

  }

  /**
   * <p>The grouping separator selection has changed</p>
   *
   * @param e The action event
   */
  private void handleGroupingSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String grouping = String.valueOf(source.getSelectedItem()).substring(0,1);

    // Validate the combination
    if (grouping.equals(getWizardModel().getConfiguration().getBitcoin().getDecimalSeparator())) {
      Sounds.playBeep();
      decimalErrorStatus.setVisible(false);
      groupingErrorStatus.setVisible(true);
      ViewEvents.fireWizardButtonEnabledEvent(
        getPanelName(),
        WizardButton.APPLY,
        false
      );
    } else {
      decimalErrorStatus.setVisible(false);
      groupingErrorStatus.setVisible(false);
      ViewEvents.fireWizardButtonEnabledEvent(
        getPanelName(),
        WizardButton.APPLY,
        true
      );
    }

    // Update the model (even if in error)
    getWizardModel().getConfiguration().getBitcoin().setGroupingSeparator(grouping);

    displayAmountMaV.getView().updateView(getWizardModel().getConfiguration());

  }

  /**
   * <p>The decimal separator selection has changed</p>
   *
   * @param e The action event
   */
  private void handleDecimalSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String decimal = String.valueOf(source.getSelectedItem()).substring(0,1);

    // Validate the combination
    if (decimal.equals(getWizardModel().getConfiguration().getBitcoin().getGroupingSeparator())) {
      Sounds.playBeep();
      groupingErrorStatus.setVisible(false);
      decimalErrorStatus.setVisible(true);
      ViewEvents.fireWizardButtonEnabledEvent(
        getPanelName(),
        WizardButton.APPLY,
        false
      );
    } else {
      groupingErrorStatus.setVisible(false);
      decimalErrorStatus.setVisible(false);
      ViewEvents.fireWizardButtonEnabledEvent(
        getPanelName(),
        WizardButton.APPLY,
        true
      );
    }

    // Update the model (even if in error)
    getWizardModel().getConfiguration().getBitcoin().setDecimalSeparator(decimal);

    displayAmountMaV.getView().updateView(getWizardModel().getConfiguration());

  }

  /**
   * <p>The local symbol selection has changed</p>
   *
   * @param e The action event
   */
  private void handleLocalSymbolSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    String localSymbol = (String) source.getSelectedItem();

    // Change the local symbol
    getWizardModel().getConfiguration().getBitcoin().setLocalCurrencySymbol(localSymbol);

    // Update the display to match the new configuration
    displayAmountMaV.getView().updateView(getWizardModel().getConfiguration());

  }

  /**
   * <p>The symbol placement selection has changed</p>
   *
   * @param e The action event
   */
  private void handlePlacementSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    boolean isLeading = (source.getSelectedIndex() == 0);

    // Change the position
    getWizardModel().getConfiguration().getBitcoin().setCurrencySymbolLeading(isLeading);

    // Update the display to match the new configuration
    displayAmountMaV.getView().updateView(getWizardModel().getConfiguration());

  }

  /**
   * <p>The grouping separator selection has changed</p>
   *
   * @param e The action event
   */
  private void handleBitcoinSymbolSelection(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();
    int ordinal = source.getSelectedIndex();

    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.values()[ordinal];

    // Change the Bitcoin symbol
    getWizardModel().getConfiguration().getBitcoin().setBitcoinSymbol(bitcoinSymbol.name());

    // Update the display to match the new configuration
    displayAmountMaV.getView().updateView(getWizardModel().getConfiguration());

  }

}
