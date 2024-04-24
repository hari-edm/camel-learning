package com.edm.camellearning.routes;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class KdsToKdsRoute extends RouteBuilder {
    Logger logger = LoggerFactory.getLogger(KdsToKdsRoute.class);
    @Override
    public void configure() throws Exception {

     //   from("aws2-kinesis://my-test-input-kds?useDefaultCredentialsProvider=true")
       from("myscheme://my-test-input-kds")
              //  .to("log://abcd");
                .to("myscheme://abcd");
               // .to("aws2-kinesis://my-test-producer-kds?useDefaultCredentialsProvider=true");
                /*.to("seda:process");*/

       /* from("seda:process?concurrentConsumers=5") //.to("log://abcd");
                .process(new Processor() {

                    public void process(Exchange exchange) throws Exception {
                        logger.info(exchange.getMessage().getBody().toString());
                        System.out.println(Thread.currentThread().getName() + " " +exchange.getMessage().getBody());
                    }
                });*/

    }
}
