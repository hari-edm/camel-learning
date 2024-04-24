package com.edm.camellearning.components;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.kinesis.coordinator.Scheduler;

public class CamelKclEndpoint extends DefaultEndpoint {

    @Autowired
    private BeanFactory beanFactory;

    private Scheduler scheduler ;

    public CamelKclEndpoint(String uri, CamelKclComponent component, Scheduler scheduler) {
        super(uri, component);
        this.scheduler = scheduler;
    }

    @Override
    public Producer createProducer() throws Exception {
        CamelKclProducer producer = (CamelKclProducer) beanFactory.getBean("myCustomProducer",  this);
        return producer;
    }



    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
    return new CamelKclConsumer(this, new CamelKclProcessor(),this.scheduler);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}