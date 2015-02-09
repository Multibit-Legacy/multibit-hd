package org.multibit.hd.testing;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Utils;
import org.multibit.hd.hardware.core.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Low level message event fixtures to provide the following to FEST tests:</p>
 * <ul>
 * <li>Various standard objects to act as payloads for the events</li>
 * </ul>
 * <p>These fixtures provide the low level message events that represent spontaneous events
 * associated with the device. In reality these messages would be triggered by the device
 * completing an operation or the user interacting with the device which then issues the
 * event to trigger the next step in a process.</p>
 *
 * <p>By addressing low level events the FSM within MultiBit Hardware will be correctly
 * exercised ensuring that the closest approximation to a real device is maintained.</p>
 *
 * @since 0.0.5
 *  
 */
public class MessageEventFixtures {

  public static final Logger log = LoggerFactory.getLogger(MessageEventFixtures.class);

  /**
   * The standard label for a hardware wallet
   */
  public static final String STANDARD_LABEL = "Example";

  /**
   * @return An "initialised" Features for use with FEST testing (abandon wallet)
   */
  public static Features newStandardFeatures() {

    Features features = new Features();
    features.setVendor("bitcointrezor.com");
    features.setVersion("1.3.0");
    features.setBootloaderMode(false);
    features.setDeviceId("D18894FA25FA90CD589EDE57");
    features.setPinProtection(true);
    features.setPassphraseProtection(false);
    features.setLanguage("english");
    features.setLabel(STANDARD_LABEL);
    features.setCoins(Lists.newArrayList("Bitcoin", "Testnet", "Namecoin", "Litecoin"));
    features.setInitialized(true);
    features.setRevision(Utils.HEX.decode("524f2a957afb66e6a869384aceaca1cb7f9cba60"));
    features.setBootloaderHash(Utils.HEX.decode("c4c32539b4a025a8e753a4c46264285911a45fcb14f4718179e711b1ce990524"));
    features.setImported(false);

    return features;

  }

  /**
   * @return A "wiped" Features for use with FEST testing (abandon wallet)
   */
  public static Features newWipedFeatures() {

    Features features = new Features();
    features.setVendor("bitcointrezor.com");
    features.setVersion("1.3.0");
    features.setBootloaderMode(false);
    features.setDeviceId("D18894FA25FA90CD589EDE57");
    features.setPinProtection(false);
    features.setPassphraseProtection(false);
    features.setLanguage("english");
    features.setLabel(STANDARD_LABEL);
    features.setCoins(Lists.newArrayList("Bitcoin", "Testnet", "Namecoin", "Litecoin"));
    features.setInitialized(false);
    features.setRevision(Utils.HEX.decode("524f2a957afb66e6a869384aceaca1cb7f9cba60"));
    features.setBootloaderHash(Utils.HEX.decode("c4c32539b4a025a8e753a4c46264285911a45fcb14f4718179e711b1ce990524"));
    features.setImported(false);

    return features;

  }

  /**
   * @return An "initialised" Features for use with FEST testing (abandon wallet) with unsupported firmware (1.2.0)
   */
  public static Features newUnsupportedFirmwareFeatures() {

    Features features = new Features();
    features.setVendor("bitcointrezor.com");
    features.setVersion("1.2.1");
    features.setBootloaderMode(false);
    features.setDeviceId("D18894FA25FA90CD589EDE57");
    features.setPinProtection(true);
    features.setPassphraseProtection(false);
    features.setLanguage("english");
    features.setLabel(STANDARD_LABEL);
    features.setCoins(Lists.newArrayList("Bitcoin", "Testnet", "Namecoin", "Litecoin"));
    features.setInitialized(true);
    features.setRevision(Utils.HEX.decode("524f2a957afb66e6a869384aceaca1cb7f9cba60"));
    features.setBootloaderHash(Utils.HEX.decode("c4c32539b4a025a8e753a4c46264285911a45fcb14f4718179e711b1ce990524"));
    features.setImported(false);

    return features;

  }

  /**
   * @return A new device reset success (wallet created)
   */
  public static Success newDeviceResetSuccess() {
    return new Success("Device reset");
  }

  /**
   * @return A new device wiped success
   */
  public static Success newDeviceWipedSuccess() {

    return new Success("Device wiped");

  }

  /**
   * @return A new cipher key success ("abandon" wallet)
   */
  public static CipheredKeyValue newCipheredKeyValue() {

    return new CipheredKeyValue(true, Utils.HEX.decode("ec406a3c796099050400f65ab311363e"));

  }

  /**
   * @return A new PIN entry failure
   */
  public static Failure newPinFailure() {

    return new Failure(
      FailureType.PIN_INVALID,
      ""
    );
  }

