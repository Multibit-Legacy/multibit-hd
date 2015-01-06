package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;


/**
 * <p>Abstract base class to provide the following to UI:</p>
 * <ul>
 * <li>Provision of common methods to wizards that support hardware wallet operations</li>
 * </ul>
 *
 * @param <M> the hardware wallet wizard model
 *
 * @since 0.0.1
 */
public abstract class AbstractHardwareWalletWizard<M extends AbstractHardwareWalletWizardModel> extends AbstractWizard<M> {

  private static final Logger log = LoggerFactory.getLogger(AbstractHardwareWalletWizard.class);

  /**
   * @param wizardModel     The overall wizard data model containing the aggregate information of all components in the wizard
   * @param isExiting       True if the exit button should trigger an application shutdown
   * @param wizardParameter An optional parameter that can be referenced during construction
   */
  protected AbstractHardwareWalletWizard(M wizardModel, boolean isExiting, Optional wizardParameter) {
    super(wizardModel, isExiting, wizardParameter);

    // All hardware wallet wizards can receive hardware wallet events
    HardwareWalletService.hardwareWalletEventBus.register(this);

  }

  /**
   * <p>Inform the wizard model of a "device failed"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleDeviceFailed(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "device failed" state
          getWizardModel().showDeviceFailed(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "device ready"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleDeviceReady(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          String oldPanel = getWizardModel().getPanelName();

          // Move to the "device ready" state
          getWizardModel().showDeviceReady(event);

          // Show the panel if different
          String newPanel = getWizardModel().getPanelName();
          if (oldPanel == null || !oldPanel.equals(newPanel)) {
            show(getWizardModel().getPanelName());
          }

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "device detached"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleDeviceDetached(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "device detached" state
          getWizardModel().showDeviceDetached(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "device stopped"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleDeviceStopped(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "device stopped" state
          getWizardModel().showDeviceStopped(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "PIN entry"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handlePINEntry(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "PIN entry" state
          getWizardModel().showPINEntry(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "button press"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleButtonPress(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            log.debug("Wizard panel name {}", getWizardModel().getPanelName());
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "button press" state
          getWizardModel().showButtonPress(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of an "operation succeeded"</p>
   *
   * @param event The originating event containing payload and context
   */
  private void handleOperationSucceeded(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "operation succeeded" state
          getWizardModel().showOperationSucceeded(event);

          // Show the panel
          try {
            show(getWizardModel().getPanelName());
          } catch (IllegalStateException ise) {
            // Carry on
            log.debug(ise.getMessage());
          }

        }
      });
  }

  /**
   * <p>Inform the wizard model of an "operation failed"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleOperationFailed(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "operation failed" state
          getWizardModel().showOperationFailed(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "provide entropy"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleProvideEntropy(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());

          // Move to the "operation failed" state
          getWizardModel().showProvideEntropy(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "received address"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleReceivedAddress(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());

          // Move to the "received address" state
          getWizardModel().receivedAddress(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "received public key"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleReceivedPublicKey(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "received public key" state
          getWizardModel().receivedPublicKey(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "received deterministic hierarchy"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleReceivedDeterministicHierarchy(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          if (getWizardModel().getPanelName() != null) {
            if (getWizardPanelView(getWizardModel().getPanelName()) != null) {
              getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());
            }
          }

          // Move to the "received deterministic hierarchy" state
          getWizardModel().receivedDeterministicHierarchy(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Inform the wizard model of a "received message signature"</p>
   *
   * @param event The originating event containing payload and context
   */
  public void handleReceivedMessageSignature(final HardwareWalletEvent event) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          // Ensure the panel updates its model (the button is outside of the panel itself)
          getWizardPanelView(getWizardModel().getPanelName()).updateFromComponentModels(Optional.absent());

          // Move to the "received message signature" state
          getWizardModel().receivedMessageSignature(event);

          // Show the panel
          show(getWizardModel().getPanelName());

        }
      });

  }

  /**
   * <p>Respond to hardware wallet events. Not SHOW_DEVICE_* since the MainController handles them.</p>
   * <p>Translate the hardware event into an action on the EDT for easier integration with the existing
   * framework</p>
   *
   * @param event The hardware wallet event indicating a state change
   */
  @Subscribe
  public void onHardwareWalletEvent(HardwareWalletEvent event) {

    log.debug("Received hardware event: '{}'.", event.getEventType().name());

    if (!Dates.nowUtc().isAfter(getWizardModel().getIgnoreHardwareWalletEventsThreshold())) {
      log.debug("Ignoring device event due to 'ignore threshold' still in force", event);
      return;
    }

    switch (event.getEventType()) {
      case SHOW_DEVICE_FAILED:
        handleDeviceFailed(event);
        break;
      case SHOW_DEVICE_READY:
        handleDeviceReady(event);
        break;
      case SHOW_DEVICE_DETACHED:
        handleDeviceDetached(event);
        break;
      case SHOW_DEVICE_STOPPED:
        handleDeviceStopped(event);
        break;
      case SHOW_PIN_ENTRY:
        handlePINEntry(event);
        break;
      case SHOW_BUTTON_PRESS:
        handleButtonPress(event);
        break;
      case SHOW_OPERATION_SUCCEEDED:
        handleOperationSucceeded(event);
        break;
      case SHOW_OPERATION_FAILED:
        handleOperationFailed(event);
        break;
      case PROVIDE_ENTROPY:
        handleProvideEntropy(event);
        break;
      case ADDRESS:
        handleReceivedAddress(event);
        break;
      case PUBLIC_KEY:
        handleReceivedPublicKey(event);
        break;
      case DETERMINISTIC_HIERARCHY:
        handleReceivedDeterministicHierarchy(event);
        break;
      case MESSAGE_SIGNATURE:
        handleReceivedMessageSignature(event);
        break;
      case SHOW_WORD_ENTRY:
        break;
      default:
        log.warn("Unknown hardware wallet event type: {}", event.getEventType().name());
        break;
    }
  }

}
