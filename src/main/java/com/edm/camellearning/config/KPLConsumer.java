package com.edm.camellearning.config;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import software.amazon.kinesis.coordinator.Scheduler;

public class KPLConsumer implements Consumer {

  private Processor processor;
  private Endpoint endpoint;
  private Scheduler scheduler;

  public KPLConsumer(Processor processor, Endpoint endpoint, Scheduler scheduler) {
    this.processor = processor;
    this.endpoint = endpoint;
    this.scheduler = scheduler;
  }

  @Override
  public Processor getProcessor() {
    return processor;
  }

  @Override
  public Exchange createExchange(boolean autoRelease) {
    return null;
  }

  @Override
  public void releaseExchange(Exchange exchange, boolean autoRelease) {}

  @Override
  public Endpoint getEndpoint() {
    return endpoint;
  }

  @Override
  public void start() {
    scheduler.run();
  }

  @Override
  public void stop() {
    scheduler.shutdown();
  }
}
