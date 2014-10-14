package org.multibit.hd.ui.views.components.wallet_detail;

/**
 *  <p>DTO to provide the following to WalletDetailModel:</p>
 * <ul>
 * <li>Wallet details in a neutral format</li>
 * </ul>
 *  </p>
 *  
 */
public class WalletDetail {

  private int numberOfPayments;
  private int numberOfContacts;

  private String applicationDirectory;
  private String walletDirectory;

  public int getNumberOfPayments() {
    return numberOfPayments;
  }

  public void setNumberOfPayments(int numberOfPayments) {
    this.numberOfPayments = numberOfPayments;
  }

  public int getNumberOfContacts() {
    return numberOfContacts;
  }

  public void setNumberOfContacts(int numberOfContacts) {
    this.numberOfContacts = numberOfContacts;
  }

  public String getApplicationDirectory() {
    return applicationDirectory;
  }

  public void setApplicationDirectory(String applicationDirectory) {
    this.applicationDirectory = applicationDirectory;
  }

  public String getWalletDirectory() {
    return walletDirectory;
  }

  public void setWalletDirectory(String walletDirectory) {
    this.walletDirectory = walletDirectory;
  }
}
