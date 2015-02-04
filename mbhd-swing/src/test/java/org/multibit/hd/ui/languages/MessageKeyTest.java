package org.multibit.hd.ui.languages;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.Assertions.assertThat;

public class MessageKeyTest {

  private static final Logger log = LoggerFactory.getLogger(MessageKeyTest.class);

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @Test
  public void testCoverage_EN() throws Exception {

    boolean failed = false;

    for (MessageKey messageKey : MessageKey.values()) {

      if (Languages.safeText(messageKey).equals(messageKey.getKey())) {

        failed = true;

        // Check for {} instead of {0} etc
        log.error("FAILED - MessageKey.{} is not resolved in languages.properties for locale {}", messageKey.name(), Languages.currentLocale());

      }

    }

    assertThat(failed).isFalse();

  }

}
