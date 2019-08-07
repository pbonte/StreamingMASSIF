package idlab.examples.generators;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleEventSocket {
	private final CountDownLatch closeLatch;
	@SuppressWarnings("unused")
	private Session session;

	public SimpleEventSocket() {
		this.closeLatch = new CountDownLatch(1);
	}

	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {

	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		try {
			System.out.println("trying to send:");
			for (int i = 0; i < 10; i++) {
				session.getRemote().sendString(ONT_EVENT);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		System.out.println("received");

	}

	@OnWebSocketError
	public void onError(Throwable cause) {
		System.out.print("WebSocket Error: ");
		cause.printStackTrace(System.out);
	}

	private static String ONT_EVENT = "<?xml version=\"1.0\"?>\n"
			+ "<rdf:RDF xmlns=\"http://IBCNServices.github.io/homelabPlus.owl#\"\n"
			+ "     xml:base=\"http://IBCNServices.github.io/homelabPlus.owl\"\n"
			+ "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
			+ "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
			+ "     xmlns:ssn=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#\"\n"
			+ "     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n"
			+ "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
			+ "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
			+ "    <owl:Ontology rdf:about=\"http://IBCNServices.github.io/homelabPlus2.owl\">\n"
			+ "        <owl:imports rdf:resource=\"http://IBCNServices.github.io/homelabPlus.owl\"/>\n"
			+ "    </owl:Ontology>\n" + "    \n" + "\n" + "\n" + "    <!-- \n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "    //\n" + "    // Individuals\n" + "    //\n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "     -->\n" + "\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelab.owl#lightIntensity -->\n" + "\n"
			+ "    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelab.owl#lightIntensity\">\n"
			+ "        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#LightIntensity\"/>\n"
			+ "    </owl:NamedIndividual>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelab.owl#motionIntensity -->\n" + "\n"
			+ "    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelab.owl#motionIntensity\">\n"
			+ "        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Motion\"/>\n"
			+ "    </owl:NamedIndividual>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelab.owl#soundIntensity -->\n" + "\n"
			+ "    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelab.owl#soundIntensity\">\n"
			+ "        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Sound\"/>\n"
			+ "    </owl:NamedIndividual>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelabPlus.owl#obs -->\n" + "\n"
			+ "    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl#obs\">\n"
			+ "        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "        <ssn:observedProperty rdf:resource=\"http://IBCNServices.github.io/homelab.owl#lightIntensity\"/>\n"
			+ "    </owl:NamedIndividual>\n" + "</rdf:RDF>";
}