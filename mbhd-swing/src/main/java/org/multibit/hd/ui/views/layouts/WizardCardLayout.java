package org.multibit.hd.ui.views.layouts;

import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;

/**
 * <p>Card layout to provide the following to Wizard API:</p>
 * <ul>
 * <li>Set focus on the card</li>
 * <li>Track the current card</li>
 * <li>Handle next and previous actions</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class WizardCardLayout extends CardLayout implements HierarchyListener {

  private List<JComponent> cards = Lists.newArrayList();
  private JComponent firstCard;
  private JComponent lastCard;
  private JComponent currentCard;

  /**
   * @param hgap The horizontal gap between components
   * @param vgap The vertical gap between components
   */
  public WizardCardLayout(int hgap, int vgap) {

    setHgap(hgap);
    setVgap(vgap);
  }

  @Override
  public void addLayoutComponent(Component comp, Object constraints) {
    super.addLayoutComponent(comp, constraints);

    if (!(comp instanceof JComponent)) return;

    JComponent component = (JComponent) comp;
    cards.add(component);

    if (firstCard == null)
      firstCard = component;

    lastCard = component;

  }

  @Override
  public void removeLayoutComponent(Component comp) {
    super.removeLayoutComponent(comp);

    if (!(comp instanceof JComponent)) return;

    JComponent component = (JComponent) comp;

    cards.remove(component);

    if (component.equals(firstCard)
      && cards.size() > 0) {
      firstCard = cards.get(0);
    }

    if (component.equals(lastCard)
      && cards.size() > 0) {
      lastCard = cards.get(cards.size() - 1);
    }

  }

  /**
   * @return The currently selected card
   */
  public JComponent getCurrentCard() {
    return currentCard;
  }

  /**
   * @return True if there is another card in the layout before wrapping around in the forward direction
   */
  public boolean hasNext() {
    return currentCard != lastCard;
  }

  /**
   * @return True if there is another card in the layout before wrapping around in the reverse direction
   */
  public boolean hasPrevious() {
    return currentCard != firstCard;
  }

  /**
   * <p>Keeps track of the current card</p>
   *
   * @param e The event
   */
  @Override
  public void hierarchyChanged(HierarchyEvent e) {
    JComponent component = (JComponent) e.getSource();

    if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) != 0
      && component.isShowing()) {
      currentCard = component;

    }
  }

}