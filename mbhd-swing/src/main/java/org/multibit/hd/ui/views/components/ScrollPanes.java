package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;

/**
 * <p>Decorator to provide the following to UI:</p>
 * <ul>
 * <li>Apply a scroll pane to the given components</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ScrollPanes {

  /**
   * Utilities have no public constructor
   */
  private ScrollPanes() {
  }

  /**
   * <p>Create a new scroll pane to wrap the given component using the read only theme</p>
   *
   * @param component The component to be wrapped in a scroll pane
   */
  public static JScrollPane newReadOnlyScrollPane(final JComponent component) {

    // Remove the border from the component
    component.setBorder(null);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setOpaque(true);
    scrollPane.setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setBorder(null);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    scrollPane.setViewportView(component);
    scrollPane.getViewport().setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));

    ScrollBarUIDecorator.apply(scrollPane, false);

    return scrollPane;

  }

  /**
   * <p>Create a new scroll pane to wrap the given component using the data entry theme</p>
   *
   * @param component The component to be wrapped in a scroll pane
   */
  public static JScrollPane newDataEntryScrollPane(final JComponent component) {

    // Remove the border from the component
    component.setBorder(null);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setOpaque(true);
    scrollPane.setBackground(Themes.currentTheme.dataEntryBackground());
    scrollPane.setBorder(null);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    scrollPane.setViewportView(component);
    scrollPane.getViewport().setBackground(Themes.currentTheme.detailPanelBackground());
    scrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));

    ScrollBarUIDecorator.apply(scrollPane, false);

    return scrollPane;

  }

}