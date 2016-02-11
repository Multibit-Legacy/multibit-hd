package org.multibit.hd.core.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.Maps;

import java.util.Map;

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

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Any unknown objects in the configuration go here (preserve order of insertion)
   */
  private Map<String, Object> other = Maps.newLinkedHashMap();

  /**
   * @return The map of any unknown objects in the configuration at this level
   */
  @JsonAnyGetter
  public Map<String, Object> any() {
    return other;
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    other.put(name, value);
  }

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

    // Unknown properties
    for (Map.Entry<String, Object> entry : any().entrySet()) {
      configuration.set(entry.getKey(), entry.getValue());
    }

    // Known properties
    configuration.setAlertSound(isAlertSound());
    configuration.setReceiveSound(isReceiveSound());

    return configuration;
  }
}
