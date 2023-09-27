package org.springframework.samples.web.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class RestclientSampleApplication {

	public static void main(String[] args) {

		RestClient defaultClient = RestClient.create();



		SpringApplication.run(RestclientSampleApplication.class, args);
	}

}
