package org.multibit.hd.ui.views.themes;

import java.awt.*;

/**
 * <p>Strategy to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class LightTheme implements Theme {

  @Override
  public Color applicationBackground() {
    return new Color(0xcccccc);
  }

  @Override
  public Color panelBackground() {
    return new Color(0x909090);
  }

  @Override
  public Color text() {
    return new Color(6,6,6);
  }

  @Override
  public Color lightText() {
    return new Color(96,96,96);
  }

  @Override
  public Color dangerBackground() {
    return new Color(0xf2dede);
  }

  @Override
  public Color dangerBorder() {
    return new Color(0xeed3d7);
  }

  @Override
  public Color dangerText() {
    return new Color(0xb94a48);
  }

  @Override
  public Color warningBackground() {
    return new Color(0xfcf8e3);
  }

  @Override
  public Color warningBorder() {
    return new Color(0xfbeed5);
  }

  @Override
  public Color warningText() {
    return text();
  }

  @Override
  public Color successBackground() {
    return new Color(0xdff0d8);
  }

  @Override
  public Color successBorder() {
    return new Color(0xd6e9c6);
  }

  @Override
  public Color successText() {
    return new Color(0x468847);
  }

  @Override
  public Color infoBackground() {
    return new Color(0xd9edf7);
  }

  @Override
  public Color infoBorder() {
    return new Color(0xbce8f1);
  }

  @Override
  public Color infoText() {
    return new Color(0x3a87ad);
  }

}
