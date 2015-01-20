package org.multibit.hd.core.extensions;

/**
 * Copyright 2015 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.dto.WalletType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import static org.fest.assertions.Assertions.assertThat;

public class WalletTypeExtensionTest {

  private static final Logger log = LoggerFactory.getLogger(WalletTypeExtension.class);

  public static final String EXPECTED_MBHD_SOFT_WALLET_TYPE = "MBHD_SOFT_WALLET";
  public static final String EXPECTED_TREZOR_SOFT_WALLET_TYPE = "TREZOR_SOFT_WALLET";
  public static final String EXPECTED_TREZOR_HARD_WALLET_TYPE = "TREZOR_HARD_WALLET";
  public static final String EXPECTED_UNKNOWN_WALLET_TYPE = "UNKNOWN";

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testMBHDSoftWalletType() throws Exception {
    testWalletType(WalletType.MBHD_SOFT_WALLET, EXPECTED_MBHD_SOFT_WALLET_TYPE);
  }

  @Test
  public void testTrezorSoftWalletType() throws Exception {
    testWalletType(WalletType.TREZOR_SOFT_WALLET, EXPECTED_TREZOR_SOFT_WALLET_TYPE);
  }

  @Test
  public void testTrezorHardWalletType() throws Exception {
    testWalletType(WalletType.TREZOR_HARD_WALLET, EXPECTED_TREZOR_HARD_WALLET_TYPE);
  }

  @Test
  public void testUnknownWalletType() throws Exception {
    testWalletType(WalletType.UNKNOWN, EXPECTED_UNKNOWN_WALLET_TYPE);
  }

  private void testWalletType(WalletType walletType, String expectedWalletTypeString) throws Exception {
    WalletTypeExtension extension = new WalletTypeExtension(walletType);

    assertThat(walletType.equals(extension.getWalletType())).isTrue();

    byte[] serialisedExtension = extension.serializeWalletExtension();
    log.debug("Serialised " + expectedWalletTypeString + ": {}", Hex.toHexString(serialisedExtension));

    WalletTypeExtension rebornExtension = new WalletTypeExtension();
    rebornExtension.deserializeWalletExtension(null, serialisedExtension);

    assertThat(walletType.equals(rebornExtension.getWalletType())).isTrue();
  }
}

