package org.multibit.hd.core.utils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.multibit.hd.core.config.Configurations;

import java.util.List;

/**
 * <p>Utility to provide the following to Bitcoin messages:</p>
 * <ul>
 * <li>Formatting of Bitcoin signatures</li>
 * <li>Parsing of formatted Bitcoin signatures with fall back to single message</li>
 * </ul>
 * <p>Formatting is similar to <a href="http://www.ietf.org/rfc/rfc2440.txt">RFC 2440</a> and appears as</p>
 * <pre>
 * -----BEGIN BITCOIN SIGNED MESSAGE-----
 * Hello World
 * -----BEGIN BITCOIN SIGNATURE-----
 * Version: MultiBit HD (0.0.1b-2)
 * Comment: https://multibit.org
 * Address: 16R2kAxaUNM4xj6ykKbxEugpJdYyJzTP13
 *
 * H0b22gIQIfutUzm7Z9qchdfhUtaO52alhNPK3emrkGOfbOzGHVPuWD9rMIphxniwBNgF/YN4c5C/dMwXz3yJz5k=
 * -----END BITCOIN SIGNATURE-----
 * </pre>
 * <p>The parser is generous in that it:</p>
 * <ul>
 * <li>searches for the Armor Headers as case-sensitive text anywhere on the line</li>
 * <li>allows a missing spacing line between the Armor Header Keys and the signature</li>
 * <li>allows a missing message block</li>
 * <li>failure causes the content to be treated as the message leaving the user to unpick it</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class BitcoinMessages {

  /**
   * Armor header indicating the start of the message block
   */
  private static final String BEGIN_SIGNED_MESSAGE = "BEGIN BITCOIN SIGNED MESSAGE";

  /**
   * Armor header indicating the start of the message block
   */
  private static final String BEGIN_SIGNATURE = "BEGIN BITCOIN SIGNATURE";
  /**
   * Armor header indicating the start of the message block
   */
  private static final String END_BITCOIN_SIGNATURE = "END BITCOIN SIGNATURE";

  /**
   * Armor header key indicating the version of the signing application
   */
  private static final String VERSION = "Version";
  /**
   * Armor header key indicating a comment that contains free text
   */
  private static final String COMMENT = "Comment";
  /**
   * Armor header key indicating Bitcoin address used in signing
   */
  private static final String ADDRESS = "Address";

  /**
   * @param addressText Text address that was used to sign (makes UI Address conversion code DRY)
   * @param message     The message that was signed
   * @param signature   The signature after signing
   *
   * @return A suitably formatted Bitcoin signed message
   */
  public static String formatAsBitcoinSignedMessage(
    String addressText,
    String message,
    String signature
  ) {

    // Verify the address
    final Address signingAddress;
    try {
      signingAddress = new Address(BitcoinNetwork.current().get(), addressText);
    } catch (AddressFormatException e) {
      return "";
    }

    // Provide PGP-like signature wrapping
    String versionField = VERSION + ": MultiBit HD (" + Configurations.currentConfiguration.getCurrentVersion() + ")";
    String commentField = COMMENT + ": https://multibit.org";
    String addressField = ADDRESS + ": " + signingAddress.toString();

    // Format the string to approximate RFC 2440
    // Layout is address and message outside of signature block
    return String.format(
      "-----%s-----%n%s%n-----%s-----%n%s%n%s%n%s%n%n%s%n-----%s-----%n",
      BEGIN_SIGNED_MESSAGE,
      message,
      BEGIN_SIGNATURE,
      versionField,
      commentField,
      addressField,
      // TODO PGP standard wrapping is at column 65 but not sure if this is required
      signature,
      END_BITCOIN_SIGNATURE
    );

  }


  /**
   * <p>Attempt to parse a signature block into its component fields</p>
   *
   * @param signatureBlock The signed message (could be wrapped in a Bitcoin signed message block)
   *
   * @return A string array
   */
  public static SignedMessage parseSignedMessage(Optional<String> signatureBlock) {

    // Fail fast
    if (!signatureBlock.isPresent()) {
      return new SignedMessage("", "", "", "", "");
    }

    // Attempt to parse as Bitcoin signed message
    String block = signatureBlock.get();

    if ((block.contains(BEGIN_SIGNED_MESSAGE) || block.contains(BEGIN_SIGNATURE))
      && block.contains(END_BITCOIN_SIGNATURE)) {

      // Determine line separator format by examining the first header
      String ls;
      if (block.contains(BEGIN_SIGNED_MESSAGE + "\r\n")) {
        ls = "\r\n";
      } else {
        ls = "\n";
      }
      int lsLength = ls.length();

      // Split the block into lines using line separator
      List<String> lines = Splitter.on(ls).splitToList(block);

      // Parse a fully formatted signature block
      String message = "";
      String version = "";
      String comment = "";
      String address = "";
      String signature = "";

      // Iterate over the lines extracting fields
      int state = 0;

      // Indicate if the line separator has been removed and needs to be replaced
      boolean appendLs = false;

      for (String line : lines) {

        // Use line prefixes to switch state on next line
        if (line.contains(BEGIN_SIGNED_MESSAGE)) {
          state = 1;
          continue;
        }
        if (line.contains(BEGIN_SIGNATURE)) {
          state = 2;
          if (message.length() > 0 && !appendLs) {
            // Remove the line separator from the Armor header
            message = message.substring(0, message.length() - lsLength);
          }
          continue;
        }
        if (line.contains(END_BITCOIN_SIGNATURE)) {
          // No more processing
          break;
        }

        switch (state) {

          case 0:
            // Looking for start of block
            break;
          case 1:
            // Building message
            if (appendLs) {
              // More than one line so replace the line separator
              message += ls;
            }
            if (Strings.isNullOrEmpty(line)) {
              // Blank line so treat as line separator and ignore the rest
              message += ls;
              appendLs = false;
            } else {
              // Normal line
              message += line;
              appendLs = true;
            }
            break;
          case 2:
            // Building signature fields
            if (line.startsWith(VERSION)) {
              // Avoid empty, malformed or untrimmed fields
              version = line.length() > VERSION.length() + 1 ? line.substring((VERSION.length() + 1)).trim() : "";
              continue;
            }
            if (line.startsWith(COMMENT)) {
              // Avoid empty, malformed or untrimmed fields
              comment = line.length() > COMMENT.length() + 1 ? line.substring((COMMENT.length() + 1)).trim() : "";
              continue;
            }
            if (line.startsWith(ADDRESS)) {
              // Avoid empty, malformed or untrimmed fields
              address = line.length() > ADDRESS.length() + 1 ? line.substring((ADDRESS.length() + 1)).trim() : "";
              continue;
            }
            if (Strings.isNullOrEmpty(line.trim())) {
              // Indicates transition to signature if within the signature block
              state = 3;
              continue;
            }
            // Allow for slightly malformed block
            if (!line.contains(":")) {
              // Assume this is part of a signature without a line separator separator
              state = 3;
              signature += line;
              continue;
            }
            break;
          case 3:
            // Building signature (stripping line separator)
            signature += line;
            break;
          default:
            throw new IllegalStateException("Unknown state: " + state);

        }

      }

      // Populate the signed message value object
      return new SignedMessage(
        message,
        address,
        signature,
        version,
        comment
      );

    }

    // Must be a malformed signature block to be here so treat as a message
    return new SignedMessage(block, "", "", "", "");

  }

  /**
   * Signed message result
   */
  public static class SignedMessage {

    private final String message;
    private final String address;
    private final String signature;
    private final String version;
    private final String comment;

    /**
     * @param message   The message that was signed (or the entire signature block if it could not be parsed)
     * @param address   The address used for signing
     * @param signature The signature after signing
     * @param version   The version of the application performing the signing
     * @param comment   Any commentary associated with the signature
     */
    public SignedMessage(String message, String address, String signature, String version, String comment) {

      this.message = Strings.isNullOrEmpty(message) ? "" : message;
      this.address = Strings.isNullOrEmpty(address) ? "" : address;
      this.signature = Strings.isNullOrEmpty(signature) ? "" : signature;
      this.version = Strings.isNullOrEmpty(version) ? "" : version;
      this.comment = Strings.isNullOrEmpty(comment) ? "" : comment;

    }

    /**
     * @return The message (or entire block if signature could not be parsed)
     */
    public String getMessage() {
      return message;
    }

    /**
     * @return The signing address
     */
    public String getAddress() {
      return address;
    }

    /**
     * @return The signature
     */
    public String getSignature() {
      return signature;
    }

    /**
     * @return The version field (usually the signing application and its version)
     */
    public String getVersion() {
      return version;
    }

    /**
     * @return The comment field (usually a link to where the signing application can be found)
     */
    public String getComment() {
      return comment;
    }
  }
}
