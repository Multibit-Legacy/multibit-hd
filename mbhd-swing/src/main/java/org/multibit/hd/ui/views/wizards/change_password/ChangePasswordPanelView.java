package org.multibit.hd.ui.views.wizards.change_password;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.*;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletPassword;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordView;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ChangePasswordPanelView extends AbstractWizardPanelView<ChangePasswordWizardModel, ChangePasswordPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(ChangePasswordPanelView.class);

  // Panel specific components
  private ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> displaySecurityPopoverMaV;

  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;
  private ModelAndView<ConfirmPasswordModel, ConfirmPasswordView> confirmPasswordMaV;
  private ListeningExecutorService executorService;

  /**
   * @param wizard The wizard managing the states
   */
  public ChangePasswordPanelView(AbstractWizard<ChangePasswordWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.CHANGE_PASSWORD_TITLE, AwesomeIcon.LOCK);

  }

  @Override
  public void newPanelModel() {

    displaySecurityPopoverMaV = Popovers.newDisplaySecurityPopoverMaV(getPanelName());
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());
    confirmPasswordMaV = Components.newConfirmPasswordMaV(getPanelName());

    // Configure the panel model
    final ChangePasswordPanelModel panelModel = new ChangePasswordPanelModel(
      getPanelName(),
      enterPasswordMaV.getModel(),
      confirmPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setChangePasswordPanelModel(panelModel);

    // Register components
    registerComponents(enterPasswordMaV, confirmPasswordMaV, displaySecurityPopoverMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Postpone initialisation until first showing
    executorService = SafeExecutors.newSingleThreadExecutor("change-credentials");

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    contentPanel.add(Labels.newChangePasswordNote1(), "wrap");
    contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");
    contentPanel.add(Labels.newChangePasswordNote2(), "wrap");
    contentPanel.add(confirmPasswordMaV.getView().newComponentPanel(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ChangePasswordWizardModel> wizard) {

    PanelDecorator.addCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        enterPasswordMaV.getView().requestInitialFocus();

        // This requires a security popover check
        checkForSecurityEventPopover(displaySecurityPopoverMaV);

      }
    });

  }

  @Override
  public boolean beforeHide(final boolean isExitCancel) {

    // Don't call super since this is a deferred hide

    // Don't block an exit
    if (isExitCancel) {
      return true;
    }

    // Start the spinner (we are deferring the hide)
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Ensure the view shows the spinner and disables components
        getNextButton().setEnabled(false);
        getCancelButton().setEnabled(false);
        enterPasswordMaV.getView().setSpinnerVisible(true);

      }
    });

    // Check the old credentials (might take a while so do it asynchronously while showing a spinner)
    // Tar pit (must be in a separate thread to ensure UI updates)
    ListenableFuture<Boolean> passwordFuture = executorService.submit(new Callable<Boolean>() {

      @Override
      public Boolean call() {

        // Need a very short delay here to allow the UI thread to update
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

        return checkPassword();

      }
    });
    Futures.addCallback(passwordFuture, new FutureCallback<Boolean>() {

        @Override
        public void onSuccess(Boolean result) {

          // Check the result
          if (result) {

            // Manually unsubscribe the MaVs
            CoreEvents.unsubscribe(enterPasswordMaV);
            CoreEvents.unsubscribe(confirmPasswordMaV);
            CoreEvents.unsubscribe(displaySecurityPopoverMaV);


            // Enable components
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                // Trigger the deferred hide
                ViewEvents.fireWizardDeferredHideEvent(getPanelName(), false);

                if (isNextEnabled()) {
                  getNextButton().setEnabled(true);
                }
                getCancelButton().setEnabled(true);
              }
            });

          } else {

            // Wait just long enough to be annoying (anything below 2 seconds is comfortable)
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

            // Failed
            Sounds.playBeep();

            // Ensure the view hides the spinner and enables components
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {

                if (isNextEnabled()) {
                  getNextButton().setEnabled(true);
                }
                getCancelButton().setEnabled(true);
                enterPasswordMaV.getView().setSpinnerVisible(false);

                enterPasswordMaV.getView().requestInitialFocus();

              }
            });
          }
        }

        @Override
        public void onFailure(Throwable t) {

          // Ensure the view hides the spinner and enables components
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

              if (isNextEnabled()) {
                getNextButton().setEnabled(true);
              }
              getCancelButton().setEnabled(true);
              enterPasswordMaV.getView().setSpinnerVisible(false);

              enterPasswordMaV.getView().requestInitialFocus();
            }
          });

          // Should not have seen an error
          ExceptionHandler.handleThrowable(t);
        }
      }
    );

    // Defer the hide operation
    return false;
  }

  /**
   * @return True if the selected wallet can be opened with the given credentials
   */
  private boolean checkPassword() {

    CharSequence password = enterPasswordMaV.getModel().getValue();

    if (!"".equals(password)) {

      // If a credentials has been entered, put it into the wallet summary (so that it is available for address generation)
      WalletId walletId = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId();
      Optional<WalletSummary> currentWalletSummary;
      try {
        currentWalletSummary = WalletManager.INSTANCE.openWalletFromWalletId(InstallationManager.getOrCreateApplicationDataDirectory(), walletId, password);
      } catch (WalletLoadException wle) {
        // Wallet did not load - assume credentials was incorrect
        return false;
      }
      if (currentWalletSummary != null && currentWalletSummary.isPresent()) {

        WalletPassword walletPassword = new WalletPassword(password, walletId);
        WalletSummary walletSummary = currentWalletSummary.get();
        walletSummary.setWalletPassword(walletPassword);

        CoreServices.getOrCreateHistoryService(walletPassword);

        // Must have succeeded to be here
        CoreServices.logHistory(Languages.safeText(MessageKey.PASSWORD_VERIFIED));

        return true;
      }

    }

    // Must have failed to be here
    log.error("Failed attempt to open wallet - old credentials was incorrect");

    return false;

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

    boolean isPasswordCorrect = !Strings.isNullOrEmpty(
      getPanelModel().get()
        .getEnterPasswordModel()
        .getValue()
    );

    boolean isNewPasswordConfirmed = confirmPasswordMaV.getModel().comparePasswords();

    return isPasswordCorrect && isNewPasswordConfirmed;

  }

}