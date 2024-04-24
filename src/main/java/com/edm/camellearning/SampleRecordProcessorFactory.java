package com.edm.camellearning;


import com.edm.camellearning.components.MyCustomConsumer;
import com.edm.camellearning.components.MyCustomEndpoint;
import com.edm.camellearning.components.MyCustomProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;


@Component
public class SampleRecordProcessorFactory implements ShardRecordProcessorFactory {
    @Autowired
    ApplicationContext context;
    @Autowired
    Scheduler scheduler;
    public ShardRecordProcessor shardRecordProcessor() {
    return new MyCustomConsumer(
        context.getBean("myCustomEndpoint", MyCustomEndpoint.class), new MyCustomProcessor(),scheduler);
    }
}