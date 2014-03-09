package org.multibit.hd.ui.audio;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;

public class SoundsTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = new Configuration();

  }

  @Test
  public void testPlayBeep() throws Exception {

    Sounds.playBeep();

  }

  @Test
  public void testPlayReceiveBitcoin() throws Exception {

    Sounds.initialise();
    Sounds.playReceiveBitcoin();

  }
}
