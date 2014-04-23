package org.multibit.hd.ui.languages;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

import java.util.ResourceBundle;

import static org.fest.assertions.Assertions.assertThat;

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

  @Ignore
  public void testSimilar_EN() throws Exception {

    ResourceBundle base = Languages.currentResourceBundle();

    for (MessageKey messageKey : MessageKey.values()) {

      String content =Languages.safeText(messageKey);

      for (String otherKey: base.keySet()) {

        if (messageKey.getKey().equals(otherKey)) {
          continue;
        }

        String otherContent = base.getString(otherKey);

        if (otherContent.contains(content)) {
          System.out.printf("MessageKey: '%s' ('%s') is similar to resource key '%s' ('%s'))%n", messageKey.getKey(), content, otherKey, otherContent);
        }

      }


    }

  }

}
