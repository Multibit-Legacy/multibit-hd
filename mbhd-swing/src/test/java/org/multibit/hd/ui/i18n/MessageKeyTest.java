package org.multibit.hd.ui.i18n;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

import static org.fest.assertions.api.Assertions.assertThat;

public class MessageKeyTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @Test
  public void testCoverage_EN() throws Exception {

    for (MessageKey messageKey : MessageKey.values()) {

      assertThat(Languages.safeText(messageKey)).isNotEqualTo(messageKey.getKey());

    }

  }
}
