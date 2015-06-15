package org.multibit.hd.ui.views.components.renderers;

import javax.swing.*;
import java.awt.*;


public class ComboBoxRenderer extends javax.swing.plaf.basic.BasicComboBoxRenderer {

  public ComboBoxRenderer() {
    super();
    setOpaque(true);
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      setBackground(Color.RED);

    return this;
  }
}
