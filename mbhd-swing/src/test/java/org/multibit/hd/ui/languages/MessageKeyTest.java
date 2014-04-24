package org.multibit.hd.ui.languages;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

import static org.fest.assertions.Assertions.assertThat;

public class MessageKeyTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @Test
  public void testCoverage_EN() throws Exception {

    for (MessageKey messageKey : MessageKey.values()) {

      assertThat(Languages.safeText(messageKey))
        .describedAs("MessageKey should have this entry in languages.properties")
        .isNotEqualTo(messageKey.getKey());

    }

  }

}
