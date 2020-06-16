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
import idlab.massif.interfaces.core.AbstractionInf;
import idlab.massif.interfaces.core.AbstractionListenerInf;
import idlab.massif.interfaces.core.CEPInf;
import idlab.massif.interfaces.core.CEPListener;
import idlab.massif.interfaces.core.SelectionInf;
import idlab.massif.interfaces.core.SelectionListenerInf;

import idlab.massif.selection.csparql_basic.CSparqlSelectionImpl;
import idlab.massif.sinks.PrintSink;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryParser {

	public static PipeLine parse(String query) throws OWLOntologyCreationException {
		JSONObject obj = new JSONObject(query);
		String sparqlQuery = "";
		String classExpression = "";
		String newHead = "";
		OWLOntology ontology = null;
		String querySub = "";
		Set<String> eventTypes = new HashSet<String>();
		Map<String, ?> config = obj.toMap();
		if (obj.has("ontology")) {
			String ontologyString = obj.getString("ontology");
			AbstractionInf abstractor = new HermitAbstractionImpl();
			// load ontology
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

			if (ontologyString.startsWith("http://")) {
				ontology = manager.loadOntologyFromOntologyDocument(IRI.create(ontologyString));
			} else {
				ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ontologyString));
			}

			abstractor.setOntology(ontology);
		}
		if (obj.has("classExpression")) {
			JSONArray expressions = obj.getJSONArray("classExpression");
			for (int i = 0; i < expressions.length(); i++) {
				newHead = expressions.getJSONObject(i).getString("head");
				classExpression = expressions.getJSONObject(i).getString("tail");

			}

		}
		if (obj.has("cep")) {
			JSONArray expressions = obj.getJSONArray("cep");
			for (int i = 0; i < expressions.length(); i++) {
				querySub = expressions.getJSONObject(i).getString("query");
				List<Object> types = expressions.getJSONObject(i).getJSONArray("types").toList();
				types.forEach(t -> eventTypes.add(t.toString()));

			}

		}
		if (obj.has("sparql")) {
			sparqlQuery = obj.getString("sparql");
			System.out.println(sparqlQuery);
		}
		if (obj.has("source")) {
			JSONArray sources = obj.getJSONArray("source");
			for (int i = 0; i < sources.length(); i++) {
				String sourceType = sources.getJSONObject(i).getString("type");
				if(sourceType.equals("POST")) {
					int sourcePort = sources.getJSONObject(i).getInt("port");
					System.out.println(sourceType + sourcePort);
				}else
					if(sourceType.equals("KAFKA")) {
						String kafkaServer = sources.getJSONObject(i).getString("kafkaServer");
						String kafkaTopic = sources.getJSONObject(i).getString("kafkaTopic");
					}
			}
		}
		if (obj.has("sink")) {
			JSONArray sources = obj.getJSONArray("sink");
			for (int i = 0; i < sources.length(); i++) {
				String sinkType = sources.getJSONObject(i).getString("type");

			}
		}
		// ---- define RSP
		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
		AbstractionInf abstractor = new HermitAbstractionImpl();
		CEPInf cepEngine = new EsperCEPImpl();
		
		// ---- create the pipeline
		PipeLine pipeline = new PipeLine(rsp,abstractor,cepEngine);
		PrintSink printSink = new PrintSink();
		pipeline.registerSink(printSink);

		rsp.registerContinuousQuery(sparqlQuery, 1, 1);
		rsp.addListener(pipeline);
		rsp.setStaticData(null);
		SelectionInf engine = rsp.builtEngine();
		// ---- define abstraction
		

		abstractor.setOntology(ontology);
		// register new DL query

		abstractor.registerDLQuery(newHead, classExpression, pipeline);
		// ---- define CEP


		cepEngine.registerQuery(querySub, eventTypes, pipeline);

		return pipeline;
	}


	
}
