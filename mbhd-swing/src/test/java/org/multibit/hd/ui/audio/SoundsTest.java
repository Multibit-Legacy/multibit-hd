package org.multibit.hd.ui.audio;

import org.junit.Test;

public class SoundsTest {

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
