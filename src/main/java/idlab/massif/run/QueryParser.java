package idlab.massif.run;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import idlab.massif.abstraction.hermit.HermitAbstractionImpl;
import idlab.massif.cep.esper.EsperCEPImpl;
import idlab.massif.core.PipeLine;
import idlab.massif.core.PipeLineComponent;
import idlab.massif.core.PipeLineGraph;
import idlab.massif.filter.jena.JenaFilter;
import idlab.massif.interfaces.core.AbstractionInf;
import idlab.massif.interfaces.core.AbstractionListenerInf;
import idlab.massif.interfaces.core.CEPInf;
import idlab.massif.interfaces.core.CEPListener;
import idlab.massif.interfaces.core.FilterInf;
import idlab.massif.interfaces.core.SelectionInf;
import idlab.massif.interfaces.core.SelectionListenerInf;
import idlab.massif.interfaces.core.SinkInf;
import idlab.massif.interfaces.core.SourceInf;
import idlab.massif.interfaces.core.WindowInf;
import idlab.massif.selection.csparql_basic.CSparqlSelectionImpl;
import idlab.massif.sinks.PrintSink;
import idlab.massif.sources.HTTPPostSource;
import idlab.massif.sources.KafkaSource;
import idlab.massif.window.esper.EsperWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryParser {
	
	public static PipeLineComponent parseComponent(JSONObject comp) {
		String compType = comp.getString("type").toLowerCase();
		PipeLineComponent pipeComp = null;
		switch(compType) {
		  case "sink":
			  String impl = comp.getString("impl");
			  if(impl.equals("PrintSink")) {
				  SinkInf printSink = new PrintSink();
				  pipeComp = new PipeLineComponent(printSink,Collections.EMPTY_LIST);
			  }
		    // code block
		    break;
		  case "filter":
			  impl = "jena";
			  if(comp.has("impl")) {
				  impl = comp.getString("impl");
			  }
			  if(impl.equals("jena")){
				  FilterInf filter = new JenaFilter();
				  JSONArray queries = comp.getJSONArray("queries");
				  for (int i = 0; i < queries.length(); i++) {
					  int filterQueryID=filter.registerContinuousQuery(queries.getString(i));
				  }
				  filter.start();
				  pipeComp = new PipeLineComponent(filter,Collections.EMPTY_LIST);
			  }
		    // code block
		    break;
		  case "window":
			  int size = comp.getInt("size");
			  int slide = size;
			  if(comp.has("slide")) {
				  slide=comp.getInt("slide");
			  }
			  WindowInf window = new EsperWindow();
			  window.setWindowSize(size,slide);
			  window.start();
			  pipeComp = new PipeLineComponent(window,Collections.EMPTY_LIST);
			  break;
		  case "abstract":
		    // code block
			  AbstractionInf abstractor = new HermitAbstractionImpl();
			  if(comp.has("ontologyIRI")) {
				  String ontologyIRI = comp.getString("ontologyIRI");
				  OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
				  OWLOntology ontology;
				try {
					ontology = manager.loadOntology(IRI.create(ontologyIRI));
					abstractor.setOntology(ontology);
				} catch (OWLOntologyCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
			  }
			  JSONArray queries = comp.getJSONArray("expressions");
			  for (int i = 0; i < queries.length(); i++) {
				  JSONObject exp = queries.getJSONObject(i);
				  String head = exp.getString("head");
				  String tail = exp.getString("tail");
				  abstractor.registerDLQuery(head, tail);
			  }
			  pipeComp = new PipeLineComponent(abstractor,Collections.EMPTY_LIST);
			  break;
		  case "source":
			  impl = comp.getString("impl").toLowerCase();
			  if(impl.equals("kafkasource")) {
				  KafkaSource kafkaSource = new KafkaSource(comp.getString("kafkaServer"),comp.getString("kafkaTopic"));
				  pipeComp = new PipeLineComponent(kafkaSource,Collections.EMPTY_LIST);
			  }
			  if(impl.equals("httppostsource")) {
				  HTTPPostSource postSource = new HTTPPostSource(comp.getString("path"),comp.getInt("port"));
				  pipeComp = new PipeLineComponent(postSource,Collections.EMPTY_LIST);
			  }
				  
		}
		return pipeComp;
	}

	public static PipeLineGraph parse(String query) throws OWLOntologyCreationException {
		Map<String,PipeLineComponent> pipelineComponents = new HashMap<String,PipeLineComponent>();
		JSONObject obj = new JSONObject(query);
		if(!obj.has("components")) {
			return null;
		}else {
			JSONObject components = obj.getJSONObject("components");

			for(String key : components.keySet()) {
				JSONObject comp = components.getJSONObject(key);
				
				pipelineComponents.put(key,parseComponent(comp));
			}
		}
		//configure the graph
		JSONObject configs = obj.getJSONObject("configuration");
		for(String key : configs.keySet()) {
			JSONArray linked = configs.getJSONArray(key);
			ArrayList<PipeLineComponent> linkedComps = new ArrayList<PipeLineComponent>();
			for (int i = 0; i < linked.length(); i++) {
				linkedComps.add(pipelineComponents.get(linked.get(i).toString()));
			}
			pipelineComponents.get(key).setOutput(linkedComps);
		}
		//start the sources
		
		for(PipeLineComponent comp : pipelineComponents.values()) {
			if(comp.getElement() instanceof SourceInf) {
				comp.getElement().start();
			}
		}

		return new PipeLineGraph(pipelineComponents);
	}


	
}
