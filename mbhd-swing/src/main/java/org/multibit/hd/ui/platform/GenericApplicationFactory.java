/**
 * Copyright 2011 multibit.org
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
package org.multibit.hd.ui.platform;

import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.platform.builder.generic.DefaultApplicationBuilder;
import org.multibit.hd.ui.platform.builder.linux.LinuxApplicationBuilder;
import org.multibit.hd.ui.platform.builder.unix.UnixApplicationBuilder;
import org.multibit.hd.ui.platform.builder.win.WindowsApplicationBuilder;
import org.multibit.hd.ui.platform.builder.mac.MacApplicationBuilder;
import org.multibit.hd.ui.platform.builder.solaris.SolarisApplicationBuilder;

/**
 * <p>Factory to provide the following to application:</p>
 * <ul>
 * <li>Builds a {@link GenericApplication} appropriate for the execution platform</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum GenericApplicationFactory {

  INSTANCE;

  /**
   * This method must be synchronized to ensure that it completes before any OS events come in
   *
   * @param specification The specification providing all the listeners
   *
   * @return A {@link GenericApplication} appropriate for the current execution platform
   */
  public synchronized GenericApplication buildGenericApplication(GenericApplicationSpecification specification) {
    // Determine the current platform
    if (OSUtils.isMac()) {
      return buildMacApplication(specification);
    }
    if (OSUtils.isLinux()) {
      return buildLinuxApplication(specification);
    }
    if (OSUtils.isSolaris()) {
      return buildSolarisApplication(specification);
    }
    if (OSUtils.isUnix()) {
      return buildUnixApplication(specification);
    }
    if (OSUtils.isWindows()) {
      return buildWindowsApplication(specification);
    }
    return buildUnknownApplication(specification);
  }

  private GenericApplication buildLinuxApplication(GenericApplicationSpecification specification) {
    return new LinuxApplicationBuilder().build(specification);
  }

  private GenericApplication buildMacApplication(GenericApplicationSpecification specification) {
    return new MacApplicationBuilder().build(specification);
  }

  private GenericApplication buildSolarisApplication(GenericApplicationSpecification specification) {
    return new SolarisApplicationBuilder().build(specification);
  }

  private GenericApplication buildUnixApplication(GenericApplicationSpecification specification) {
    return new UnixApplicationBuilder().build(specification);
  }

  private GenericApplication buildWindowsApplication(GenericApplicationSpecification specification) {
    return new WindowsApplicationBuilder().build(specification);
  }

  private GenericApplication buildUnknownApplication(GenericApplicationSpecification specification) {
    return new DefaultApplicationBuilder().build(specification);
  }

}
