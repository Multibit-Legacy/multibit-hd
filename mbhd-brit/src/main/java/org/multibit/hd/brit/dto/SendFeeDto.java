package org.multibit.hd.brit.dto;

import com.google.common.base.Optional;

/**
 * <p>DTO to provide the following to FeeService:</p>
 * <ul>
 * <li>The details of when to to whom the next BRITwill be sent</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendFeeDto {

  /**
   * The count of sends at which the next fee should be sent.
   * This counts from 0.
   * A value of, say, 5 means that the send at count 5 should add a fee transaction output.
   * If this value is Optional.absent() then it has not been set yet
   */
  private Optional<Integer> sendFeeCount = Optional.absent();

  /**
   * The address to send the next fee to.
   * If this value has not been set yet it will be Optional.absent();
   */
  private Optional<String> sendFeeAddress;

  public SendFeeDto(Optional<Integer> sendFeeCount, Optional<String> sendFeeAddress) {
    this.sendFeeCount = sendFeeCount;
    this.sendFeeAddress = sendFeeAddress;
  }

  public Optional<Integer> getSendFeeCount() {
    return sendFeeCount;
  }

  public Optional<String> getSendFeeAddress() {
    return sendFeeAddress;
  }

  public void setSendFeeCount(Optional<Integer> sendFeeCount) {
    this.sendFeeCount = sendFeeCount;
  }

  public void setSendFeeAddress(Optional<String> sendFeeAddress) {
    this.sendFeeAddress = sendFeeAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SendFeeDto that = (SendFeeDto) o;

    if (sendFeeAddress != null ? !sendFeeAddress.equals(that.sendFeeAddress) : that.sendFeeAddress != null)
      return false;
    if (sendFeeCount != null ? !sendFeeCount.equals(that.sendFeeCount) : that.sendFeeCount != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = sendFeeCount != null ? sendFeeCount.hashCode() : 0;
    result = 31 * result + (sendFeeAddress != null ? sendFeeAddress.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SendFeeDto{" +
      "sendFeeCount=" + sendFeeCount +
      ", sendFeeAddress=" + sendFeeAddress +
      '}';
  }
}
