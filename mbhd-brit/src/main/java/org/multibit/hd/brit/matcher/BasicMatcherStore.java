package org.multibit.hd.brit.matcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.multibit.hd.brit.dto.BRITWalletId;
import org.multibit.hd.brit.dto.WalletToEncounterDateLink;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  <p>Store to provide the following to Matcher classes:<br>
 *  <ul>
 *  <li>basic store and lookup of bitcoin addresses</li>
 * <li>basic store and lookup of wallet to encounter date links</li>
 *  </ul>
 *  </p>
 *  
 */
public class BasicMatcherStore implements MatcherStore {

  /**
   * The directory in which the backing files reside
   */
  private String backingStoreDirectory;

  /**
   * A map containing the link from a BRITWalletId to the previous encounter of this wallet (if available)
   */
  private Map<BRITWalletId, WalletToEncounterDateLink> previousEncounterMap;

  /**
   * A list of all the Bitcoin addresses in the MatcherStore
   */
  private List<String> allBitcoinAddresses;

  /**
   * A map from the date of encounter to the list of Bitcoins used that day
   */
  private Map<Date, List<String>> encounterDateToBitcoinAddressesMap;

  public BasicMatcherStore(String backingStoreDirectory) {
    this.backingStoreDirectory = backingStoreDirectory;

    initialise(backingStoreDirectory);
  }

  /**
   * Initialise the MatchStore with the data stored at the backingStoreDirectory
   * @param backingStoreDirectory
   */
  private void initialise(String backingStoreDirectory) {
    // TODO populate with the data in the backingStoreDirectory
    previousEncounterMap = Maps.newHashMap();

    allBitcoinAddresses = Lists.newArrayList();

    encounterDateToBitcoinAddressesMap = Maps.newHashMap();
  }

  @Override
  public void storeWalletToEncounterDateLink(WalletToEncounterDateLink walletToEncounterDateLink) {
    previousEncounterMap.put(walletToEncounterDateLink.getBritWalletId(), walletToEncounterDateLink);

    // TODO also append link data to backing file
  }

  @Override
  public WalletToEncounterDateLink lookupWalletToEncounterDateLink(BRITWalletId britWalletId) {
    // See if we have already seen this WalletId before

    // If this is present, return it.
    // If this is null, return a null.
    return previousEncounterMap.get(britWalletId);
  }

  @Override
  public List<String> getBitcoinAddressListForDate(Date encounterDate) {
    return encounterDateToBitcoinAddressesMap.get(encounterDate);
  }

  @Override
  public void storeBitcoinAddressListForDate(List<String> bitcoinAddressList, Date encounterDate) {
    encounterDateToBitcoinAddressesMap.put(encounterDate, bitcoinAddressList);

    // TODO also write to a file
  }

  @Override
  public void storeAllBitcoinAddresses(List<String> allBitcoinAddresses) {
    this.allBitcoinAddresses = allBitcoinAddresses;

    // TODO also write out to file
  }

  @Override
  public List<String> getAllBitcoinAddress() {
    return allBitcoinAddresses;
  }
}
