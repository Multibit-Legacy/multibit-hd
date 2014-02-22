package org.multibit.hd.ui.views.components.panels;

import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
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
   * <p>Create the standard "wizard" theme</p>
   *
   * @param wizardPanel The wizard panel to decorate (arranged as [title][dataPanel][buttons])
   * @param dataPanel   The data panel sandwiched into the wizard
   * @param titleKey    The key to use for the title text
   */
  public static void applyWizardTheme(JPanel wizardPanel, JPanel dataPanel, MessageKey titleKey) {

    Preconditions.checkNotNull(wizardPanel, "'wizardPanel' must be present");
    Preconditions.checkNotNull(dataPanel, "'dataPanel' must be present");
    Preconditions.checkNotNull(titleKey, "'titleKey' must be present");

    // Standard wizard layout
    MigLayout layout = new MigLayout(
      "fillx,insets 5", // Layout constraints
      "[][][][]", // Column constraints
      "[shrink]10[grow]10[]" // Row constraints
    );
    wizardPanel.setLayout(layout);

    // Apply the theme
    wizardPanel.setBackground(Themes.currentTheme.detailPanelBackground());
    //wizardPanel.setOpaque(true);

    // Add the wizard components
    wizardPanel.add(Labels.newTitleLabel(titleKey), "span 4,shrink,wrap,aligny top");
    wizardPanel.add(dataPanel, "span 4,grow,wrap");

  }

  /**
   * <p>Create the standard "detail" theme</p>
   *
   * @param detailPanel The wizard panel to decorate (arranged as [title][dataPanel][buttons])
   * @param titleKey    The key to use for the title text
   */
  public static void applyScreenTheme(JPanel detailPanel, MessageKey titleKey) {

    Preconditions.checkNotNull(detailPanel, "'detailPanel' must be present");
    Preconditions.checkNotNull(titleKey, "'titleKey' must be present");

    // Standard wizard layout
    MigLayout layout = new MigLayout(
      "fillx,insets 5", // Layout constraints
      "[][][][]", // Column constraints
      "[shrink]10[grow]10[]" // Row constraints
    );
    detailPanel.setLayout(layout);

    // Apply the theme
    detailPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Add the wizard components
    detailPanel.add(Labels.newTitleLabel(titleKey), "span 4,shrink,wrap,aligny top");

  }

  /**
   * <p>Add an exit, cancel combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancel(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel wizardPanel = view.getWizardPanel();

    addExit(view, wizard, wizardPanel);
    addCancel(view, wizard, wizardPanel);

  }

  /**
   * <p>Add a finish only button</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addFinish(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");

    // Use the wizard panel
    JPanel wizardPanel = view.getWizardPanel();

    // Add an invisible button to push the finish
    JButton empty = Buttons.newExitButton(null);
    empty.setVisible(false);

    wizardPanel.add(empty, "cell 0 2,push");

    addFinish(view, wizard, wizardPanel);

    view.getFinishButton().requestFocusInWindow();

  }

  /**
   * <p>Add a cancel, finish combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addCancelFinish(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel wizardPanel = view.getWizardPanel();

    addCancel(view, wizard, wizardPanel);
    addFinish(view, wizard, wizardPanel);

  }

  /**
   * <p>Add a cancel, apply combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addCancelApply(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel wizardPanel = view.getWizardPanel();

    addCancel(view, wizard, wizardPanel);
    addApply(view, wizard, wizardPanel);

  }

  /**
   * <p>Add an exit/cancel, next button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancelNext(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel wizardPanel = view.getWizardPanel();

    addExitCancel(view, wizard, wizardPanel);
    addNext(view, wizard, wizardPanel);

  }

  /**
   * <p>Add an exit/cancel, previous, next button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancelPreviousNext(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the current panel
    JPanel wizardPanel = view.getWizardPanel();

    addExitCancel(view, wizard, wizardPanel);
    addPrevious(view, wizard, wizardPanel);
    addNext(view, wizard, wizardPanel);

  }

  /**
   * <p>Add an exit/cancel, previous, finish button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancelPreviousFinish(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the current panel
    JPanel wizardPanel = view.getWizardPanel();

    addExitCancel(view, wizard, wizardPanel);
    addPrevious(view, wizard, wizardPanel);
    addFinish(view, wizard, wizardPanel);

  }

  /**
   * <p>Add an exit/cancel, restore, finish button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addExitCancelRestoreFinish(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the current panel
    JPanel wizardPanel = view.getWizardPanel();

    addExitCancel(view, wizard, wizardPanel);
    addRestore(view, wizard, wizardPanel);
    addFinish(view, wizard, wizardPanel);

  }

  /**
   * <p>Add a cancel, previous, send(next) button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addCancelPreviousSend(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the current panel
    JPanel wizardPanel = view.getWizardPanel();

    addCancel(view, wizard, wizardPanel);
    addPrevious(view, wizard, wizardPanel);

    // Replace next with send
    view.setNextButton(Buttons.newSendButton(wizard.getNextAction(view)));
    wizardPanel.add(view.getNextButton(), "cell 3 2");

  }

  /**
   * <p>Add a cancel, previous, next button combination</p>
   *
   * @param view   The view containing the panel to decorate
   * @param wizard The wizard providing the actions
   * @param <M>    The wizard model type
   * @param <P>    The wizard panel model type
   */
  public static <M extends WizardModel, P> void addCancelPreviousNext(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    // Use the wizard panel
    JPanel wizardPanel = view.getWizardPanel();

    addCancel(view, wizard, wizardPanel);
    addPrevious(view, wizard, wizardPanel);
    addNext(view, wizard, wizardPanel);

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
   * <p>Make the panel have the "pending" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyPendingTheme(JPanel panel) {

    Preconditions.checkNotNull(panel, "'panel' must be present");

    Color background = Themes.currentTheme.pendingAlertBackground();
    Color border = Themes.currentTheme.pendingAlertBorder();
    Color text = Themes.currentTheme.pendingAlertText();

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

    // Ensure that the background color is presented
    panel.setOpaque(true);

    // Use a simple rounded border
    panel.setBorder(new TextBubbleBorder(border));

    for (Component component : panel.getComponents()) {
      if (component instanceof JLabel) {
        component.setForeground(text);
      }
    }

  }

  /**
   * <p>Add a "next" button into the standard cell</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addNext(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {
    view.setNextButton(Buttons.newNextButton(wizard.getNextAction(view)));
    wizardPanel.add(view.getNextButton(), "cell 3 2");
  }

  /**
   * <p>Add a "previous" button into the standard cell</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addPrevious(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {
    view.setPreviousButton(Buttons.newPreviousButton(wizard.getPreviousAction(view)));
    wizardPanel.add(view.getPreviousButton(), "cell 2 2");
  }

  /**
   * <p>Add a "restore" button into the standard cell</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addRestore(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {
    view.setRestoreButton(Buttons.newRestoreButton(wizard.getRestoreAction(view)));
    wizardPanel.add(view.getRestoreButton(), "cell 2 2");
  }

  /**
   * <p>Add an "exit/cancel" button into the standard cell</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addExitCancel(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {

    if (wizard.isExiting()) {

      view.setExitButton(Buttons.newExitButton(wizard.getExitAction()));
      wizardPanel.add(view.getExitButton(), "cell 0 2,push");

    } else {

      view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
      wizardPanel.add(view.getCancelButton(), "cell 0 2,push");

    }

  }

  /**
   * <p>Add "exit" button into the standard cell</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addExit(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {

    view.setExitButton(Buttons.newExitButton(wizard.getExitAction()));
    wizardPanel.add(view.getExitButton(), "cell 0 2,push");

  }

  /**
   * <p>Add an "cancel" button into an appropriate cell (will detect exiting wizard)</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addCancel(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {

    if (wizard.isExiting()) {

      view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
      wizardPanel.add(view.getCancelButton(), "cell 3 2");

    } else {

      view.setCancelButton(Buttons.newCancelButton(wizard.getCancelAction()));
      wizardPanel.add(view.getCancelButton(), "cell 0 2,push");

    }

  }

  /**
   * <p>Add a "finish" button into an appropriate cell</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addFinish(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    view.setFinishButton(Buttons.newFinishButton(wizard.getFinishAction(view)));
    wizardPanel.add(view.getFinishButton(), "cell 3 2");
  }

  /**
   * <p>Add an "apply" button into an appropriate cell</p>
   *
   * @param view        The view containing the panel to decorate
   * @param wizard      The wizard providing the actions
   * @param wizardPanel The wizard panel providing the layout
   * @param <M>         The wizard model type
   * @param <P>         The wizard panel model type
   */
  private static <M extends WizardModel, P> void addApply(AbstractWizardPanelView<M, P> view, AbstractWizard<M> wizard, JPanel wizardPanel) {

    Preconditions.checkNotNull(view, "'view' must be present");
    Preconditions.checkNotNull(view, "'wizard' must be present");
    Preconditions.checkNotNull(view.getWizardPanel(), "'wizardPanel' must be present");

    view.setApplyButton(Buttons.newApplyButton(wizard.getApplyAction(view)));
    wizardPanel.add(view.getApplyButton(), "cell 3 2");
  }

}
