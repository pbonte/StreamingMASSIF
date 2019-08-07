package idlab.massif.sources;



import idlab.massif.core.PipeLine;
import idlab.massif.sources.util.WebSocketInputStream;
import spark.Spark;

public class WebSocketServerSource {
	private String wsURL;
	private PipeLine pipeline;
	private int port;

	public WebSocketServerSource(int port,String wsURL) {
		this.wsURL = wsURL;
		this.port = port;
	}
	public void registerPipeline(PipeLine pipeline) {
		this.pipeline = pipeline;
	}

	public void stream() {
		Spark.port(port);
		Spark.webSocket("/"+wsURL, new WebSocketInputStream(pipeline));
		Spark.init();
	}
	
}
