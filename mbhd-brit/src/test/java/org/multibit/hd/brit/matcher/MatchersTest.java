package org.multibit.hd.brit.matcher;

/**
 * Copyright 2014 multibit.org
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
import org.multibit.hd.brit.crypto.PGPUtilsTest;
import org.multibit.hd.brit.utils.FileUtils;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

public class MatchersTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreateMatcher() throws Exception {
    // Find the example Matcher PGP secret key ring file
    File matcherSecretKeyFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_MATCHER_SECRET_KEYRING_FILE);

    // Create a random temporary directory for the matcher store to use
    String matcherStoreDirectoryLocation = FileUtils.makeRandomTemporaryDirectory().getAbsolutePath();

    MatcherConfig matcherConfig = new MatcherConfig(matcherSecretKeyFile, PGPUtilsTest.TEST_DATA_PASSWORD, matcherStoreDirectoryLocation);

    Matcher matcher = Matchers.newBasicMatcher(matcherConfig);
    assertThat(matcher).isNotNull();

    // Check the Matcher PGP private key is stored properly
    assertThat(matcher.getConfig().getMatcherSecretKeyringFile()).isEqualTo(matcherSecretKeyFile);
  }
}
