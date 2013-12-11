package org.multibit.hd.ui.audio;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;


public class MP3 {
  private String filename;
  private Player player;

  // constructor that takes the name of an MP3 file
  public MP3(String filename) {
    this.filename = filename;
  }

  public void close() { if (player != null) player.close(); }

  // play the MP3 file to the sound card
  public void play() {
    try {
      InputStream fis     = MP3.class.getResourceAsStream("/assets/sounds/receive-bitcoin.mp3");
      BufferedInputStream bis = new BufferedInputStream(fis);
      player = new Player(bis);
    }
    catch (Exception e) {
      System.out.println("Problem playing file " + filename);
      System.out.println(e);
    }

    // run in new thread to play in background
    new Thread() {
      public void run() {
        try { player.play(); }
        catch (Exception e) { System.out.println(e); }
      }
    }.start();




  }


  // test client
  public static void main(String[] args) {
    MP3 mp3 = new MP3("");
    mp3.play();

    // do whatever computation you like, while music plays
    int N = 4000;
    double sum = 0.0;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        sum += Math.sin(i + j);
      }
    }
    System.out.println(sum);

    // when the computation is done, stop playing it
    mp3.close();

    // play from the beginning
    mp3 = new MP3("");
    mp3.play();

  }

}