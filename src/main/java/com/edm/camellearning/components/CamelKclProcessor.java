package com.edm.camellearning.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


public class CamelKclProcessor implements Processor {
  @Override
  public void process(Exchange exchange) throws Exception {
    System.out.println("Hello" + exchange.getExchangeId());
  }
}
