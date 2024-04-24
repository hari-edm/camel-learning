package com.edm.camellearning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.kinesis.coordinator.Scheduler;

@SpringBootApplication
public class CamelLearningApplication {



	public static void main(String[] args) {
		SpringApplication.run(CamelLearningApplication.class, args);
	}


}
