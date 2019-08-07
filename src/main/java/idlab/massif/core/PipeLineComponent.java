package idlab.massif.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import idlab.massif.interfaces.core.ListenerInf;
import idlab.massif.interfaces.core.PipeLineElement;

public class PipeLineComponent implements ListenerInf{
	
	
	private List<PipeLineComponent> output;
	private PipeLineElement element;
	private ExecutorService queue = Executors.newFixedThreadPool(1);

	public PipeLineComponent(PipeLineElement element,List<PipeLineComponent> output) {
		this.output = output;
		this.element = element;
		element.addListener(this);
		
	}
	public void addEvent(String event) {
		//add all arriving events to the queue
		queue.execute(new Runnable() {

			@Override
			public void run() {
				element.addEvent(event);

			}

		});
	}
	@Override
	public void notify(int queryID,String event) {
		//send all the response from this component to all its output components
		for(PipeLineComponent comp: output) {
			comp.addEvent(event);
		}
	}

}
