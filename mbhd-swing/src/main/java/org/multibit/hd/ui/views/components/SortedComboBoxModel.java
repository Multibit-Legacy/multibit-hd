package org.multibit.hd.ui.views.components;


import javax.swing.*;
import java.util.Comparator;

/**
 * <p>ComboBox model to provide the following to combo boxes:</p>
 * <ul>
 * <li>Provision of a continuously updated sort order at runtime</li>
 * </ul>
 *
 * @since 0.0.5
 *  
 */
class SortedComboBoxModel<E> extends DefaultComboBoxModel<E> {

  private final Comparator<E> comparator;

  /**
   * @param comparator The comparator to use
   */
  public SortedComboBoxModel(Comparator<E> comparator) {
    super();
    this.comparator = comparator;
  }

  /**
   * @param items      The initial items
   * @param comparator The comparator for sorting
   */
  public SortedComboBoxModel(E items[], Comparator<E> comparator) {
    this(comparator);

    for (E item : items) {
      addElement(item);
    }
  }

  @Override
  public void addElement(E element) {
    insertElementAt(element, 0);
  }

  @Override
  public void insertElementAt(E element, int index) {
    int size = getSize();

    //  Determine where to insert element to keep model in sorted order

    for (index = 0; index < size; index++) {
      E o = getElementAt(index);

      if (comparator.compare(o, element) > 0) {
        break;
      }
    }

    super.insertElementAt(element, index);

    //  Select an element when it is added to the beginning of the model

    if (index == 0 && element != null) {
      setSelectedItem(element);
    }
  }
}
