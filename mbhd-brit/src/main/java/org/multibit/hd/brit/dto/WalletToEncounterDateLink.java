package org.multibit.hd.brit.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.Date;

/**
 *  <p>DTO to provide the following to BRIT classes:<br>
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
  private final BRITWalletId britWalletId;
  private final Optional<Date> encounterDateOptional;
  private final Optional<Date> firstTransactionDate;

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
}
