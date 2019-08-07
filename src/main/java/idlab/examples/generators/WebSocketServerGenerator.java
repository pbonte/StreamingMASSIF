package idlab.examples.generators;

import idlab.massif.sources.util.WebSocketInputStream;
import spark.Spark;

public class WebSocketServerGenerator {

	public static void main(String[] args) {
		Spark.port(4000);
		Spark.webSocket("/ws", new SimpleEventSocket());
		Spark.init();
	}
}
