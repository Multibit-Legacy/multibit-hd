package org.multibit.hd.core.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Fixture assertions to provide the following to application:</p>
 * <ul>
 * <li>Support methods to simplify common test patterns</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class FixtureAsserts {

  /**
   * Validates the fixture JSON and provides a minified String for comparison
   *
   * @param fixtureClasspath The classpath (can be in other JARs)
   *
   * @return The contents as parsed by JSON
   *
   * @throws java.io.IOException If something goes wrong
   */
  public static String jsonFixture(String fixtureClasspath) throws IOException {
    ObjectMapper om = new ObjectMapper();
    return om.writeValueAsString(om.readValue(fixture(fixtureClasspath),JsonNode.class));
  }

  /**
   * @param fixtureClasspath The classpath (can be in other JARs)
   *
   * @return A contents as a UTF8 string
   *
   * @throws java.io.IOException If something goes wrong
   */
  public static String fixture(String fixtureClasspath) throws IOException {
    return Resources.toString(FixtureAsserts.class.getResource(fixtureClasspath), Charsets.UTF_8).trim();
  }

  /**
   * Takes a String and compares it to a JSON fixture (
   *
   * @param reason           The reason (e.g. "a Customer can be marshalled to JSON")
   * @param representation   The simple string representation
   * @param fixtureClasspath The classpath reference to the resource (e.g. "fixtures/example.json")
   *
   * @throws java.io.IOException If something goes wrong
   */
  public static void assertStringMatchesJsonFixture(String reason, String representation, String fixtureClasspath) throws IOException {

    assertThat(jsonFixture(fixtureClasspath)).isEqualTo(representation).describedAs(reason);
  }

  /**
   * Takes a String and compares it to a String fixture
   *
   * @param reason           The reason (e.g. "a Customer can be marshalled to JSON")
   * @param representation   The simple string representation
   * @param fixtureClasspath The classpath reference to the resource (e.g. "/fixtures/example.json")
   *
   * @throws java.io.IOException If something goes wrong
   */
  public static void assertStringMatchesStringFixture(String reason, String representation, String fixtureClasspath) throws IOException {

    assertThat(fixture(fixtureClasspath)).isEqualTo(representation).describedAs(reason);

  }

  /**
   * Takes a String and compares it to a normalised (CRLF stripped) String fixture
   *
   * @param reason           The reason (e.g. "a Customer can be marshalled to JSON")
   * @param representation   The simple string representation
   * @param fixtureClasspath The classpath reference to the resource (e.g. "/fixtures/example.json")
   *
   * @throws java.io.IOException If something goes wrong
   */
  public static void assertStringMatchesNormalisedStringFixture(String reason, String representation, String fixtureClasspath) throws IOException {

    String normalisedFixture = fixture(fixtureClasspath).replaceAll("\n","").replaceAll("\r","");
    String normalisedRepresentation = representation.replaceAll("\n","").replaceAll("\r","");

    assertThat(normalisedRepresentation).isEqualTo(normalisedFixture).describedAs(reason);

  }

  /**
   * Compares the given byte[] with that read from the expected fixture
   *
   * @param reason           The reason (e.g. "a correct Issue has been generated")
   * @param representation   The byte[] to test
   * @param fixtureClasspath The classpath reference to the resource (e.g. "fixtures/example.png")
   *
   * @throws java.io.IOException If something goes wrong
   */
  public static void assertRepresentationMatchesBinaryFixture(String reason, byte[] representation, String fixtureClasspath) throws IOException {

    assertThat(BinaryFixtureHelpers.fixture(fixtureClasspath)).isEqualTo(representation).describedAs(reason);

  }

}