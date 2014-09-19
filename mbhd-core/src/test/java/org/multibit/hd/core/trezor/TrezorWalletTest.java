package org.multibit.hd.core.trezor;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.crypto.ChildNumber;
import com.google.bitcoin.crypto.DeterministicHierarchy;
import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.crypto.HDKeyDerivation;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.config.Configurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.Assertions.assertThat;

public class TrezorWalletTest {

  private static NetworkParameters networkParameters;

  private static final Logger log = LoggerFactory.getLogger(TrezorWalletTest.class);

  private static final String TREZOR_SEED_PHRASE = "sniff divert demise scrub pony motor struggle innocent model mask enroll settle cash junior denial harsh peasant update estate aspect lyrics season empower asset";

  // m/44'/0'/0'/0/0
  private static final String EXPECTED_ADDRESS_0 = "1MkTpZN4TpLwJjZt9zHBXREJA8avUHXB3q";

  // m/44'/0'/0'/0/1
  private static final String EXPECTED_ADDRESS_1 = "1WGmwv86m1fFNVDRQ2YagdAFCButd36SV";

  // m/44'/0'/0'/0/2
  private static final String EXPECTED_ADDRESS_2 = "1PP1BvDeXjUcPDiEHBPWptQBAukhAwsLFt";

  // m/44'/0'/0'/0/3
  private static final String EXPECTED_ADDRESS_3 = "128f69V7GRqNSKwrjMkcuB6dbFKKEPtaLC";

  // m/44'/0'/0'/0/4
  private static final String EXPECTED_ADDRESS_4 = "18dxk72otf2amyAsjiKnEWhox5CJGQHYGA";

  @Before
  public void setUp() throws Exception {

  }

  @Test
  /**
   * Create some keys that derives addresses using BIP 44 - this is the HD account structure used by Trezor
   * This derivation uses the private master key
   */
  public void testCreateKeysWithTrezorAccount() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    networkParameters = BitcoinNetwork.current().get();

    // Create a private master key from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(TREZOR_SEED_PHRASE));

    DeterministicKey privateMasterKey = HDKeyDerivation.createMasterPrivateKey(seed);

    // Trezor uses BIP-44
    // BIP-44 starts from M/44'/0'

    DeterministicKey key_m_44h = HDKeyDerivation.deriveChildKey(privateMasterKey, new ChildNumber(44 | ChildNumber.HARDENED_BIT));
    log.debug("key_m_44h deterministic key = " + key_m_44h);

    DeterministicKey key_m_44h_0h = HDKeyDerivation.deriveChildKey(key_m_44h, ChildNumber.ZERO_HARDENED);
    log.debug("key_m_44h_0h deterministic key = " + key_m_44h_0h);

    DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(key_m_44h_0h);

    DeterministicKey key_m_44h_0h_0h = deterministicHierarchy.deriveChild(key_m_44h_0h.getPath(), false, false, new ChildNumber(0, true));
    log.debug("key_m_44h_0h_0h = " + key_m_44h_0h_0h);

    DeterministicKey key_m_44h_0h_0h_0 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h.getPath(), false, false, new ChildNumber(0, false));
    log.debug("key_m_44h_0h_0h_0 = " + key_m_44h_0h_0h_0);

    DeterministicKey key_m_44h_0h_0h_0_0 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(0, false));
    log.debug("key_m_44h_0h_0h_0_0 = " + key_m_44h_0h_0h_0_0);
    Address address0 = key_m_44h_0h_0h_0_0.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_1 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(1, false));
    log.debug("key_m_44h_0h_0h_0_1 = " + key_m_44h_0h_0h_0_1);
    Address address1 = key_m_44h_0h_0h_0_1.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_2 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(2, false));
    log.debug("key_m_44h_0h_0h_0_2 = " + key_m_44h_0h_0h_0_2);
    Address address2 = key_m_44h_0h_0h_0_2.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_3 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(3, false));
    log.debug("key_m_44h_0h_0h_0_3 = " + key_m_44h_0h_0h_0_3);
    Address address3 = key_m_44h_0h_0h_0_3.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_4 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(4, false));
    log.debug("key_m_44h_0h_0h_0_4 = " + key_m_44h_0h_0h_0_4);
    Address address4 = key_m_44h_0h_0h_0_4.toAddress(networkParameters);

    assertThat(address0.toString()).isEqualTo(EXPECTED_ADDRESS_0);

    assertThat(address1.toString()).isEqualTo(EXPECTED_ADDRESS_1);

    assertThat(address2.toString()).isEqualTo(EXPECTED_ADDRESS_2);

    assertThat(address3.toString()).isEqualTo(EXPECTED_ADDRESS_3);

    assertThat(address4.toString()).isEqualTo(EXPECTED_ADDRESS_4);
  }
}
