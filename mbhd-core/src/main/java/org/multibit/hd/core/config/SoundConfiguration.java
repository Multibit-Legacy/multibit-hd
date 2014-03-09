package org.multibit.hd.core.config;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of sound effects</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SoundConfiguration {

  private boolean alertSound = true;

  private boolean receiveSound = true;

  /**
   * @return True if a sound should be played for info and danger alerts
   */
  public boolean isAlertSound() {
    return alertSound;
  }

  public void setAlertSound(boolean alertSound) {
    this.alertSound = alertSound;
  }

  /**
   * @return True if a sound should be played when bitcoins get first confirmation
   */
  public boolean isReceiveSound() {
    return receiveSound;
  }

  public void setReceiveSound(boolean receiveSound) {
    this.receiveSound = receiveSound;
  }

  /**
   * @return A deep copy of this object
   */
  public SoundConfiguration deepCopy() {

    SoundConfiguration configuration = new SoundConfiguration();

    configuration.setAlertSound(isAlertSound());
    configuration.setReceiveSound(isReceiveSound());

    return configuration;
  }
}
