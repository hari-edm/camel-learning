package com.edm.camellearning.config;

import com.edm.camellearning.components.*;
import com.edm.camellearning.kcl.KclShardRecordProcessorFactory;
import java.util.UUID;
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

  @Autowired private KclShardRecordProcessorFactory factory;

  @Bean("myCustomEndpoint")
  @Scope("prototype")
  public CamelKclEndpoint myCustomEndpoint(
      @Value("${endpoint}") String uri, CamelKclComponent component) {
    return new CamelKclEndpoint(uri, component, getScheduler());
  }

  @Bean("myCustomProducer")
  @Scope("prototype")
  public CamelKclProducer myCustomProducer(CamelKclEndpoint endpoint) {
    return new CamelKclProducer(endpoint);
  }

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
