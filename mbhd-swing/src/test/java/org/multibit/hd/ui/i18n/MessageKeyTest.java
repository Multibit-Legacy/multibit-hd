package org.multibit.hd.ui.i18n;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MessageKeyTest {

  @Test
  public void testCoverage_EN() throws Exception {

    for (MessageKey messageKey: MessageKey.values()) {

      assertThat(Languages.safeText(messageKey)).isNotEqualTo(messageKey.getKey());

    }

  }
}
