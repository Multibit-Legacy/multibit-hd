package org.multibit.hd.core.config;

import ch.qos.logback.classic.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.multibit.hd.core.config.Configurations.*;

public class ConfigurationsTest {

  private File propertiesFile = new File("test-properties.properties");

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {

    if (propertiesFile.exists()) {
      assertThat(propertiesFile.delete()).isTrue();
    }

  }

  @Test
  public void testDefaultConfiguration() throws Exception {

    Configuration configuration = Configurations.newDefaultConfiguration();

    // Bitcoin
    assertThat(configuration.getBitcoinConfiguration().getBitcoinSymbol()).isEqualTo("MICON");

    // Internationalisation
    assertThat(configuration.getLocale().getLanguage()).isEqualTo("en");
    assertThat(configuration.getI18NConfiguration().getDecimalSeparator()).isEqualTo('.');
    assertThat(configuration.getI18NConfiguration().getGroupingSeparator()).isEqualTo(',');
    assertThat(configuration.getI18NConfiguration().isCurrencySymbolLeading()).isTrue();

    // Logging
    assertThat(configuration.getLoggingConfiguration().getLoggers().get("org.multibit")).isEqualTo(Level.DEBUG);

  }

  @Test
  public void testDefaultProperties() throws Exception {

    Configuration configuration = Configurations.newDefaultConfiguration();

    Properties properties = new ConfigurationWriteAdapter(configuration).adapt();

    // Bitcoin
    assertThat(properties.get(BITCOIN_SYMBOL)).isEqualTo("MICON");

    // Internationalisation
    assertThat(properties.get(I18N_LOCALE).toString()).isEqualTo("en_GB");
    assertThat(properties.get(I18N_DECIMAL_SEPARATOR)).isEqualTo('.');
    assertThat(properties.get(I18N_GROUPING_SEPARATOR)).isEqualTo(',');
    assertThat(properties.get(I18N_IS_CURRENCY_PREFIXED)).isEqualTo("true");

    // Logging
    assertThat(properties.get(LOGGING_PACKAGE_PREFIX + "org.multibit")).isEqualTo("DEBUG");

  }

  @Test
  public void testReadProperties_New() throws Exception {

    Properties properties = Configurations.readProperties(propertiesFile);
    assertThat(properties.isEmpty()).isTrue();

  }

  @Test
  public void testWriteProperties_NewToExisting() throws Exception {

    // Create an existing properties file
    Properties existingProperties = Configurations.readProperties(propertiesFile);
    assertThat(existingProperties.isEmpty()).isTrue();

    existingProperties.put("test1.subtest1", "test1");

    Configurations.writeProperties(propertiesFile, existingProperties);

    // Verify that the write was successful by repeating the read
    Properties updatedProperties = Configurations.readProperties(propertiesFile);
    assertThat(updatedProperties.size()).isEqualTo(1);

    updatedProperties.put("test2.subtest2","test2");

    Configurations.writeProperties(propertiesFile, updatedProperties);

    // Verify that the write was successful by repeating the read
    Properties updatedProperties2 = Configurations.readProperties(propertiesFile);
    assertThat(updatedProperties2.size()).isEqualTo(2);

  }

  @Test
  public void testMergeProperties() throws Exception {

    // Arrange

    // Create a superset
    Properties superset = new Properties();
    superset.put("test1.subtest1", "test1");
    superset.put("test2.subtest2", "test2");
    superset.put("test3.subtest3", "test3");

    // Create a subset
    Properties subset = new Properties();
    superset.put("test1.subtest1", "test1-new");
    superset.put("test4.subtest4", "test4-new");

    // Act
    Configurations.mergeProperties(subset, superset);

    // Assert
    assertThat((String) superset.get("test1.subtest1")).isEqualTo("test1-new");
    assertThat((String) superset.get("test2.subtest2")).isEqualTo("test2");
    assertThat((String) superset.get("test3.subtest3")).isEqualTo("test3");
    assertThat((String) superset.get("test4.subtest4")).isEqualTo("test4-new");

  }

}
