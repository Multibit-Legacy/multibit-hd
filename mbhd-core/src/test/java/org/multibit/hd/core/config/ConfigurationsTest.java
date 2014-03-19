package org.multibit.hd.core.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;

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
