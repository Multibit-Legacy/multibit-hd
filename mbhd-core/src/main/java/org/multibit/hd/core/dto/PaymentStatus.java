package org.multibit.hd.core.dto;

/**
 * The status of a payment
 * This wraps a RAGStatus and has a depth and detail description information
 */
public class PaymentStatus {

  /**
   * The RAGStatus for the payment
   */
  private RAGStatus status;

  /**
   * The localised status text for the payment. This will typically be 'Unconfirmed', "Confirmed by 2 blocks", "Dead" etc
   */
  private String statusText;

  public PaymentStatus(RAGStatus status) {
    this.status = status;
  }

  public RAGStatus getStatus() {
    return status;
  }

  private int depth = -1; // not set

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public String getStatusText() {
    return statusText;
  }

  public void setStatusText(String statusText) {
    this.statusText = statusText;
  }

  /**
   * Order as RED < PINK < AMBER < GREEN < EMPTY and then order by the depth within GREEN
   *
   * @param other the PaymentStatus to compare to
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
