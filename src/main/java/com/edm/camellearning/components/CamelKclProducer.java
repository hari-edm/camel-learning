package com.edm.camellearning.components;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

public class CamelKclProducer extends DefaultProducer {

  // Now, I am able to inject some other Spring beans
  // @Autowired
  //  private AnotherSpringBean bean;

  public CamelKclProducer(Endpoint endpoint) {
    super(endpoint);
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    System.out.println("Hello" + exchange.getExchangeId());
  }
}
