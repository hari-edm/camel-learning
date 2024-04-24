package com.edm.camellearning.components;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.aws2.kinesis.Kinesis2Constants;
import org.apache.camel.support.DefaultConsumer;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

public class CamelKclConsumer extends DefaultConsumer {
  private final Scheduler scheduler;

  public CamelKclConsumer(Endpoint endpoint, Processor processor, Scheduler scheduler) {
    super(endpoint, processor);
    this.scheduler = scheduler;
  }

  @Override
  protected void doStart() throws Exception {

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

  private Exchange createExchange(ProcessRecordsInput dataRecord) {

    KinesisClientRecord record = dataRecord.records().getFirst();

    Exchange exchange = createExchange(true);
    exchange.getIn().setBody(record.data());
    exchange
        .getIn()
        .setHeader(Kinesis2Constants.APPROX_ARRIVAL_TIME, record.approximateArrivalTimestamp());
    exchange.getIn().setHeader(Kinesis2Constants.PARTITION_KEY, record.partitionKey());
    exchange.getIn().setHeader(Kinesis2Constants.SEQUENCE_NUMBER, record.sequenceNumber());
    if (record.approximateArrivalTimestamp() != null) {
      long ts = record.approximateArrivalTimestamp().getEpochSecond() * 1000;
      exchange.getIn().setHeader(Kinesis2Constants.MESSAGE_TIMESTAMP, ts);
    }
    return exchange;
  }
}
