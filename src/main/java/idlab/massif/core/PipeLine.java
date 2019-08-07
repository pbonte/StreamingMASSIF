package idlab.massif.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idlab.massif.interfaces.core.AbstractionInf;
import idlab.massif.interfaces.core.AbstractionListenerInf;
import idlab.massif.interfaces.core.CEPInf;
import idlab.massif.interfaces.core.CEPListener;
import idlab.massif.interfaces.core.SelectionInf;
import idlab.massif.interfaces.core.SelectionListenerInf;
import idlab.massif.interfaces.core.SinkInf;


public class PipeLine implements SelectionListenerInf, AbstractionListenerInf, CEPListener{
	
	private SelectionInf selection;
	private AbstractionInf abstraction;
	private CEPInf cep;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ExecutorService abstractionQueue = Executors.newFixedThreadPool(1);
	private ExecutorService cepQueue = Executors.newFixedThreadPool(1);
	private Set<SinkInf> sinks;

	public PipeLine(SelectionInf selection, AbstractionInf abstraction, CEPInf cep) {
		this.selection = selection;
		this.abstraction = abstraction;
		this.cep = cep;
		this.sinks = new HashSet<SinkInf>();
	}
	public void addEvent(String event) {
		selection.addEvent(event);
	}
	public void registerSink(SinkInf sink) {
		this.sinks.add(sink);
	}
	public void unregisterSink(SinkInf sink) {
		if(this.sinks.contains(sink)) {
			this.sinks.remove(sink);
			logger.debug("Removing sink");
		}else {
			logger.error("Trying to unregister unknown sink");
		}
		
	}
	//CEP listener
	@Override
	public void notify(Map<String, Object> events) {
		for(SinkInf sink: sinks) {
			sink.addEvent(events.toString());
		}
	}
	//abstraction listener
	@Override
	public void notify(String event, String eventName) {
		logger.debug("Abstraction Listener received " + eventName);

		cepQueue.execute(new Runnable() {

			@Override
			public void run() {
				cep.addEvent(event, eventName);
			}

		});
	}
	//Selection listener
	@Override
	public void notify(String triples) {
		logger.debug("Selection Listener received " + triples);

		abstractionQueue.execute(new Runnable() {

			@Override
			public void run() {
				abstraction.addEvent(triples);
			}

		});
	}

}
