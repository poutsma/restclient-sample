package org.springframework.samples.web.client;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author Arjen Poutsma
 */
public class MockWebServerTest {

	protected MockWebServer server;

	@BeforeEach
	protected final void startServer() throws IOException {
		this.server = new MockWebServer();
		this.server.start();
	}

	@AfterEach
	protected final void stopServer() throws IOException {
		this.server.shutdown();
	}
	protected final void prepareResponse(Consumer<MockResponse> consumer) {
		MockResponse response = new MockResponse();
		consumer.accept(response);
		this.server.enqueue(response);
	}


	protected final void expectRequest(Consumer<RecordedRequest> consumer) {
		try {
			consumer.accept(this.server.takeRequest());
		}
		catch (InterruptedException ex) {
			throw new IllegalStateException(ex);
		}
	}


}
