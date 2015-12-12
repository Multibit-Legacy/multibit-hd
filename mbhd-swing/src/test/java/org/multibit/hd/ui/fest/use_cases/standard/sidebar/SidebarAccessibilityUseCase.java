package org.multibit.hd.ui.fest.use_cases.standard.sidebar;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the sidebar screen is accessible</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SidebarAccessibilityUseCase extends AbstractFestUseCase {

  public SidebarAccessibilityUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Start by selecting row 0
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(BUY_SELL_ROW);
    // Expect the Buy/Sell dialog to show with a Cancel button
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .click();

    // Down to Send/Request
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible();

    // Down to Payments
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    window
      .table(MessageKey.PAYMENTS.getKey())
      .requireVisible();

    // Down to Contacts
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    window
      .table(MessageKey.CONTACTS.getKey())
      .requireVisible();

    // Down to Help
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    window
      .button(MessageKey.VIEW_IN_EXTERNAL_BROWSER.getKey())
      .requireVisible();

    // Down to Settings
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible();

    // Down to Manage Wallet
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible();

    // Down to Tools
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .requireVisible();

    // Down to Exit
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN));
    // Expect the Exit dialog to show with a Cancel button
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .click();

    // Change direction

    // Up to Tools
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .requireVisible();

    // Up to Manage Wallet
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible();

    // Up to Settings
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible();

    // Up to Help
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    window
      .button(MessageKey.VIEW_IN_EXTERNAL_BROWSER.getKey())
      .requireVisible();

    // Up to Contacts
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    window
      .table(MessageKey.CONTACTS.getKey())
      .requireVisible();

    // Up to Payments
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    window
      .table(MessageKey.PAYMENTS.getKey())
      .requireVisible();

    // Up to Send/Request
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible();

    // Up to Buy/Sell
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP));
    // Expect the Buy/Sell dialog to show with a Cancel button
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .click();

  }

}
