package org.multibit.hd.brit.extensions;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.WalletExtension;
import org.bitcoinj.params.MainNetParams;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.multibit.hd.brit.dto.SendFeeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Strings;

/**
 * <p>Wallet Extension to provide the following to Wallet:</p>
 * <ul>
 * <li>Persistence of a SendFeeDto</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendFeeDtoWalletExtension implements WalletExtension {

  private static final Logger log = LoggerFactory.getLogger(SendFeeDtoWalletExtension.class);

  public static final String SEND_FEE_DTO_WALLET_EXTENSION_ID = "org.multibit.hd.brit.SendFeeDto";

  public static final String NOT_PRESENT_MARKER = "not-present";
  public static final char SEPARATOR = ' ';

  private SendFeeDto sendFeeDto;

  public SendFeeDtoWalletExtension() {
    this.sendFeeDto = null;
  }

  public SendFeeDtoWalletExtension(SendFeeDto sendFeeDto) {
    this.sendFeeDto = sendFeeDto;
  }

  @Override
  public String getWalletExtensionID() {
    return SEND_FEE_DTO_WALLET_EXTENSION_ID;
  }

  @Override
  public boolean isWalletExtensionMandatory() {
    return false;
  }

  @Override
  public byte[] serializeWalletExtension() {
    if (sendFeeDto != null) {

      StringBuilder builder = new StringBuilder();

      if (sendFeeDto.getSendFeeCount().isPresent()) {
        builder.append(sendFeeDto.getSendFeeCount().get().toString()).append(SEPARATOR);
      } else {
        builder.append(NOT_PRESENT_MARKER).append(SEPARATOR);
      }

      if (sendFeeDto.getSendFeeAddress().isPresent()) {
        builder.append(sendFeeDto.getSendFeeAddress().get()).append(SEPARATOR);
      } else {
        builder.append(NOT_PRESENT_MARKER).append(SEPARATOR);
      }

      return builder.toString().getBytes(Charsets.UTF_8);

    } else {
      return new byte[0];
    }
  }

  @Override
  public void deserializeWalletExtension(Wallet containingWallet, byte[] data) throws Exception {

    String serialisedString = new String(data, Charsets.UTF_8);
    log.debug("Parsing string '{}'", serialisedString);

    String[] tokens = Strings.split(serialisedString, SEPARATOR);
    if (tokens != null && tokens.length >= 2) {

      String countString = tokens[0];
      Optional<Integer> nextFeeSendCount = Optional.absent();

      if (!NOT_PRESENT_MARKER.equals(countString)) {
        try {
          nextFeeSendCount = Optional.of(Integer.parseInt(countString));
        } catch (NumberFormatException nfe) {
          // return Optional.absent();
          nfe.printStackTrace();
        }
      }

      String rawAddress = tokens[1];
      Optional<Address> nextFeeSendAddress = Optional.absent();

      if (!NOT_PRESENT_MARKER.equals(rawAddress)) {
        nextFeeSendAddress = Optional.of(new Address(MainNetParams.get(), rawAddress));
      }

      sendFeeDto = new SendFeeDto(nextFeeSendCount, nextFeeSendAddress);
    } else {
      log.error("Parse failed");
    }
  }

  public SendFeeDto getSendFeeDto() {
    return sendFeeDto;
  }

  @Override
  public String toString() {
    return "SendFeeDtoWalletExtension{" +
            "sendFeeDto=" + sendFeeDto +
            '}';
  }
}
