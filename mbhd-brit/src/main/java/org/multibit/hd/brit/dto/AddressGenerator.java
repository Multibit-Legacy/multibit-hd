package org.multibit.hd.brit.dto;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;

import java.math.BigInteger;

/**
 * <p>Class to provide the following to BRIT:</p>
 * <ul>
 * <li>Deterministic generation of Bitcoin addresses for Matcher</li>
 * </ul>
 */
public class AddressGenerator {

  /**
   * Always use main net for address generation
   */
  private static final NetworkParameters networkParameters = MainNetParams.get();

  /**
   * The seed to use in address generation
   */
  private BigInteger generatorSeed;

  public AddressGenerator(BigInteger generatorSeed) {
    this.generatorSeed = generatorSeed;
  }

  /**
   * Generate a bitcoin address for a given index
   */
  public Address createAddress(int index) {
    // TODO
    return null;
  }
}
