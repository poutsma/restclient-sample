package org.springframework.samples.web.client;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * @author Arjen Poutsma
 */
public class BasicRestClientTest extends MockWebServerTest {

	private RestClient restClient;


	@BeforeEach
	void createClient() {
		this.restClient = RestClient.builder()
				.baseUrl(this.server.url("/").toString())
				.build();
	}

	@Test
	void basic() {
		prepareResponse(response -> response.setBody("Hello World"));

		String body = restClient.get()
				.retrieve()
				.body(String.class);

		System.out.println(body);
	}

	@Test
	void json() {
		prepareResponse(response -> response
				.setHeader("Content-Type", "application/json")
				.setBody("{\"name\":\"Arjen\",\"age\":\"48\"}"));

		Person person = restClient.get()
				.retrieve()
				.body(Person.class);


		System.out.println(person);
	}

	@Test
	void responseEntity() {
		prepareResponse(response -> {
			response.setResponseCode(201);
			response.addHeader("foo", "bar");
			response.setBody("Hello World");
		});

		ResponseEntity<String> entity = restClient.get()
				.retrieve()
				.toEntity(String.class);

		System.out.println("status:" + entity.getStatusCode());
		System.out.println("headers:" + entity.getHeaders());
		System.out.println("body:" + entity.getBody());
	}

	@Test
	void post() {
		prepareResponse(response -> response.setResponseCode(201));

		Person person = new Person();
		person.setName("Arjen");
		person.setAge(48);

		ResponseEntity<Void> entity = restClient.post()
				.body(person)
				.retrieve()
				.toBodilessEntity();

		System.out.println("response status:" + entity.getStatusCode());
		System.out.println("response headers:" + entity.getHeaders());
		System.out.println("response body:" + entity.getBody());
		System.out.println();

		expectRequest(request -> {
			System.out.println("request headers:" + request.getHeaders());
			System.out.println("request body:" + request.getBody().readUtf8());
		});
	}
}
