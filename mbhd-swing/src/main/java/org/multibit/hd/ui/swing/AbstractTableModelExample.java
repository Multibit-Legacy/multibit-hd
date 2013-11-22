package org.multibit.hd.ui.swing;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.SingleListSelectionAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class AbstractTableModelExample extends JPanel {
  private ArrayListModel arrayListModel;

  public AbstractTableModelExample() {
    super(new BorderLayout());
    DefaultFormBuilder defaultFormBuilder = new DefaultFormBuilder(new FormLayout("p, 2dlu, p:g"));
    defaultFormBuilder.setDefaultDialogBorder();

    this.arrayListModel = new ArrayListModel();
    this.arrayListModel.add(new DisplayTechnology("Swing", "Is a Java API", "Sun"));
    this.arrayListModel.add(new DisplayTechnology("Flash", "Is NOT a Java API", "Macromedia"));
    this.arrayListModel.add(new DisplayTechnology("SWT", "Is a Java API", "Eclipse"));
    this.arrayListModel.add(new DisplayTechnology("QT", "Is NOT a Java API", "Trolltech"));
    this.arrayListModel.add(new DisplayTechnology("AWT", "Is a Java API", "Sun"));

    SelectionInList selectionInList = new SelectionInList((ListModel) this.arrayListModel);

    JList jlist = new JList();
    Bindings.bind(jlist, selectionInList);
    defaultFormBuilder.append("List Model: ", new JScrollPane(jlist));

    JTable table = new JTable(new DisplayTechnologyTableAdapter(selectionInList, new String[]{"Name", "Description",
      "Maker"}));
    table.setSelectionModel(new SingleListSelectionAdapter(
      selectionInList.getSelectionIndexHolder()));
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(table.getPreferredSize());
    defaultFormBuilder.append("Table", scrollPane);

    add(defaultFormBuilder.getPanel());
  }

  private class DisplayTechnology {
    private String name;
    private String description;
    private String maker;

    public DisplayTechnology(String name, String description, String maker) {
      this.name = name;
      this.description = description;
      this.maker = maker;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public String getMaker() {
      return maker;
    }

    public String toString() {
      return name;
    }
  }

  private class DisplayTechnologyTableAdapter extends AbstractTableAdapter {
    public DisplayTechnologyTableAdapter(ListModel listModel, String[] columnNames) {
      super(listModel, columnNames);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      DisplayTechnology displayTechnology = (DisplayTechnology) getRow(rowIndex);
      if (columnIndex == 0) {
        return displayTechnology.getName();
      } else if (columnIndex == 1) {
        return displayTechnology.getDescription();
      } else {
        return displayTechnology.getMaker();
      }
    }
  }

  public static void main(String[] a){
    JFrame f = new JFrame("Abstract TableModel Example");
    f.setDefaultCloseOperation(2);
    f.add(new AbstractTableModelExample());
    f.pack();
    f.setVisible(true);
  }

}