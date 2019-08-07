package idlab.massif.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import idlab.massif.core.PipeLine;
/***
 * Allows to continuously pull a HTTP source through a HTTP get request.
 * @author psbonte
 *
 */
public class HTTPGetSource {

	private String url;
	private PipeLine pipeline;
	private long pullTimeOut;

	public HTTPGetSource(String url,long pullTimeOut) {
		this.url = url;
		this.pullTimeOut = pullTimeOut;
	}
	public void registerPipeline(PipeLine pipeline) {
		this.pipeline = pipeline;
	}
	public void stream() {
		while(true) {
			URL urlCon;
			try {
				urlCon = new URL(url);
				URLConnection conn = urlCon.openConnection();
				InputStream is = conn.getInputStream();
				String result ="";
				try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()))) {
					result =br.lines().collect(Collectors.joining(System.lineSeparator()));
					pipeline.addEvent(result);
				}
				Thread.sleep(pullTimeOut);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
