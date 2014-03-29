package org.multibit.hd.brit.matcher;

import org.multibit.hd.brit.dto.BRITWalletId;
import org.multibit.hd.brit.dto.WalletToEncounterDateLink;

import java.util.Date;
import java.util.List;

/**
 *  <p>Backing store to provide the following to Matcher:<br>
 *  <ul>
 *  <li>Load and store links between britWalletIds and encounter date</li>
 *  <li>Load and store redeemer Bitcoin addresses, all of them and by day</li>
 *  </ul>
 *  </p>
 *  
 */
public interface MatcherStore {

  /**
   * Store the link between the wallet and the encounter date
   * @param walletToEncounterDateLink The wallet to encounter date link object
   */
  public void storeWalletToEncounterDateLink(WalletToEncounterDateLink walletToEncounterDateLink);

  /**
   * Lookup the Wallet to encounter date that is previously stored, if available
   * @param britWalletId the britWalletId you want to lookup
   * @return the WalletToEncounterDateLink for this britWalletId, or null if the britWalletId has never been seen before
   */
  public WalletToEncounterDateLink lookupWalletToEncounterDateLink(BRITWalletId britWalletId);

  /**
   * Get the Bitcoin address list being sent back to the Payers for the day specified.
   * @param encounterDate the date to look up the List of Bitcoin addresses for
   * @return the Bitcoin address list for this date, or null if non has been set yet
   */
  public List<String> getBitcoinAddressListForDate(Date encounterDate);

  /**
   * Store the bitcoinAddressList as the Bitcoin addresses to send back tothe payer for the encounterDate
   * @param bitcoinAddressList the Bitcoin address list to store
   * @param encounterDate the date to store them against
   */
  public void storeBitcoinAddressListForDate(List<String> bitcoinAddressList, Date encounterDate);

  /**
   * Store the list of Bitcoin addresses as the 'universe' of all possible Bitcoin addresses
   * @param allBitcoinAddresses The list of all possible Bitcoin addresses to store
   */
  public void storeAllBitcoinAddresses(List<String> allBitcoinAddresses);

  /**
   * Get the list of all possible Bitcoin addresses in this MatcherStore
   * @return The list of all Bitcoin addresses in the store
   */
  public List<String> getAllBitcoinAddress();
}
