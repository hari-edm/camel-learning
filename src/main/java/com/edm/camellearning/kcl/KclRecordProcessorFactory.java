package com.edm.camellearning.kcl;

import com.edm.camellearning.components.CamelKclConsumer;
import com.edm.camellearning.components.CamelKclEndpoint;
import com.edm.camellearning.components.CamelKclProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Component
public class KclRecordProcessorFactory implements ShardRecordProcessorFactory {
  @Autowired ApplicationContext context;
  @Autowired Scheduler scheduler;

  public ShardRecordProcessor shardRecordProcessor() {
    return new CamelKclConsumer(
        context.getBean("myCustomEndpoint", CamelKclEndpoint.class),
        new CamelKclProcessor(),
        scheduler);
  }
}
