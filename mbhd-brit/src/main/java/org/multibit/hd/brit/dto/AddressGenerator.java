package org.multibit.hd.brit.dto;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;

import java.math.BigInteger;

/**
 *  <p>Class to provide the following to BRIT:<br>
 *  <ul>
 *  <li>Generation of deterministically generated Bitcoin addresses to Matcher</li>
 *  </ul>
 *  </p>
 *  
 */
public class AddressGenerator {

  /**
   * Always used main net for address generation
   */
  private static final NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

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
