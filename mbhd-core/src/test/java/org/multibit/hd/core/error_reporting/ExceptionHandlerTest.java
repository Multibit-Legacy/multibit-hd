package org.multibit.hd.core.error_reporting;

import org.junit.Test;

import java.io.InputStream;


public class ExceptionHandlerTest {

  @Test
  public void testReadTruncatedCurrentLogfile() throws Exception {

    // Arrange
    InputStream is = ExceptionHandlerTest.class.getResourceAsStream("/fixtures/error_reporting/test-multibit-hd.log");

    // Act
    String result = ExceptionHandler.readAndTruncateInputStream(is, 200);

    System.out.println("");


  }
}