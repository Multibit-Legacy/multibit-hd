package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.events.SecurityEvent;
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
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_wallet.SelectWalletModel;
import org.multibit.hd.ui.views.components.select_wallet.SelectWalletView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Password: Enter password</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PasswordEnterPasswordPanelView extends AbstractWizardPanelView<PasswordWizardModel, PasswordEnterPasswordPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(PasswordEnterPasswordPanelView.class);

  // Panel specific components
  private ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> displaySecurityPopoverMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;
  private ModelAndView<SelectWalletModel, SelectWalletView> selectWalletMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public PasswordEnterPasswordPanelView(AbstractWizard<PasswordWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PASSWORD_TITLE, AwesomeIcon.LOCK);

  }

  @Override
  public void newPanelModel() {

    displaySecurityPopoverMaV = Popovers.newDisplaySecurityPopoverMaV(getPanelName());
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());
    enterPasswordMaV.getView().setAddLabel(false);
    selectWalletMaV = Components.newSelectWalletMaV(getPanelName());

    // Configure the panel model
    final PasswordEnterPasswordPanelModel panelModel = new PasswordEnterPasswordPanelModel(
      getPanelName(),
      enterPasswordMaV.getModel(),
      selectWalletMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterPasswordPanelModel(panelModel);

    // Register components
    registerComponents(displaySecurityPopoverMaV, enterPasswordMaV, selectWalletMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXLayout(),
      "[]", // Column constraints
      "[]0[]32[]0[]32[]" // Row constraints
    ));

    contentPanel.add(Labels.newPasswordNote(), "wrap");
    contentPanel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");

    contentPanel.add(Labels.newSelectWalletNote(), "wrap");
    contentPanel.add(selectWalletMaV.getView().newComponentPanel(), "wrap");

    contentPanel.add(Labels.newRestoreWalletNote(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PasswordWizardModel> wizard) {

    PanelDecorator.addExitCancelRestoreUnlock(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      false
    );

  }

  @Override
  public boolean beforeShow() {

    // TODO (GR) Combine this into a single method on WalletManager
    List<File> walletDirectories = WalletManager.findWalletDirectories(InstallationManager.getOrCreateApplicationDataDirectory());
    Optional<String> walletRoot = WalletManager.INSTANCE.getCurrentWalletRoot();
    List<WalletSummary> wallets = WalletManager.findWalletSummaries(walletDirectories, walletRoot);

    selectWalletMaV.getModel().setWalletList(wallets);
    selectWalletMaV.getView().setEnabled(true);

    return true;
  }

  @Override
  public void afterShow() {

    registerDefaultButton(getFinishButton());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        enterPasswordMaV.getView().requestInitialFocus();

        // Check for any security alerts
        Optional<SecurityEvent> securityEvent = CoreServices.getApplicationEventService().getLatestSecurityEvent();
        if (securityEvent.isPresent()) {

          displaySecurityPopoverMaV.getModel().setValue(securityEvent.get());

          // Show the security alert as a popover
          Panels.showLightBoxPopover(displaySecurityPopoverMaV.getView().newComponentPanel());

        }

        selectWalletMaV.getView().updateViewFromModel();

      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Don't block an exit
    if (isExitCancel) {
      return true;
    }

    // Start the spinner (we are deferring the hide)
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Ensure the view shows the spinner and disables components
        getFinishButton().setEnabled(false);
        getExitButton().setEnabled(false);
        getRestoreButton().setEnabled(false);

        enterPasswordMaV.getView().setSpinnerVisibility(true);
        selectWalletMaV.getView().setEnabled(false);

      }
    });

    // Check the password (might take a while so do it asynchronously while showing a spinner)
    // Tar pit (must be in a separate thread to ensure UI updates)
    ListenableFuture<Boolean> passwordFuture = SafeExecutors.newSingleThreadExecutor("check-password").submit(new Callable<Boolean>() {

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

            // Maintain the spinner while the initialisation continues

            // Manually deregister the MaVs
            CoreServices.uiEventBus.unregister(displaySecurityPopoverMaV);
            CoreServices.uiEventBus.unregister(enterPasswordMaV);
            CoreServices.uiEventBus.unregister(selectWalletMaV);

            // Trigger the deferred hide
            ViewEvents.fireWizardDeferredHideEvent(getPanelName(), false);

          } else {

            // Wait just long enough to be annoying (anything below 2 seconds is comfortable)
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

            // Failed
            Sounds.playBeep();

            // Ensure the view hides the spinner and enables components
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {

                enterPasswordMaV.getView().incorrectPassword();

                getFinishButton().setEnabled(true);
                getExitButton().setEnabled(true);
                getRestoreButton().setEnabled(true);
                enterPasswordMaV.getView().setSpinnerVisibility(false);

                enterPasswordMaV.getView().requestInitialFocus();
                selectWalletMaV.getView().setEnabled(true);

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

              getFinishButton().setEnabled(true);
              getExitButton().setEnabled(true);
              getRestoreButton().setEnabled(true);
              enterPasswordMaV.getView().setSpinnerVisibility(false);

              enterPasswordMaV.getView().requestInitialFocus();
              selectWalletMaV.getView().setEnabled(true);
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
   * @return True if the selected wallet can be opened with the given password
   */
  private boolean checkPassword() {

    CharSequence password = enterPasswordMaV.getModel().getValue();

    if (!"".equals(password)) {
      // Attempt to open the wallet
      WalletId walletId = selectWalletMaV.getModel().getValue().getWalletId();
      try {
        WalletManager.INSTANCE.open(InstallationManager.getOrCreateApplicationDataDirectory(), walletId, password);
      } catch (WalletLoadException wle) {
        // Mostly this will be from a bad 
        log.error(wle.getMessage());
        // Assume bad password
        return false;
      }
      Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
      if (currentWalletSummary.isPresent()) {

        // Store this wallet in the current configuration
        String walletRoot = WalletManager.createWalletRoot(walletId);
        Configurations.currentConfiguration.getWallet().setCurrentWalletRoot(walletRoot);

        // Update the wallet data
        WalletSummary walletSummary = currentWalletSummary.get();
        walletSummary.setPassword(password);

        // Create the history service
        CoreServices.getOrCreateHistoryService(walletSummary.getWalletId());

        // Must have succeeded to be here
        CoreServices.logHistory(Languages.safeText(MessageKey.PASSWORD_VERIFIED));

        return true;
      }

    }

    // Must have failed to be here
    log.error("Failed attempt to open wallet");

    return false;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      isFinishEnabled()
    );

  }

  /**
   * @return True if the "finish" button should be enabled
   */
  private boolean isFinishEnabled() {

    return !Strings.isNullOrEmpty(
      getPanelModel().get()
        .getEnterPasswordModel()
        .getValue()
    );

  }

}