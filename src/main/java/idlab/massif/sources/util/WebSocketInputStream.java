package idlab.massif.sources.util;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import idlab.massif.core.PipeLine;

@WebSocket

public class WebSocketInputStream {
	private PipeLine pipeline;

	public WebSocketInputStream(PipeLine pipeline) {
		this.pipeline=pipeline;
	}
    @OnWebSocketConnect
    public void connected(Session session) {
    	System.out.println("connecting");
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        pipeline.addEvent(message);
    }

}
