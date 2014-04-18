package org.multibit.hd.ui.languages;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.config.Configurations;

import static org.fest.assertions.Assertions.assertThat;

public class CoreMessageKeyTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @Test
  public void testCoverage_EN() throws Exception {

    for (CoreMessageKey messageKey: CoreMessageKey.values()) {

      assertThat(Languages.safeText(messageKey)).isNotEqualTo(messageKey.getKey());

    }

  }
}
