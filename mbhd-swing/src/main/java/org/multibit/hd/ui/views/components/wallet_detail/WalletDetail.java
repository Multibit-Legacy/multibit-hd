package org.multibit.hd.ui.views.components.wallet_detail;

/**
 *  <p>DTO to provide the following to WalletDetailModel:<br>

 *  </p>
 *  
 */
public class WalletDetail {
  private int numberOfTransactions;
   private int numberofContacts;

   private String applicationDirectory;
   private String walletDirectory;

  public int getNumberOfTransactions() {
    return numberOfTransactions;
  }

  public void setNumberOfTransactions(int numberOfTransactions) {
    this.numberOfTransactions = numberOfTransactions;
  }

  public int getNumberofContacts() {
    return numberofContacts;
  }

  public void setNumberofContacts(int numberofContacts) {
    this.numberofContacts = numberofContacts;
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
