package com.edm.camellearning.components;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyCustomComponent extends DefaultComponent {

  @Autowired private BeanFactory beanFactory;

  @Override
  protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
      throws Exception {
    MyCustomEndpoint endpoint =
        (MyCustomEndpoint) beanFactory.getBean("myCustomEndpoint", uri, this);
    setProperties(endpoint, parameters);
    return endpoint;
  }
}
