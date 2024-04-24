package com.edm.camellearning.components;

import com.edm.camellearning.kcl.KclRecordProcessor;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;

//@Component
public class CamelKclConsumer extends DefaultConsumer implements ShardRecordProcessor {
  private final Scheduler scheduler;

  public CamelKclConsumer(Endpoint endpoint, Processor processor, Scheduler scheduler) {
    super(endpoint, processor);
    this.scheduler = scheduler;
  }

  @Override
  protected void doStart() throws Exception {
    // LOG.debug("Starting consumer: {}", this);
    scheduler.run();
  }


  public void readMessage(ProcessRecordsInput processRecordsInput) {
    Exchange exchange = createExchange(processRecordsInput);
    try {
      getProcessor().process(exchange);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Exchange createExchange(ProcessRecordsInput processRecordsInput) {
    return null;
  }




  private static final String SHARD_ID_MDC_KEY = "ShardId";

  @Autowired
  private ApplicationContext beanFactory;

  CamelKclConsumer consumer;

  // @Autowired ConsumerBridge bridge;

  private static final Logger log = LoggerFactory.getLogger(KclRecordProcessor.class);

  private String shardId;

  /**
   * Invoked by the KCL before data records are delivered to the ShardRecordProcessor instance (via
   * processRecords). In this example we do nothing except some logging.
   *
   * @param initializationInput Provides information related to initialization.
   */
  public void initialize(InitializationInput initializationInput) {
    shardId = initializationInput.shardId();
    MDC.put(SHARD_ID_MDC_KEY, shardId);
    try {
      consumer = (CamelKclConsumer) beanFactory.getBean("myCustomEndpoint", CamelKclEndpoint.class).createConsumer(new CamelKclProcessor());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      log.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber());
    } finally {
      MDC.remove(SHARD_ID_MDC_KEY);
    }
  }

  /**
   * Handles record processing logic. The Amazon Kinesis Client Library will invoke this method to
   * deliver data records to the application. In this example we simply log our records.
   *
   * @param processRecordsInput Provides the records to be processed as well as information and
   *     capabilities related to them (e.g. checkpointing).
   */
  public void processRecords(ProcessRecordsInput processRecordsInput) {
    MDC.put(SHARD_ID_MDC_KEY, shardId);
    try {
      log.info("Processing {} record(s)", processRecordsInput.records().size());
      processRecordsInput
              .records()
              .forEach(
                      r ->
                              log.info(
                                      "Processing record pk: {} -- Seq: {}", r.partitionKey(), r.sequenceNumber()));
      consumer.readMessage(processRecordsInput);
    } catch (Throwable t) {
      log.error("Caught throwable while processing records. Aborting.");
      Runtime.getRuntime().halt(1);
    } finally {
      MDC.remove(SHARD_ID_MDC_KEY);
    }
  }

  /**
   * Called when the lease tied to this record processor has been lost. Once the lease has been
   * lost, the record processor can no longer checkpoint.
   *
   * @param leaseLostInput Provides access to functions and data related to the loss of the lease.
   */
  public void leaseLost(LeaseLostInput leaseLostInput) {
    MDC.put(SHARD_ID_MDC_KEY, shardId);
    try {
      log.info("Lost lease, so terminating.");
    } finally {
      MDC.remove(SHARD_ID_MDC_KEY);
    }
  }

  @Override
  public void shardEnded(ShardEndedInput shardEndedInput) {
    MDC.put(SHARD_ID_MDC_KEY, shardId);
    try {
      log.info("Reached shard end checkpointing.");
      shardEndedInput.checkpointer().checkpoint();
    } catch (ShutdownException | InvalidStateException e) {
      log.error("Exception while checkpointing at shard end. Giving up.", e);
    } finally {
      MDC.remove(SHARD_ID_MDC_KEY);
    }
  }

  /**
   * Invoked when Scheduler has been requested to shut down (i.e. we decide to stop running the app
   * by pressing Enter). Checkpoints and logs the data a final time.
   *
   * @param shutdownRequestedInput Provides access to a checkpointer, allowing a record processor to
   *     checkpoint before the shutdown is completed.
   */
  public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
    MDC.put(SHARD_ID_MDC_KEY, shardId);
    try {
      log.info("Scheduler is shutting down, checkpointing.");
      shutdownRequestedInput.checkpointer().checkpoint();
    } catch (ShutdownException | InvalidStateException e) {
      log.error("Exception while checkpointing at requested shutdown. Giving up.", e);
    } finally {
      MDC.remove(SHARD_ID_MDC_KEY);
    }
  }
}
