package org.multibit.hd.core.services;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.services.FeeService;
import static org.fest.assertions.api.Assertions.assertThat;

public class CoreServicesTest {

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testCreateFeeService() throws Exception {
    FeeService feeService = CoreServices.createFeeService();
    assertThat(feeService).isNotNull();
  }
}
