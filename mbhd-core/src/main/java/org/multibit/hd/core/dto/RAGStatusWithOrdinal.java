package org.multibit.hd.core.dto;

public class RAGStatusWithOrdinal {

  /**
   * Red means stop, critical, user must take action to fix
   */
  public static final int RED = 0;

  /**
   * Pink means pending, no action required but keep an eye on things
   */
  public static final int PINK = 1;

  /**
   * Amber means warning, interested users may want to explore further
   */
  public static final int AMBER = 2;

  /**
   * Green means go, no action required all is well
   */
  public static final int GREEN = 3;

  /**
   * Empty indicates there is no applicable RAG status
   */
  public static final int EMPTY = 4;

  private int status;

  public RAGStatusWithOrdinal(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  private int ordinal = -1; // not set

  public int getOrdinal() {
    return ordinal;
  }

  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }

  /**
   * Order as RED < PINK < AMBER < GREEN < EMPTY and then order by the ordinal within GREEN
   *
   * @param other the RAGStatusWithOrdinal to compare to
   * @return the relative order (as per Comparable)
   */
  public int compareToWithOrdinal(RAGStatusWithOrdinal other) {
    switch (this.getStatus()) {
      case RED: {
        if (other.getStatus() == RAGStatusWithOrdinal.RED) {
          return 0;
        } else {
          return -1;
        }
      }
      case PINK: {
        if (other.getStatus() == RAGStatusWithOrdinal.RED) {
          return 1;
        } else {
          if (other.getStatus() == RAGStatusWithOrdinal.PINK) {
            return 0;
          } else {
            return -1;
          }
        }
      }
      case AMBER: {
        if (other.getStatus() == RAGStatusWithOrdinal.RED || other.getStatus() == RAGStatusWithOrdinal.PINK) {
          return 1;
        } else {
          if (other.getStatus() == RAGStatusWithOrdinal.AMBER) {
            return 0;
          } else {
            return -1;
          }
        }
      }
      case GREEN: {
        if (other.getStatus() == RAGStatusWithOrdinal.RED || other.getStatus() == RAGStatusWithOrdinal.PINK || other.getStatus() == RAGStatusWithOrdinal.AMBER) {
          return 1;
        } else {
          if (other.getStatus() == RAGStatusWithOrdinal.GREEN) {
            return new Integer(this.getOrdinal()).compareTo(other.getOrdinal());
          } else {
            return -1;
          }
        }
      }
      case EMPTY: {
        if (other.getStatus() == RAGStatusWithOrdinal.EMPTY) {
          return 0;
        } else {
          return 1;
        }
      }
    }
    return 0;
  }
}
