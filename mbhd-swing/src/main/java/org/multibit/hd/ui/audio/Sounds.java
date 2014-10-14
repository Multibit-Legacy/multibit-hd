package org.multibit.hd.ui.audio;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.spongycastle.util.io.Streams;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of standard sounds</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Sounds {

  private static final Map<String, byte[]> allSounds = Maps.newHashMap();

  /**
   * The name and key of the MP3 file containing the "payment received" sound
   */
  private static final String PAYMENT_RECEIVED = "receive-bitcoin";

  private static final ExecutorService executorService = SafeExecutors.newSingleThreadExecutor("play-received");

  /**
   * Utilities have no public constructor
   */
  private Sounds() {
  }

  public static void initialise() {

    allSounds.put(PAYMENT_RECEIVED, load(PAYMENT_RECEIVED));

  }

  /**
   * Make a standard beep sound (useful for audio feedback of failure)
   */
  public static void playBeep() {

    if (Configurations.currentConfiguration.getSound().isAlertSound()) {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  /**
   * Plays the "payment received" sound in a new thread
   */
  public static void playPaymentReceived() {

    if (Configurations.currentConfiguration.getSound().isReceiveSound()) {

      executorService.submit(new Runnable() {
        @Override
        public void run() {
          play(PAYMENT_RECEIVED);
        }
      });
    }

  }

  /**
   * @param name The name of the sound file (no extension)
   */
  private static void play(String name) {

    Preconditions.checkState(allSounds.containsKey(name), "'" + name + "' must be present (did you initialise?)");

    byte[] sound = allSounds.get(name);

    Player player = null;
    try {
      player = new Player(new ByteArrayInputStream(sound));
      player.play();
    } catch (JavaLayerException e) {
      throw new IllegalStateException(e.getMessage());
    } finally {
      if (player != null) {
        player.close();
      }
    }
  }

  /**
   * @param name The name of the sound file (no extension)
   *
   * @return A byte[] containing the sound
   */
  private static byte[] load(String name) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (InputStream is = Sounds.class.getResourceAsStream("/assets/sounds/" + name + ".mp3")) {

      Streams.pipeAll(is, baos);

    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }

    return baos.toByteArray();
  }

}
