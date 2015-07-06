package org.multibit.hd.brit.matcher;

import org.bitcoinj.core.Address;
import org.multibit.hd.brit.dto.BRITWalletId;
import org.multibit.hd.brit.dto.WalletToEncounterDateLink;

import java.util.Date;
import java.util.Set;

/**
 * <p>Backing store to provide the following to Matcher:</p>
 * <ul>
 * <li>Load and store links between britWalletIds and encounter date</li>
 * <li>Load and store redeemer Bitcoin addresses, all of them and by day</li>
 * </ul>
 * </p>
 *
 * @since 0.0.1
 */
public interface MatcherStore {

  /**
   * Store the link between the wallet and the encounter date
   *
   * @param walletToEncounterDateLink The wallet to encounter date link object
   */
  void storeWalletToEncounterDateLink(WalletToEncounterDateLink walletToEncounterDateLink);

  /**
   * Lookup the Wallet to encounter date that is previously stored, if available
   *
   * @param britWalletId the britWalletId you want to lookup
   *
   * @return the WalletToEncounterDateLink for this britWalletId, or null if the britWalletId has never been seen before
   */
  WalletToEncounterDateLink lookupWalletToEncounterDateLink(BRITWalletId britWalletId);

  /**
   * Get the Bitcoin address set being sent back to the Payers for the day specified.
   *
   * @param encounterDate The date for which to look up the set of Bitcoin addresses
   *
   * @return The Bitcoin address set for this date (empty if none selected yet)
   */
  Set<Address> lookupBitcoinAddressListForDate(Date encounterDate);

  /**
   * Store the Bitcoin address set as the Bitcoin addresses to send back to the payer for the encounter date
   *
   * @param bitcoinAddresses The Bitcoin addresses to store
   * @param encounterDate    The date to store them against
   */
  void storeBitcoinAddressesForDate(Set<Address> bitcoinAddresses, Date encounterDate);

  /**
   * Store the set of Bitcoin addresses as the 'universe' of all possible Bitcoin addresses
   *
   * @param allBitcoinAddresses The set of all possible Bitcoin addresses to store
   */
  void storeAllBitcoinAddresses(Set<Address> allBitcoinAddresses);

  /**
   * Get the set of all possible Bitcoin addresses in this MatcherStore
   *
   * @return The set of all Bitcoin addresses in the store
   */
  Set<Address> getAllBitcoinAddresses();
}
