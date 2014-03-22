package org.multibit.hd.ui.views.components.display_payments;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.BalanceChangedEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.utils.LocalisedDateUtils;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;

import javax.swing.*;
import java.util.List;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a list of payments</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayPaymentsView extends AbstractComponentView<DisplayPaymentsModel> {

  // View components
  private List<ModelAndView<DisplayAmountModel, DisplayAmountView>> displayAmountMaVList = Lists.newArrayList();

  private boolean initialised = false;

  final private Object lockObject = new Object();

  /**
   * @param model The model backing this view
   */
  public DisplayPaymentsView(DisplayPaymentsModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
            "insets 0", // Layout
            "[]10[]10[]", // Columns
            "[][]" // Rows
    ));

    // Populate components
    createView();

    return panel;
  }

  public void createView() {

    synchronized (lockObject) {
      if (getModel().isPresent()) {
        List<PaymentData> paymentDataList = getModel().get().getValue();
        panel.removeAll();
        displayAmountMaVList = Lists.newArrayList();

        for (PaymentData paymentData : paymentDataList) {
          JLabel timeLabel = Labels.newBlankLabel();
          timeLabel.setText(LocalisedDateUtils.formatShortDate(paymentData.getDate()));

          JLabel paymentDataLabel = Labels.newBlankLabel();
          paymentDataLabel.setText(Languages.safeText(paymentData.getType().getLocalisationKey()));
          LabelDecorator.applyStatusIcon(paymentData.getStatus(), paymentDataLabel, paymentData.isCoinBase(), MultiBitUI.NORMAL_ICON_SIZE);

          ModelAndView<DisplayAmountModel, DisplayAmountView> paymentAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.PLAIN, false);
          if (CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent().isPresent()) {
            paymentAmountMaV.getModel().setRateProvider(CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent().get().getRateProvider());
          }
          displayAmountMaVList.add(paymentAmountMaV);
          paymentAmountMaV.getModel().setSatoshis(paymentData.getAmountBTC());
          paymentAmountMaV.getModel().setLocalAmount(paymentData.getAmountFiat().getAmount());

          panel.add(timeLabel, "shrink");
          panel.add(paymentDataLabel, "shrink");
          JPanel amountPanel = paymentAmountMaV.getView().newComponentPanel();
          amountPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
          panel.add(amountPanel, "shrink, wrap");
        }

        panel.invalidate();
        panel.validate();
        panel.repaint();
        initialised = true;
      }
    }
  }

  synchronized public void updateView() {

    synchronized (lockObject) {
      if (!initialised) {
        return;
      }

      if (displayAmountMaVList != null) {
        for (ModelAndView<DisplayAmountModel, DisplayAmountView> paymentAmountMaV : displayAmountMaVList) {
          paymentAmountMaV.getView().updateView(Configurations.currentConfiguration);
        }
      }
      panel.invalidate();
      panel.validate();
      panel.repaint();
    }
  }

  @Subscribe
  public void onBalanceChangedEvent(BalanceChangedEvent event) {
    if (!initialised) {
      return;
    }

    createView();
    if (displayAmountMaVList != null) {
      for (ModelAndView<DisplayAmountModel, DisplayAmountView> paymentAmountMaV : displayAmountMaVList) {
        paymentAmountMaV.getModel().setRateProvider(event.getRateProvider());
      }
    }
    updateView();
  }


  @Override
  public void requestInitialFocus() {
  }

  @Override
  public void updateModelFromView() {
    // Do nothing - the model is driving the view
  }

  /**
   * <p>Called when the model has changed and the view components need to update</p>
   * <p>Default implementation is to do nothing since most components are pulling data</p>
   */
  @Override
  public void updateViewFromModel() {
  }
}