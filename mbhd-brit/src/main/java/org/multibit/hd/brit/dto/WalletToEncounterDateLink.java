package org.multibit.hd.brit.dto;

import com.google.bitcoin.core.Utils;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Strings;

import java.util.Date;

/**
 *  <p>DTO to provide the following to BRIT classes:</p>
 *  <ul>
 *  <li>This data represents the link between a BRITWalletId and an encounter date</li>
 *  </ul>
 *  Example: BasicMatcher<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class WalletToEncounterDateLink {
  private static final Logger log = LoggerFactory.getLogger(PayerRequest.class);

  private final BRITWalletId britWalletId;
  private final Optional<Date> encounterDateOptional;
  private final Optional<Date> firstTransactionDate;

  private static final char SERIALISER_SEPARATOR = ' ';

  public WalletToEncounterDateLink(BRITWalletId britWalletId, Optional<Date> encounterDateOptional, Optional<Date> firstTransactionDate) {

    Preconditions.checkNotNull(britWalletId, "britWalletId must be supplied");
    Preconditions.checkNotNull(encounterDateOptional, "encounterDateOptional must be supplied");
    Preconditions.checkNotNull(firstTransactionDate, "britWalletId must be supplied");

    this.britWalletId = britWalletId;
    this.encounterDateOptional = encounterDateOptional;
    this.firstTransactionDate = firstTransactionDate;
  }

  public BRITWalletId getBritWalletId() {
    return britWalletId;
  }

  public Optional<Date> getEncounterDateOptional() {
    return encounterDateOptional;
  }

  public Optional<Date> getFirstTransactionDate() {
    return firstTransactionDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WalletToEncounterDateLink that = (WalletToEncounterDateLink) o;

    if (!britWalletId.equals(that.britWalletId)) return false;
    if (encounterDateOptional != null ? !encounterDateOptional.equals(that.encounterDateOptional) : that.encounterDateOptional != null)
      return false;
    if (firstTransactionDate != null ? !firstTransactionDate.equals(that.firstTransactionDate) : that.firstTransactionDate != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = britWalletId.hashCode();
    result = 31 * result + (encounterDateOptional != null ? encounterDateOptional.hashCode() : 0);
    result = 31 * result + (firstTransactionDate != null ? firstTransactionDate.hashCode() : 0);
    return result;
  }

  /**
   * Serialise the contents of the WalletToEncounterDateLink to a String
   *
   * @return String representing the WalletToEncounterDateLink
   */
  public String serialise() {
    StringBuilder builder = new StringBuilder();
    builder.append(Utils.bytesToHexString(britWalletId.getBytes())).append(SERIALISER_SEPARATOR);
    if (encounterDateOptional.isPresent()) {
      builder.append(encounterDateOptional.get().getTime()).append(SERIALISER_SEPARATOR);
    } else {
      builder.append(PayerRequest.OPTIONAL_NOT_PRESENT_TEXT).append(SERIALISER_SEPARATOR);
    }
    if (firstTransactionDate.isPresent()) {
      builder.append(firstTransactionDate.get().getTime());
    } else {
      builder.append(PayerRequest.OPTIONAL_NOT_PRESENT_TEXT);
    }
    log.debug("Serialised walletToEncounterDateLink = \n" + builder.toString());
    return builder.toString();
  }

  public static WalletToEncounterDateLink parse(String serialisedWalletToEncounterDate) {
    log.debug("Attempting to parse walletToEncounterDateLink:\n" + serialisedWalletToEncounterDate);

    if (serialisedWalletToEncounterDate == null) {
      return null;
    }
    String[] rows = Strings.split(serialisedWalletToEncounterDate.trim(), SERIALISER_SEPARATOR);
    if (rows.length == 3) {
      BRITWalletId britWalletId = new BRITWalletId(rows[0]);
      Optional<Date> encounterDateOptional;
      Optional<Date> firstTransactionDateOptional;
      if (PayerRequest.OPTIONAL_NOT_PRESENT_TEXT.equals(rows[1])) {
        encounterDateOptional = Optional.absent();
      } else {
        encounterDateOptional = Optional.of(new Date(Long.parseLong(rows[1])));
      }

      if (PayerRequest.OPTIONAL_NOT_PRESENT_TEXT.equals(rows[2])) {
        firstTransactionDateOptional = Optional.absent();
      } else {
        firstTransactionDateOptional = Optional.of(new Date(Long.parseLong(rows[2])));
      }

      return new WalletToEncounterDateLink(britWalletId, encounterDateOptional, firstTransactionDateOptional);
    } else {
      // Cannot parse
      log.error("Cannot parse"); // TODO throw exception
      return null;
    }
  }
}
