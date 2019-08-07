package idlab.massif.sources;



import idlab.massif.core.PipeLine;
import spark.Spark;

/***
 * Provides a Source that can be externally call through the provided url through a HTTP Post request.
 * 
 * @author psbonte
 *
 */
public class HTTPPostSource {

	private String path;
	private PipeLine pipeline;

	public HTTPPostSource(String path) {
		this.path = path;
	}

	public void registerPipeline(PipeLine pipeline) {
		this.pipeline = pipeline;
	}

	public void stream() {
		Spark.port(8080);
		Spark.post("/" + path, (req, res) -> {	
			this.pipeline.addEvent(req.body());
			return "ok";
		});

	}
}
