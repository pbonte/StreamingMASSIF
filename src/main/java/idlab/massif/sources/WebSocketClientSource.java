package idlab.massif.sources;

import java.net.URI;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import idlab.massif.core.PipeLine;
import idlab.massif.sources.util.WebSocketInputStream;

public class WebSocketClientSource {

	private PipeLine pipeline;
	private String wsURL;

	public WebSocketClientSource(String wsURL) {
		this.wsURL = wsURL;
	}

	public void registerPipeline(PipeLine pipeline) {
		this.pipeline = pipeline;
	}

	public void stream() {
		WebSocketClient client = new WebSocketClient();

		WebSocketInputStream socket = new WebSocketInputStream(this.pipeline);
		try {
			client.start();

			URI echoUri = new URI(wsURL);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			System.out.printf("Connecting to : %s%n", echoUri);

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				//client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
