package org.multibit.hd.ui.views.components;

import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
import org.multibit.hd.ui.views.wizards.WizardModel;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Decorator to provide the following to panels:</p>
 * <ul>
 * <li>Application of various themed styles to panels</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PanelDecorator {

  /**
   * Utilities have a private constructor
   */
  private PanelDecorator() {
  }

  /**
   * <p>Add a finish button</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addFinish(AbstractWizardView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel panel = view.getWizardPanel();

    view.setFinishButton(Buttons.newFinishButton(wizard.getFinishAction(view)));
    panel.add(view.getFinishButton(), "span 2,push");
  }

  /**
   * <p>Add an exit, cancel combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancel(AbstractWizardView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel panel = view.getWizardPanel();

    view.setExitButton(Buttons.newExitButton(wizard.getExitAction()));
    panel.add(view.getExitButton(), "span 2,push");

    view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
    panel.add(view.getCancelButton(), "shrink");

  }

  /**
   * <p>Add an exit/cancel, next button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancelNext(AbstractWizardView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel panel = view.getWizardPanel();

    if (wizard.isExiting()) {

      view.setExitButton(Buttons.newExitButton(wizard.getExitAction()));
      panel.add(view.getExitButton(), "span 2,push");

    } else {

      view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
      panel.add(view.getCancelButton(), "span 2,push");

    }

    view.setNextButton(Buttons.newNextButton(wizard.getNextAction(view)));
    panel.add(view.getNextButton(), "right,shrink");

  }

  /**
   * <p>Add an exit/cancel, previous, next button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancelPreviousNext(AbstractWizardView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the current panel
    JPanel panel = view.getWizardPanel();

    if (wizard.isExiting()) {

      view.setExitButton(Buttons.newExitButton(wizard.getExitAction()));
      panel.add(view.getExitButton(), "span 2,push");

    } else {

      view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
      panel.add(view.getCancelButton(), "span 2,push");
    }

    view.setPreviousButton(Buttons.newPreviousButton(wizard.getPreviousAction(view)));
    panel.add(view.getPreviousButton(), "right,shrink");

    view.setNextButton(Buttons.newNextButton(wizard.getNextAction(view)));
    panel.add(view.getNextButton(), "right,shrink");

  }

  /**
   * <p>Add a cancel, previous, send(next) button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addCancelPreviousSend(AbstractWizardView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the current panel
    JPanel panel = view.getWizardPanel();

    view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
    panel.add(view.getCancelButton(), "span 2,push");

    view.setPreviousButton(Buttons.newPreviousButton(wizard.getPreviousAction(view)));
    panel.add(view.getPreviousButton(), "right,shrink");

    view.setNextButton(Buttons.newSendButton(wizard.getNextAction(view)));
    panel.add(view.getNextButton(), "right,shrink");

  }

  /**
   * <p>Add a cancel, previous, next button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addCancelPreviousNext(AbstractWizardView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel panel = view.getWizardPanel();

    view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
    panel.add(view.getCancelButton(), "span 2,push");

    view.setPreviousButton(Buttons.newPreviousButton(wizard.getPreviousAction(view)));
    panel.add(view.getPreviousButton(), "right,shrink");

    view.setNextButton(Buttons.newNextButton(wizard.getNextAction(view)));
    panel.add(view.getNextButton(), "right,shrink");

  }

  /**
   * <p>Create the standard "wizard" theme</p>
   *
   * @param panel            The panel to decorate (contains wizard components at the top and buttons at the bottom)
   * @param wizardComponents The wizard components arranged in a panel
   */
  public static void applyWizardTheme(JPanel panel, JPanel wizardComponents, MessageKey titleKey) {

    Preconditions.checkNotNull(panel, "'panel' must be present");
    Preconditions.checkNotNull(wizardComponents, "'wizardComponents' must be present");
    Preconditions.checkNotNull(titleKey, "'titleKey' must be present");

    // Standard wizard layout
    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[][][][]", // Column constraints
      "[shrink]10[grow]10[shrink]" // Row constraints
    );

    panel.setLayout(layout);

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Add the wizard components
    panel.add(Labels.newTitleLabel(titleKey), "span 4,shrink,wrap,aligny top");
    panel.add(wizardComponents, "span 4,grow,wrap");

  }

  /**
   * <p>Make the panel have the "danger" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyDangerTheme(JPanel panel) {

    Preconditions.checkNotNull(panel, "'panel' must be present");

    Color background = Themes.currentTheme.dangerAlertBackground();
    Color border = Themes.currentTheme.dangerAlertBorder();
    Color text = Themes.currentTheme.dangerAlertText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "danger faded" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyDangerFadedTheme(JPanel panel) {

    Preconditions.checkNotNull(panel, "'panel' must be present");

    Color background = Themes.currentTheme.dangerAlertFadedBackground();
    Color border = Themes.currentTheme.dangerAlertBorder();
    Color text = Themes.currentTheme.dangerAlertText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "warning" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyWarningTheme(JPanel panel) {

    Preconditions.checkNotNull(panel, "'panel' must be present");

    Color background = Themes.currentTheme.warningAlertBackground();
    Color border = Themes.currentTheme.warningAlertBorder();
    Color text = Themes.currentTheme.warningAlertText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "success" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applySuccessTheme(JPanel panel) {

    Preconditions.checkNotNull(panel, "'panel' must be present");

    Color background = Themes.currentTheme.successAlertBackground();
    Color border = Themes.currentTheme.successAlertBorder();
    Color text = Themes.currentTheme.successAlertText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "success faded" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applySuccessFadedTheme(JPanel panel) {

    Preconditions.checkNotNull(panel, "'panel' must be present");

    Color background = Themes.currentTheme.successAlertFadedBackground();
    Color border = Themes.currentTheme.successAlertBorder();
    Color text = Themes.currentTheme.successAlertText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Apply panel colours</p>
   *
   * @param panel      The target panel
   * @param background The background colour
   * @param border     The border colour
   * @param text       The text colour
   */
  private static void applyTheme(JPanel panel, Color background, Color border, Color text) {

    Preconditions.checkNotNull(panel, "'panel' must be present");

    panel.setBackground(background);
    panel.setForeground(text);

    // Use a simple rounded border
    panel.setBorder(new TextBubbleBorder(border));

    for (Component component : panel.getComponents()) {
      if (component instanceof JLabel) {
        component.setForeground(text);
      }
    }

  }
}
