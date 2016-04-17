package org.multibit.hd.core.managers;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class InstallationManagerTest {

  @Test
  public void verifyApplicationSettings() throws Exception {

    assertThat(InstallationManager.MBHD_APP_NAME).isEqualTo("MultiBitHD");
    assertThat(InstallationManager.CA_CERTS_NAME).isEqualTo("mbhd-cacerts");
    assertThat(InstallationManager.MBHD_CONFIGURATION_FILE).isEqualTo("mbhd.yaml");
    assertThat(InstallationManager.MBHD_WEBSITE_HELP_BASE).isEqualTo("https://multibit.org/hd0.3");

  }

}