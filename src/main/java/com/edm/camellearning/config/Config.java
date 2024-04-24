package com.edm.camellearning.config;

import com.edm.camellearning.SampleRecordProcessorFactory;
import com.edm.camellearning.components.*;

import java.util.UUID;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

@Configuration
public class Config {

  @Value("${aws.region}")
  private String region;

  @Value("${kds.name}")
  private String streamName;

  @Autowired private SampleRecordProcessorFactory factory;


  @Bean("myCustomEndpoint")
  @Scope("prototype")
  public MyCustomEndpoint myCustomEndpoint(String uri, MyCustomComponent component) {
    MyCustomEndpoint endpoint = new MyCustomEndpoint(uri, component,getScheduler());
    return endpoint;
  }

  @Bean("myCustomProducer")
  @Scope("prototype")
  public MyCustomProducer myCustomProducer(MyCustomEndpoint endpoint) {
    return new MyCustomProducer(endpoint);
  }





  /* @Bean("schd")
  public ExecutorService service(){
      ThreadPoolProfileBuilder builder = new ThreadPoolProfileBuilder("only5threads");
      ThreadPoolProfile threadPoolProfile = builder.poolSize(5).maxPoolSize(5).maxQueueSize(-1).build();
      DefaultExecutorServiceManager defaultExecutorServiceManager = new DefaultExecutorServiceManager(camelContext);
      defaultExecutorServiceManager.setDefaultThreadPoolProfile(threadPoolProfile);
      ExecutorService executorService = defaultExecutorServiceManager.newDefaultScheduledThreadPool("only5threads","only5threads");
  }*/

  @Bean
  public Scheduler getScheduler() {

    KinesisAsyncClient kinesisClient =
        KinesisClientUtil.createKinesisAsyncClient(
            KinesisAsyncClient.builder().region(Region.of(region)));
    DynamoDbAsyncClient dynamoClient =
        DynamoDbAsyncClient.builder().region(Region.of(region)).build();
    CloudWatchAsyncClient cloudWatchClient =
        CloudWatchAsyncClient.builder().region(Region.of(region)).build();
    ConfigsBuilder configsBuilder =
        new ConfigsBuilder(
            streamName,
            streamName,
            kinesisClient,
            dynamoClient,
            cloudWatchClient,
            UUID.randomUUID().toString(),
            factory);

    return new Scheduler(
        configsBuilder.checkpointConfig(),
        configsBuilder.coordinatorConfig(),
        configsBuilder.leaseManagementConfig(),
        configsBuilder.lifecycleConfig(),
        configsBuilder.metricsConfig(),
        configsBuilder.processorConfig(),
        configsBuilder
            .retrievalConfig()
            .retrievalSpecificConfig(new PollingConfig(streamName, kinesisClient)));
  }
}