  /**
   * @return A new confirm wipe button request
   */
  public static ButtonRequest newWipeDeviceButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.WIPE_DEVICE,
      ""
    );

  }

  /**
   * @return A new confirm word button request
   */
  public static ButtonRequest newConfirmWordButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.CONFIRM_WORD,
      ""
    );

  }

  /**
   * @return A new "other" button request (entropy request etc)
   */
  public static ButtonRequest newOtherButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.OTHER,
      ""
    );

  }

  /**
   * @return A new "protect call" button request (cipher key etc)
   */
  public static ButtonRequest newProtectCallButtonRequest() {

    return new ButtonRequest(
      ButtonRequestType.PROTECT_CALL,
      ""
    );

  }

  /**
   * @return A new PIN matrix for "current" (unlock)
   */
  public static PinMatrixRequest newCurrentPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.CURRENT);

  }

  /**
   * @return A new PIN matrix for "new first" (set)
   */
  public static PinMatrixRequest newNewFirstPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.NEW_FIRST);

  }

  /**
   * @return A new PIN matrix for "new second" (confirm)
   */
  public static PinMatrixRequest newNewSecondPinMatrix() {

    return new PinMatrixRequest(PinMatrixRequestType.NEW_SECOND);

  }

  /**
   * @return A new standard public key for M (abandon)
   */
  public static PublicKey newStandardPublicKey_M() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03d902f35f560e0470c63313c7369168d9d7df2d49bf295fd9fb7cb109ccee0494"),
      false,
      null,
      true,
      Utils.HEX.decode("7923408dadd3c7b56eed15567707ae5e5dca089de972e07f3b860450e2a3b70e"),
      true,
      0,
      true,
      0,
      true,
      0
    );

    return new PublicKey(
      true,
      "xpub661MyMwAqRbcFkPHucMnrGNzDwb6teAX1RbKQmqtEF8kK3Z7LZ59qafCjB9eCRLiTVG3uxBxgKvRgbubRhqSKXnGGb1aoaqLrpMBDrVxga8",
      Utils.HEX.decode(
        "0488b21e0000000000000000007923408dadd3c7b56eed15567707ae5e5dca089de972e07f3b860450e2a3b70e03d902f35f560e0470c63313c7369168d9d7df2d49bf295fd9fb7cb109ccee0494c7fe61f5"),
      true,
      hdNodeType
    );
  }

  /**
   * @return A new standard public key for M/44H (abandon)
   */
  public static PublicKey newStandardPublicKey_M_44H() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03428a2da3e76291667a67a38ed45468ceb0d156bc8beda6e86fbc4cf295087c2b"),
      false,
      null,
      true,
      Utils.HEX.decode("45d3b0e8206db10a08d555317c7e245c5bbd12254ce968f3c79a959d4e6af98a"),
      true,
      0x8000002c,
      true,
      1,
      true,
      0x73c5da0a
    );

    return new PublicKey(
      true,
      "xpub68jrRzQopSUSfYDVF7r6KMbite5ge2zei1y94YhzTbJvt9wUC9DXaEPfvmcz7E5XdgQYTvUqehtjSM3Zvc4MadbTzabTNZvWq12kjzkKA3b",
      Utils.HEX.decode(
        "0488b21e0173c5da0a8000002c45d3b0e8206db10a08d555317c7e245c5bbd12254ce968f3c79a959d4e6af98a03428a2da3e76291667a67a38ed45468ceb0d156bc8beda6e86fbc4cf295087c2b1a4472fa"),
      true,
      hdNodeType
    );
  }

  /**
   * @return A new standard public key for M/44H/0H (abandon)
   */
  public static PublicKey newStandardPublicKey_M_44H_0H() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03f72f0e3684b0d7295f391616f12a469070bfcd175c55366239047495a2c1c410"),
      false,
      null,
      true,
      Utils.HEX.decode("af0894dc5f2d5bed0dc85b2fd2053a98575765c144e4e64126ee1009b38860b2"),
      true,
      0x80000000,
      true,
      2,
      true,
      0x88b3582b
    );

    return new PublicKey(
      true,
      "xpub6AmukNpN4yyLgyzSysjU6JqqoYA1mVUvtinHYdBGPDppatJXHxT8CcDsmBo9n3yLBgrcw9z62ygt1siT9xai4UaJ2w4FPmY6kPCF96YN2cF",
      Utils.HEX.decode(
        "0488b21e0288b3582b80000000af0894dc5f2d5bed0dc85b2fd2053a98575765c144e4e64126ee1009b38860b203f72f0e3684b0d7295f391616f12a469070bfcd175c55366239047495a2c1c4101d4fcb78"),
      true,
      hdNodeType
    );
  }

  /**
   * @return A new standard public key for M/44H/0H/0H (abandon)
   */
  public static PublicKey newStandardPublicKey_M_44H_0H_0H() {

    HDNodeType hdNodeType = new HDNodeType(
      true,
      Utils.HEX.decode("03774c910fcf07fa96886ea794f0d5caed9afe30b44b83f7e213bb92930e7df4bd"),
      false,
      null,
      true,
      Utils.HEX.decode("3da4bc190a2680111d31fadfdc905f2a7f6ce77c6f109919116f253d43445219"),
      true,
      0x80000000,
      true,
      3,
      true,
      0x155bca59
    );

    return new PublicKey(
      true,
      "xpub6BosfCnifzxcFwrSzQiqu2DBVTshkCXacvNsWGYJVVhhawA7d4R5WSWGFNbi8Aw6ZRc1brxMyWMzG3DSSSSoekkudhUd9yLb6qx39T9nMdj",
      Utils.HEX.decode(
        "0488b21e03155bca59800000003da4bc190a2680111d31fadfdc905f2a7f6ce77c6f109919116f253d4344521903774c910fcf07fa96886ea794f0d5caed9afe30b44b83f7e213bb92930e7df4bdc84b94ea"),
      true,
      hdNodeType
    );
  }
}
