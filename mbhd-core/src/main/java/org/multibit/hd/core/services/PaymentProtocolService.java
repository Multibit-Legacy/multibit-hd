package org.multibit.hd.core.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.store.PaymentsProtobufSerializer;
import org.multibit.hd.core.store.TransactionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * <p>Service to provide the following to GUI classes:</p>
 * <ul>
 * <li>Handle Payment Protocol requests and sessions</li>
 * </ul>
 * <p/>
 */
public class PaymentProtocolService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(PaymentProtocolService.class);

  /**
   * The Bitcoin network parameters
   */
  private final NetworkParameters networkParameters;

  /**
   * The serializer for the backing store
   */
  private PaymentsProtobufSerializer protobufSerializer= new PaymentsProtobufSerializer();

  /**
   * The payment requests in a map, indexed by the bitcoin address
   */
  private final Map<Address, PaymentRequestData> paymentRequestMap = Collections.synchronizedMap(new HashMap<Address, PaymentRequestData>());

  /**
   * The additional transaction information, in the form of a map, index by the transaction hash
   */
  private final ConcurrentMap<String, TransactionInfo> transactionInfoMap = Maps.newConcurrentMap();

  /**
   * Handles payment protocol operations
   */
  private static final ExecutorService executorService = SafeExecutors.newSingleThreadExecutor("wallet-service");

  public PaymentProtocolService(NetworkParameters networkParameters) {

    super();

    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    this.networkParameters = networkParameters;

  }

  @Override
  protected boolean startInternal() {

    return true;
  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    // Always treat as a hard shutdown
    return true;

  }

}
