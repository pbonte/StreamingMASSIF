package idlab.massif.sources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import idlab.massif.core.PipeLine;

/***
 * Reads a provided file line by line and streams each line to the pipeline.
 * @author psbonte
 *
 */
public class FileSource {
	
	private String fileName;
	private PipeLine pipeline;
	private long timeout;

	public FileSource(String fileName, long timeout) {
		this.fileName = fileName;
		this.timeout = timeout;
	}
	public void registerPipeline(PipeLine pipeline) {
		this.pipeline = pipeline;
	}
	public void stream() {

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String line;
			while ((line = br.readLine()) != null) {
				pipeline.addEvent(line);
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
