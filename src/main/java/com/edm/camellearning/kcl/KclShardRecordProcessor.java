package com.edm.camellearning.kcl;

import com.edm.camellearning.components.CamelKclConsumer;
import com.edm.camellearning.components.CamelKclEndpoint;
import com.edm.camellearning.components.CamelKclProcessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;

@Component
@Scope(value = "prototype")
public class KclShardRecordProcessor implements ShardRecordProcessor {

  public KclShardRecordProcessor(ApplicationContext context) {
    this.context = context;
  }

  private static final String SHARD_ID_MDC_KEY = "ShardId";

  @Autowired private ApplicationContext context;

  CamelKclConsumer consumer;

  @PostConstruct
  public void init() {
    consumer = context.getBean(CamelKclConsumer.class);
  }

  private static final Logger log = LoggerFactory.getLogger(KclShardRecordProcessor.class);

  private String shardId;

  public void initialize(InitializationInput initializationInput) {
    shardId = initializationInput.shardId();
    MDC.put(SHARD_ID_MDC_KEY, shardId);
    try {
      consumer =
          (CamelKclConsumer)
              context
                  .getBean("myCustomEndpoint", CamelKclEndpoint.class)
                  .createConsumer(new CamelKclProcessor());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      log.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber());
    } finally {
      MDC.remove(SHARD_ID_MDC_KEY);
    }
  }

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
