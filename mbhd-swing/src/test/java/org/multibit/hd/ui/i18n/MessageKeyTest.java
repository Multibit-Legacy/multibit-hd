package org.multibit.hd.ui.i18n;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

public class MessageKeyTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @Test
  public void testCoverage_EN() throws Exception {

    System.out.println("NOT RUNNING MESSAGEKEYTEST");
    System.out.println("NOT RUNNING MESSAGEKEYTEST");
    System.out.println("NOT RUNNING MESSAGEKEYTEST");
    for (MessageKey messageKey : MessageKey.values()) {

//      assertThat(Languages.safeText(messageKey)).isNotEqualTo(messageKey.getKey());

    }

  }
}
