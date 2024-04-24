package com.edm.camellearning.components;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CamelKclProducer extends DefaultProducer {

  private static final Logger logger = LoggerFactory.getLogger(CamelKclProducer.class);

  public CamelKclProducer(Endpoint endpoint) {
    super(endpoint);
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    String message = UTF_8.newDecoder().decode((ByteBuffer) exchange.getIn().getBody()).toString();
    logger.info("In producer : Received message : {}", message);
  }
}
