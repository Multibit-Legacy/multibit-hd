package org.multibit.hd.core.config;

import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.testing.FixtureAsserts;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.fest.assertions.Assertions.assertThat;

public class ConfigurationsTest {

  @SuppressFBWarnings({"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD"})
  @Before
  public void setUp() {

    InstallationManager.unrestricted = true;

  }

  @SuppressFBWarnings({"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD"})
  @After
  public void tearDown() throws Exception {

    InstallationManager.unrestricted = false;

  }

  @Test
  public void testReadConfiguration_ExampleWithUnknown() throws Exception {

    InputStream is = ConfigurationsTest.class.getResourceAsStream("/fixtures/example-configuration.yaml");

    Optional<Configuration> configuration = Yaml.readYaml(is, Configuration.class);

    assertThat(configuration.isPresent()).isTrue();
    assertThat(configuration.get().getAppearance().getCurrentScreen()).isEqualTo("TOOLS");

    assertThat(configuration.get().any().isEmpty()).isFalse();
  }

  @SuppressFBWarnings({"DM_DEFAULT_ENCODING"})
  @Test
  public void testWriteConfiguration_ExampleWithUnknown() throws Exception {

    InputStream is = ConfigurationsTest.class.getResourceAsStream("/fixtures/example-configuration.yaml");

    Optional<Configuration> configuration = Yaml.readYaml(is, Configuration.class);

    assertThat(configuration.isPresent()).isTrue();
    assertThat(configuration.get().getAppearance().getCurrentScreen()).isEqualTo("TOOLS");
    assertThat(configuration.get().isTrezor()).isTrue();

    assertThat(configuration.get().any().isEmpty()).isFalse();

    // Write to a byte array to prevent overwriting the local settings
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Yaml.writeYaml(baos, configuration.get());

    FixtureAsserts.assertStringMatchesNormalisedStringFixture(
      "Writing out fields does not match original fixture",
      baos.toString(),
      "/fixtures/example-configuration.yaml"
    );


  }

}
