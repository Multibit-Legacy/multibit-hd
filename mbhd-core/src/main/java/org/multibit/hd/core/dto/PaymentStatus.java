package org.multibit.hd.core.dto;

/**
 * The status of a payment
 * This wraps a RAGStatus and has a depth and detail description information
 */
public class PaymentStatus {

  /**
   * The RAG status for the payment
   */
  private final RAGStatus status;

  /**
   * The localisation key for the status text for the payment. This will typically be the localisation key for 'Unconfirmed', "Confirmed by 2 blocks", "Dead" etc
   */
  private final CoreMessageKey statusKey;

  /**
   * The localisation data for the status text for the payment
   */
  private Object[] statusData = null;

  /**
   * @param status    The RAG status for quick assessment
   * @param statusKey The localisation key for the status text for the payment. This will typically be the localisation key for 'Unconfirmed', "Confirmed by 2 blocks", "Dead" etc
   */
  public PaymentStatus(RAGStatus status, CoreMessageKey statusKey) {
    this.status = status;
    this.statusKey = statusKey;
  }

  /**
   * @return The RAG status for quick assessment
   */
  public RAGStatus getStatus() {
    return status;
  }

  private int depth = -1; // not set

  /**
   * @return The depth in the blockchain
   */
  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  /**
   * @return The core message key providing the appropriate status message (e.g. "Confirmed by several blocks" etc)
   */
  public CoreMessageKey getStatusKey() {
    return statusKey;
  }

  public Object[] getStatusData() {
    return statusData;
  }

  public void setStatusData(Object[] statusData) {
    this.statusData = statusData;
  }

  /**
   * Order as RED < PINK < AMBER < GREEN < EMPTY and then order by the depth within GREEN
   *
   * @param other the PaymentStatus to compare to
   *
   * @return the relative order (as per Comparable)
   */
  public int compareToWithOrdinal(PaymentStatus other) {
    switch (this.getStatus()) {
      case RED: {
        if (other.getStatus() == RAGStatus.RED) {
          return 0;
        } else {
          return -1;
        }
      }
      case PINK: {
        if (other.getStatus() == RAGStatus.RED) {
          return 1;
        } else {
          if (other.getStatus() == RAGStatus.PINK) {
            return 0;
          } else {
            return -1;
          }
        }
      }
      case AMBER: {
        if (other.getStatus() == RAGStatus.RED || other.getStatus() == RAGStatus.PINK) {
          return 1;
        } else {
          if (other.getStatus() == RAGStatus.AMBER) {
            return 0;
          } else {
            return -1;
          }
        }
      }
      case GREEN: {
        if (other.getStatus() == RAGStatus.RED || other.getStatus() == RAGStatus.PINK || other.getStatus() == RAGStatus.AMBER) {
          return 1;
        } else {
          if (other.getStatus() == RAGStatus.GREEN) {
            return new Integer(this.getDepth()).compareTo(other.getDepth());
          } else {
            return -1;
          }
        }
      }
      case EMPTY: {
        if (other.getStatus() == RAGStatus.EMPTY) {
          return 0;
        } else {
          return 1;
        }
      }
    }
    return 0;
  }
}
