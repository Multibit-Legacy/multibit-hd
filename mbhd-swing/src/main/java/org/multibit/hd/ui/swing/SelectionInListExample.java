package org.multibit.hd.ui.swing;

import com.google.common.collect.Lists;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import org.multibit.hd.ui.fonts.AwesomeDecorator;
import org.multibit.hd.ui.fonts.AwesomeIcon;

import javax.swing.*;
import java.util.ArrayList;

public class SelectionInListExample extends JPanel {
  public SelectionInListExample() {

    ArrayList<String> strings = Lists.newArrayList();
    strings.add("Swing");
    strings.add("SWT");
    strings.add("HTML");
    strings.add("Flash");
    strings.add("QT");

    SelectionInList selectionInList = new SelectionInList<>(strings);
    DefaultFormBuilder defaultFormBuilder = new DefaultFormBuilder(new FormLayout("p, 2dlu, p:g"));
    defaultFormBuilder.border(Borders.DIALOG);

    JLabel label = AwesomeDecorator.createIconLabel(AwesomeIcon.GLOBE, "Hello!");

    defaultFormBuilder.append("Globe: ", label);

    JList jlist = new JList();
    Bindings.bind(jlist, selectionInList);

    defaultFormBuilder.append("JList: ", new JScrollPane(jlist));
    defaultFormBuilder.append("Selected String: ", BasicComponentFactory.createTextField(selectionInList.getSelectionHolder()));

    add(defaultFormBuilder.getPanel());
  }

  public static void main(String[] a){
    JFrame f = new JFrame("Selection In List Example");
    f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    f.add(new SelectionInListExample());
    f.pack();
    f.setVisible(true);
  }
}
