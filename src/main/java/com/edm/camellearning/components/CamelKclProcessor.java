package com.edm.camellearning.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;

public class CamelKclProcessor implements Processor {

  private static final Logger logger = LoggerFactory.getLogger(CamelKclProcessor.class);

  @Override
  public void process(Exchange exchange) throws Exception {

    String message = UTF_8.newDecoder().decode((ByteBuffer) exchange.getIn().getBody()).toString();
    logger.info("Received message in processor: {}", message);
  }
}
