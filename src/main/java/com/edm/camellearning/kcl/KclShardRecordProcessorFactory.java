package com.edm.camellearning.kcl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Component
public class KclShardRecordProcessorFactory implements ShardRecordProcessorFactory {
  @Autowired ApplicationContext context;

  public ShardRecordProcessor shardRecordProcessor() {
    return new KclShardRecordProcessor(context);
  }
}
