package org.multibit.hd.ui.views;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.ShowDetailScreenEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class DetailView {

  private final JPanel contentPanel;

  private CardLayout cardlayout = new CardLayout();

  private JPanel cardHolder = new JPanel(cardlayout);


  public DetailView() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );
    contentPanel = new JPanel(layout);

    for (Screen screen : Screen.values()) {
      JLabel cardLabel = new JLabel(screen.name(), SwingConstants.CENTER);
      cardHolder.add(cardLabel, screen.name());
    }

    contentPanel.add(cardHolder, "grow,wrap");

    Action[] actions = {
      new ShowPreviousAction(),
      new ShowNextAction(),
      new ShowTwoCardAction()
    };

    for (Action action : actions) {
      contentPanel.add(new JButton(action),"split,right");
    }

  }

  /**
   * @return The content panel for this View
   */
  public JPanel getContentPanel() {
    return contentPanel;
  }

  @Subscribe
  public void onShowDetailScreen(ShowDetailScreenEvent event) {

    cardlayout.show(cardHolder, event.getScreen().name());


  }

  private class ShowPreviousAction extends AbstractAction {
    public ShowPreviousAction() {
      super("Previous");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      cardlayout.previous(cardHolder);
    }
  }

  private class ShowNextAction extends AbstractAction {
    public ShowNextAction() {
      super("Next");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      cardlayout.next(cardHolder);
    }
  }

  private class ShowTwoCardAction extends AbstractAction {
    public ShowTwoCardAction() {
      super("Show Two");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      cardlayout.show(cardHolder, Screen.MAIN_SETTINGS.name());
    }
  }

}
