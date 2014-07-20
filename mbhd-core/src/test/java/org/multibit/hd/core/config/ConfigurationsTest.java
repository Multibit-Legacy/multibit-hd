package org.multibit.hd.core.config;

import com.google.common.base.Optional;
import org.junit.Ignore;
import org.multibit.hd.core.testing.FixtureAsserts;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.fest.assertions.Assertions.assertThat;

public class ConfigurationsTest {

  @Ignore
  public void testReadConfiguration_ExampleWithUnknown() throws Exception {

    InputStream is = ConfigurationsTest.class.getResourceAsStream("/fixtures/example-configuration.yaml");

    Optional<Configuration> configuration = Configurations.readYaml(is, Configuration.class);

    assertThat(configuration.isPresent()).isTrue();
    assertThat(configuration.get().getAppearance().getCurrentScreen()).isEqualTo("TOOLS");

    assertThat(configuration.get().any().isEmpty()).isFalse();
  }

  @Ignore
  public void testWriteConfiguration_ExampleWithUnknown() throws Exception {

    InputStream is = ConfigurationsTest.class.getResourceAsStream("/fixtures/example-configuration.yaml");

    Optional<Configuration> configuration = Configurations.readYaml(is, Configuration.class);

    assertThat(configuration.isPresent()).isTrue();
    assertThat(configuration.get().getAppearance().getCurrentScreen()).isEqualTo("TOOLS");

    assertThat(configuration.get().any().isEmpty()).isFalse();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Configurations.writeYaml(baos, configuration.get());

    FixtureAsserts.assertStringMatchesNormalisedStringFixture(
      "Writing out fields does not match original fixture",
      baos.toString(),
      "/fixtures/example-configuration.yaml"
    );


  }

}
