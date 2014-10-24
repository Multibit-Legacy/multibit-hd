package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.ClipboardUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <p>Decorator to provide the following to application:</p>
 * <ul>
 * <li>Standard technique for applying FEST and Accessibility API information</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class AccessibilityDecorator {

  private static final String MIDDLE_CLICK_LISTENER = "Middle click listener";

  /**
   * <p>Full FEST and Accessibility support (tooltip and description)</p>
   *
   * @param component  The Swing component to decorate
   * @param nameKey    The component name (used directly for FEST and with lookup for accessible name)
   * @param tooltipKey The component tooltip and accessible description
   */
  public static void apply(JComponent component, MessageKey nameKey, MessageKey tooltipKey) {

    // Ensure FEST can find it
    component.setName(nameKey.getKey());

    // Ensure we have a suitable tooltip
    component.setToolTipText(Languages.safeText(tooltipKey));

    // Ensure Accessibility API can find it
    component.getAccessibleContext().setAccessibleName(Languages.safeText(nameKey));
    component.getAccessibleContext().setAccessibleDescription(Languages.safeText(tooltipKey));

    applyMiddleClickPaste(component);

  }

  /**
   * <p>Basic FEST and Accessibility support (no tooltip or description)</p>
   *
   * @param component The Swing component to decorate
   * @param nameKey   The component name (used directly for FEST and with lookup for accessible name)
   */
  public static void apply(JComponent component, MessageKey nameKey) {

    // Ensure FEST can find it
    component.setName(nameKey.getKey());

    // Ensure Accessibility API can find it
    component.getAccessibleContext().setAccessibleName(Languages.safeText(nameKey));

    applyMiddleClickPaste(component);

  }

  /**
   * <p>Full FEST and Accessibility support (tooltip and description)</p>
   *
   * @param component  The Swing component to decorate
   * @param nameKey    The component name (used directly for FEST and with lookup for accessible name)
   * @param tooltipKey The component tooltip and accessible description
   */
  public static void apply(JComponent component, CoreMessageKey nameKey, CoreMessageKey tooltipKey) {

    // Ensure FEST can find it
    component.setName(nameKey.getKey());

    // Ensure we have a suitable tooltip
    component.setToolTipText(Languages.safeText(tooltipKey));

    // Ensure Accessibility API can find it
    component.getAccessibleContext().setAccessibleName(Languages.safeText(nameKey));
    component.getAccessibleContext().setAccessibleDescription(Languages.safeText(tooltipKey));

    applyMiddleClickPaste(component);

  }

  /**
   * <p>Basic FEST and Accessibility support (no tooltip or description)</p>
   *
   * @param component The Swing component to decorate
   * @param nameKey   The component name (used directly for FEST and with lookup for accessible name)
   */
  public static void apply(JComponent component, CoreMessageKey nameKey) {

    // Ensure FEST can find it
    component.setName(nameKey.getKey());

    // Ensure Accessibility API can find it
    component.getAccessibleContext().setAccessibleName(Languages.safeText(nameKey));

    applyMiddleClickPaste(component);

  }

  /**
   * @param component The Swing component to decorate (if it is a text component)
   */
  private static void applyMiddleClickPaste(JComponent component) {

    // Support middle click
    if (component instanceof JTextComponent) {
      for (MouseListener mouseListener : component.getMouseListeners()) {
        if (MIDDLE_CLICK_LISTENER.equals(mouseListener.toString())) {
          // Avoid decorating multiple times (e.g. private notes becoming public notes)
          return;
        }
      }
      component.addMouseListener(getMiddleClickPasteMouseListener((JTextComponent) component));
    }

  }

  /**
   * @return A mouse listener that initiates the standard component paste behaviour for middle click on Unix
   */
  private static MouseListener getMiddleClickPasteMouseListener(final JTextComponent textComponent) {

    return new MouseAdapter() {

      @Override
      public String toString() {
        return MIDDLE_CLICK_LISTENER;
      }

      @Override
      public void mouseClicked(MouseEvent e) {

        switch (e.getButton()) {
          case MouseEvent.BUTTON2:
            int onmask = MouseEvent.ALT_DOWN_MASK;
            if ((e.getModifiersEx() & onmask) == onmask)
            // Support paste from middle click on Unix systems
            {
              // Use the standard paste mechanism
              // Updates can be detected through a DocumentListener on the component
              ClipboardUtils.pasteStringFromClipboard(textComponent);
            }
        }
      }
    };

  }

}
