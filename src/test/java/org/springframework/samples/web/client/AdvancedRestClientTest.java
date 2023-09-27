package org.springframework.samples.web.client;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Arjen Poutsma
 */
public class AdvancedRestClientTest extends MockWebServerTest {

	private RestClient restClient;


	@BeforeEach
	void createClient() {
		this.restClient = RestClient.builder()
				.baseUrl(this.server.url("/").toString())
				.build();
	}

	@Test
	void parameterized() {
		prepareResponse(response -> response
				.setHeader("Content-Type", "application/json")
				.setBody("""
						[{"name":"Arjen","age":"48"},
						 {"name":"Juergen","age":"47"},
						 {"name":"Sebastien","age":"35"}]
						 """));

		List<Person> result = restClient.get()
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});

		System.out.println("result = " + result);

	}

	@Test
	void errorHandling() {
		prepareResponse(response -> {
			response.setResponseCode(418);
			response.setBody("I'm a teapot");
		});

		restClient.get()
				.retrieve()
				.onStatus(status -> status.equals(I_AM_A_TEAPOT), (request, response) -> {
					throw new TeaPotException();
				})
				.body(String.class);
	}

	@Test
	void exchange() {
		prepareResponse(response -> {
			response.setResponseCode(418);
			response.setBody("Hello World");
		});

		String result = restClient.get()
				.exchange((request, response) -> {
					if (response.getStatusCode().equals(I_AM_A_TEAPOT)) {
						throw new TeaPotException();
					}
					else {
						StringHttpMessageConverter converter = new StringHttpMessageConverter();
						return converter.read(String.class, response);
					}

				});

		System.out.println("result = " + result);
	}

	@Test
	void builder() {
		RestClient restClient = this.restClient.mutate()
				.requestFactory(new HttpComponentsClientHttpRequestFactory())
				.requestInitializer(new MyRequestInitializer())
				.build();

		prepareResponse(response -> {
			response.setBody("Hello World");
		});

		String body = restClient.get()
				.retrieve()
				.body(String.class);

		System.out.println(body);

		expectRequest(request -> {
			System.out.println("request headers:" + request.getHeaders());
		});
	}



	public static class TeaPotException extends RuntimeException {

	}

	public static class MyRequestInitializer implements ClientHttpRequestInitializer {

		@Override
		public void initialize(ClientHttpRequest request) {
			request.getHeaders().add("Foo", "Bar");
		}
	}


}
