package org.multibit.hd.ui.javafx.logging;

import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;

/**
 * <p>Converter to provide the following to logging framework:</p>
 * <ul>
 * <li>Additional information for Throwables</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class PrefixedExtendedThrowableProxyConverter extends PrefixedThrowableProxyConverter {

  @Override
  protected void extraData(StringBuilder builder, StackTraceElementProxy step) {
    if (step != null) {
      ThrowableProxyUtil.subjoinPackagingData(builder, step);
    }
  }
}
