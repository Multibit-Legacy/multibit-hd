package org.multibit.hd.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import org.bitcoinj.core.Wallet;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;

/**
 * <p>Value object to provide the following to application:</p>
 * <ul>
 * <li>Access to optional Bitcoinj Wallet and mandatory WalletId</li>
 * <li>Provision of a name and notes for identifying a wallet</li>
 * </ul>
 *
 * <p>A wallet summary can be built from a wallet root and an appropriate YAML file</p>
 *
 * @since 0.0.1 
 */
public class WalletSummary {

  @JsonIgnore
  private Wallet wallet;

  @JsonIgnore
  private WalletId walletId;

  @JsonIgnore
  private File walletFile;

  @JsonIgnore
  private CharSequence password;

  private String name;

  private String notes;

  private WalletType walletType = WalletType.MBHD_SOFT_WALLET;

  /**
   * The wallet credentials, encrypted with an AES key derived from the wallet seed
   */
  private byte[] encryptedPassword;

  /**
   * The backup AES key, encrypted with an AES key derived from the wallet credentials
   */
  private byte[] encryptedBackupKey;

  /**
   * Default constructor for Jackson
   */
  public WalletSummary() {
  }

  /**
   * <p>Standard constructor</p>
   *
   * @param walletId The wallet ID
   * @param wallet   The Bitcoinj wallet
   */
  public WalletSummary(WalletId walletId, @Nullable Wallet wallet) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    this.wallet = wallet;
    this.walletId = walletId;
  }

  /**
   * @return The Bitcoinj wallet associated with this summary (can be null)
   */
  public Wallet getWallet() {
    return wallet;
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  /**
   * @return The wallet ID
   */
  public WalletId getWalletId() {
    return walletId;
  }

  public void setWalletId(WalletId walletId) {
    this.walletId = walletId;
  }

  /**
   * @return The wallet credentials
   */
  public CharSequence getPassword() {
    return password;
  }

  public void setPassword(CharSequence password) {
    this.password = password;
  }

  /**
   * @return The short wallet name (e.g. "ACME Ltd")
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The longer notes (e.g. "The ACME Ltd business wallet")
   */
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Get the encrypted wallet credentials (which is padded)
   * - use WalletManager#unpadPasswordBytes to unpad
   * @return encrypted, padded wallet credentials
   */
  public byte[] getEncryptedPassword() {
    return Arrays.copyOf(encryptedPassword, encryptedPassword.length);
  }

  public void setEncryptedPassword(byte[] encryptedPassword) {
    this.encryptedPassword = Arrays.copyOf(encryptedPassword, encryptedPassword.length);
  }

  public byte[] getEncryptedBackupKey() {
    return Arrays.copyOf(encryptedBackupKey, encryptedBackupKey.length);
  }

  public void setEncryptedBackupKey(byte[] encryptedBackupKey) {
    this.encryptedBackupKey = Arrays.copyOf(encryptedBackupKey, encryptedBackupKey.length);
  }

  public WalletType getWalletType() {
     return walletType;
   }

  public void setWalletType(WalletType walletType) {
     this.walletType = walletType;
   }

  public File getWalletFile() {
    return walletFile;
  }

  public void setWalletFile(File walletFile) {
    this.walletFile = walletFile;
  }


  @Override
  public String toString() {
    return "WalletSummary{" +
            "wallet=" + wallet +
            ", walletId=" + walletId +
            ", walletType=" + walletType +
            ", walletFile=" +walletFile +
            ", credentials=***" +
            ", name='" + name + '\'' +
            ", notes='" + notes + '\'' +
            ", encryptedPassword=" + Arrays.toString(encryptedPassword) +
            ", encryptedBackupKey=" + Arrays.toString(encryptedBackupKey) +
            '}';
  }
}
