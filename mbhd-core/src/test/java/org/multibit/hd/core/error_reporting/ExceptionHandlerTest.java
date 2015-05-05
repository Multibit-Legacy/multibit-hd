package org.multibit.hd.core.error_reporting;

import com.google.common.base.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.common.error_reporting.ErrorReport;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.Json;
import org.multibit.hd.core.managers.InstallationManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.fest.assertions.Assertions.assertThat;


public class ExceptionHandlerTest {

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @After
  public void tearDown() throws Exception {

    InstallationManager.unrestricted = false;

  }

  @Test
  public void testReadTruncatedCurrentLogfile() throws Exception {

    // Arrange
    InputStream is = ExceptionHandlerTest.class.getResourceAsStream("/fixtures/error_reporting/test-multibit-hd.log");

    // Act
    String result = ExceptionHandler.readAndTruncateInputStream(is, 2000);

    // Assert
    assertThat(result).startsWith("{");

  }

  @Test
  public void testBuildErrorReport() throws Exception {

    // Arrange
    InputStream is = ExceptionHandlerTest.class.getResourceAsStream("/fixtures/error_reporting/test-multibit-hd.log");
    String truncatedLog = ExceptionHandler.readAndTruncateInputStream(is, 2000);

    // Act
    ErrorReport testObject = ExceptionHandler.buildErrorReport(
      "Example notes",
      truncatedLog
    );

    // Assert
    assertThat(testObject.getLogEntries()).isNotEmpty();
    assertThat(testObject.getLogEntries().size()).isEqualTo(8);

  }

  @Test
  public void testWriteErrorReport() throws Exception {

    // Arrange
    InputStream is = ExceptionHandlerTest.class.getResourceAsStream("/fixtures/error_reporting/test-multibit-hd.log");
    String truncatedLog = ExceptionHandler.readAndTruncateInputStream(is, 2000);
    ErrorReport fixtureErrorReport = ExceptionHandler.buildErrorReport(
      "Example notes",
      truncatedLog
    );

    ByteArrayOutputStream testObject = new ByteArrayOutputStream();

    // Act
    Json.writeJson(testObject, fixtureErrorReport);

    // Assert
    Optional<ErrorReport> actualErrorReport = Json.readJson(testObject.toString(), ErrorReport.class);

    assertThat(actualErrorReport.get().getLogEntries().size()).isEqualTo(8);

  }

}