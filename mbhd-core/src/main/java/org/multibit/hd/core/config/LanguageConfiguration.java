package org.multibit.hd.core.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Locale;
import java.util.Map;

/**
 * <p>Configuration to provide the following to logging framework:</p>
 * <ul>
 * <li>Configuration of internationalisation</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class LanguageConfiguration {

  /**
   * Default to US since it is the dominant locale on the Internet
   */
  @JsonProperty
  private Locale locale = Locale.US;

  /**
   * Default constructor for Jackson
   */
  public LanguageConfiguration() {}

  /**
   * @param locale The locale on which to set defaults
   */
  public LanguageConfiguration(Locale locale) {
    this.locale = locale;

  }

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
   * @return A deep copy of this object
   */
  public LanguageConfiguration deepCopy() {

    LanguageConfiguration configuration = new LanguageConfiguration();

    // Unknown properties
    for (Map.Entry<String, Object> entry : any().entrySet()) {
      configuration.set(entry.getKey(), entry.getValue());
    }

    // Known properties
    configuration.setLocale(getLocale());

    return configuration;

  }

  /**
   * @return The locale
   */
  public Locale getLocale() {
    return locale;
  }

  @JsonIgnore
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * @param value The string representation of the locale (e.g. "en_GB" etc)
   */
  public void setLocale(String value) {

    Preconditions.checkNotNull(value, "'value' must be present");

    String[] parameters = value.split("_");

    Preconditions.checkState(parameters.length > 0, "'value' must not be empty");

    final Locale newLocale;

    switch (parameters.length) {
      case 1:
        newLocale = new Locale(parameters[0]);
        break;
      case 2:
        newLocale = new Locale(parameters[0], parameters[1]);
        break;
      case 3:
        newLocale = new Locale(parameters[0], parameters[1], parameters[2]);
        break;
      default:
        throw new IllegalArgumentException("Unknown locale descriptor: " + value);
    }

    setLocale(newLocale);

  }
}
