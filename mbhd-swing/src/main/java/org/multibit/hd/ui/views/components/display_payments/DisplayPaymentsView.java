package org.multibit.hd.ui.views.components.display_payments;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.BalanceChangedEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a list of payments</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class DisplayPaymentsView extends AbstractComponentView<DisplayPaymentsModel> {

  private static final Logger log = LoggerFactory.getLogger(DisplayPaymentsView.class);

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

    panel = Panels.newPanel(
      new MigLayout(
        "insets 0", // Layout
        "[]10[]10[]", // Columns
        "[][]" // Rows
      ));

    // Populate components
    createView();

    return panel;
  }

  /**
   * Refresh or create the view by rebuilding the content panel
   */
  public void createView() {

    // Prevent other events interfering
    synchronized (lockObject) {

      if (getModel().isPresent()) {

        final String panelName = getModel().get().getPanelName();

        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {

              // Clear the panel
              List<PaymentData> paymentDataList = getModel().get().getValue();
              panel.removeAll();

              // Deregister any previous entries from UI events since
              // this view is never closed unless the application exits
              for (ModelAndView<DisplayAmountModel, DisplayAmountView> mav : displayAmountMaVList) {
                mav.unsubscribe();
              }

              // Reset the list
              displayAmountMaVList.clear();

              log.trace("Displaying {} payment(s) for {}", paymentDataList.size(), panelName);

              // Work through the list of payment data entries
              int count = 0;
              for (PaymentData paymentData : paymentDataList) {

                // TODO Address the EDT violation occurring here

                // Time label
                JLabel timeLabel = Labels.newBlankLabel();
                // Display in the system timezone
                timeLabel.setText(Dates.formatShortTimeLocal(paymentData.getDate()));

                // Payment icon label and text ("sending", "receiving" etc)
                JLabel paymentDataLabel = Labels.newBlankLabel();
                paymentDataLabel.setText(Languages.safeText(paymentData.getType().getLocalisationKey()));
                LabelDecorator.applyPaymentStatusIcon(paymentData.getStatus(), paymentDataLabel, paymentData.isCoinBase(), MultiBitUI.NORMAL_ICON_SIZE);

                // Create a unique FEST name to ensure accessibility
                String festName = panelName + "." + paymentData.getType().name().toLowerCase(Locale.UK) + "." + count;

                // Amount MaV (ensure it is accessible)
                ModelAndView<DisplayAmountModel, DisplayAmountView> paymentAmountMaV = Components.newDisplayAmountMaV(DisplayAmountStyle.PLAIN, false, festName);
                if (CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent().isPresent()) {
                  Optional<String> rateProvider = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent().get().getRateProvider();
                  paymentAmountMaV.getModel().setRateProvider(rateProvider);
                  paymentAmountMaV.getModel().setLocalAmountVisible(rateProvider.isPresent());
                }

                // Show the local currency if we have fiat currency information
                boolean showLocalCurrency = paymentData.getAmountFiat() != null && paymentData.getAmountFiat().getCurrency().isPresent();
                // Don't show the fiat amount if the currency on the fiat payment is different to that in the bitcoin configuration (it's misleading)
                if (showLocalCurrency && !paymentData.getAmountFiat().getCurrency().get().getCurrencyCode().equals(Configurations.currentConfiguration.getBitcoin().getLocalCurrencyCode())) {
                  showLocalCurrency = false;
                }
                paymentAmountMaV.getModel().setLocalAmountVisible(showLocalCurrency);

                paymentAmountMaV.getView().setVisible(true);
                paymentAmountMaV.getModel().setCoinAmount(paymentData.getAmountCoin());
                if (paymentData.getAmountFiat() != null && paymentData.getAmountFiat().getAmount().isPresent()) {
                  paymentAmountMaV.getModel().setLocalAmount(paymentData.getAmountFiat().getAmount().get());
                } else {
                  paymentAmountMaV.getModel().setLocalAmount(null);
                }

                displayAmountMaVList.add(paymentAmountMaV);

                // Add to the panel
                panel.add(timeLabel, "shrink");
                panel.add(paymentDataLabel, "shrink");
                JPanel amountPanel = paymentAmountMaV.getView().newComponentPanel();
                amountPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                panel.add(amountPanel, "shrink, wrap");

                count++;
              }

              // Redraw
              panel.invalidate();
              panel.validate();
              panel.repaint();

              initialised = true;

            }
          });
      }
    }
  }

  synchronized public void updateView() {

    synchronized (lockObject) {
      if (!initialised) {
        return;
      }

      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            if (displayAmountMaVList != null) {
              for (ModelAndView<DisplayAmountModel, DisplayAmountView> paymentAmountMaV : displayAmountMaVList) {
                paymentAmountMaV.getView().updateView(Configurations.currentConfiguration);
              }
            }
            panel.invalidate();
            panel.validate();
            panel.repaint();

          }
        });

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
        paymentAmountMaV.getModel().setLocalAmountVisible(event.getRateProvider().isPresent());
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