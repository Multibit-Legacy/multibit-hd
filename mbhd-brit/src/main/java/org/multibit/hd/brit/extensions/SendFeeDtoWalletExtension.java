package org.multibit.hd.brit.extensions;

import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletExtension;
import com.google.common.base.Optional;
import org.multibit.hd.brit.dto.SendFeeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Strings;

import java.io.UnsupportedEncodingException;

/**
 *  <p>Walet Extension to provide the following to Wallet:<br>
 *  <ul>
 *  <li>Persistence of a SendFeeDto</li>
 *  </ul>
 *  Example:FeeService<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class SendFeeDtoWalletExtension implements WalletExtension {
  private static final Logger log = LoggerFactory.getLogger(SendFeeDtoWalletExtension.class);

  public static final String SEND_FEE_DTO_WALLET_EXTENSION_ID = "org.multibit.hd.brit.SendFeeDto";

  public static final String NOT_PRESENT_MARKER = "not-present";
  public static final char SEPARATOR = ' ';

  private SendFeeDto sendFeeDto;;

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
      if (sendFeeDto.getSendFeeCount().isPresent()) {
         builder.append(sendFeeDto.getSendFeeCount().get().toString()).append(SEPARATOR);
       } else {
         builder.append(NOT_PRESENT_MARKER).append(SEPARATOR);
       }
      try {
        return builder.toString().getBytes("UTF8");
      } catch (UnsupportedEncodingException e) {
        // Will not happen
        e.printStackTrace();
        return new byte[0];
      }
    } else {
    return new byte[0];
  }
}

  @Override
  public void deserializeWalletExtension(Wallet containingWallet, byte[] data) throws Exception {
    String serialisedString = new String(data);
    log.debug("Parsing string '" + serialisedString + "'");

    String[] tokens = Strings.split(serialisedString, SEPARATOR);
    if (tokens != null &&tokens.length >= 2) {
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
      String addressString = tokens[1];
      Optional<String> nextFeeSendAddress = Optional.absent();
      if (!NOT_PRESENT_MARKER.equals(addressString)) {
         nextFeeSendAddress = Optional.of(addressString);
      }
      sendFeeDto = new SendFeeDto(nextFeeSendCount, nextFeeSendAddress);
    } else {
      log.error("Parse failed");
    }
  }

  public SendFeeDto getSendFeeDto() {
    return sendFeeDto;
  }
}
