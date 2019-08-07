package idlab.examples.generators;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;


public class WebSocketClientGenerator {
	public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient();

		SimpleEventSocket socket = new SimpleEventSocket();
        try
        {
            client.start();

            URI echoUri = new URI("ws://localhost:4000/ws");
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket,echoUri,request);
            System.out.printf("Connecting to : %s%n",echoUri);
            
            // wait for closed socket connection.
            socket.awaitClose(5,TimeUnit.SECONDS);
            
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            try
            {
                client.stop();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
	}
            
}
