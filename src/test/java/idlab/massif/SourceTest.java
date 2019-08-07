package idlab.massif;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import idlab.massif.abstraction.hermit.HermitAbstractionImpl;
import idlab.massif.cep.esper.EsperCEPImpl;
import idlab.massif.core.PipeLine;
import idlab.massif.interfaces.core.AbstractionInf;
import idlab.massif.interfaces.core.CEPInf;
import idlab.massif.interfaces.core.SelectionInf;
import idlab.massif.selection.csparql_basic.CSparqlSelectionImpl;
import idlab.massif.sinks.PrintSink;
import idlab.massif.sources.WebSocketClientSource;
import idlab.massif.sources.WebSocketServerSource;

public class SourceTest {
	@Test
	public void websocketClientSourceTest() throws OWLOntologyCreationException {
		// ---- define RSP
		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
		AbstractionInf abstractor = new HermitAbstractionImpl();
		CEPInf cepEngine = new EsperCEPImpl();
		PipeLine pipeline = new PipeLine(rsp,abstractor,cepEngine);
		PrintSink printSink = new PrintSink();
		pipeline.registerSink(printSink);
		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> " 
				+ "CONSTRUCT{?work ?pred ?type.} "
		// + "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
		// <http://dbpedia.org/ontology/Work>.} "
				+ "WHERE  {  ?work ?pred ?type. }";
		rsp.registerContinuousQuery(query, 1, 1);
		rsp.addListener(pipeline);
		rsp.setStaticData(null);
		SelectionInf engine = rsp.builtEngine();
		// ---- define abstraction
		// load ontology
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ONT_STRING));
		abstractor.setOntology(ontology);
		// register new DL query
		String classExpression = "Observation and (observedProperty some LightIntensity)";

		String newHead = "http://massif.test/EventA";
		abstractor.registerDLQuery(newHead, classExpression, pipeline);
		// ---- define CEP
		// register query
		String querySub = "every(a=EventA or b=EventB)";
		//querySub="a=GraphEvent";
		Set<String> eventTypes = new HashSet<String>();
		eventTypes.add("EventA");
		eventTypes.add("EventB");
		
		cepEngine.registerQuery(querySub, eventTypes, pipeline);
		
		
		WebSocketClientSource fsource = new WebSocketClientSource("ws://localhost:4000/ws");
		fsource.registerPipeline(pipeline);
		fsource.stream();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
//	public void websocketServerSourceTest() throws OWLOntologyCreationException {
//		// ---- define RSP
//		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
//		AbstractionInf abstractor = new HermitAbstractionImpl();
//		CEPInf cepEngine = new EsperCEPImpl();
//		PipeLine pipeline = new PipeLine(rsp,abstractor,cepEngine);
//		PrintSink printSink = new PrintSink();
//		pipeline.registerSink(printSink);
//		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> " 
//				+ "CONSTRUCT{?work ?pred ?type.} "
//		// + "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
//		// <http://dbpedia.org/ontology/Work>.} "
//				+ "WHERE  {  ?work ?pred ?type. }";
//		rsp.registerContinuousQuery(query, 1, 1);
//		rsp.addListener(pipeline);
//		rsp.setStaticData(null);
//		SelectionInf engine = rsp.builtEngine();
//		// ---- define abstraction
//		// load ontology
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ONT_STRING));
//		abstractor.setOntology(ontology);
//		// register new DL query
//		String classExpression = "Observation and (observedProperty some LightIntensity)";
//
//		String newHead = "http://massif.test/EventA";
//		abstractor.registerDLQuery(newHead, classExpression, pipeline);
//		// ---- define CEP
//		// register query
//		String querySub = "every(a=EventA or b=EventB)";
//		//querySub="a=GraphEvent";
//		Set<String> eventTypes = new HashSet<String>();
//		eventTypes.add("EventA");
//		eventTypes.add("EventB");
//		
//		cepEngine.registerQuery(querySub, eventTypes, pipeline);
//		
//		
//		WebSocketServerSource fsource = new WebSocketServerSource(4000,"ws");
//		fsource.registerPipeline(pipeline);
//		fsource.stream();
//		try {
//			Thread.sleep(30000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

//	@Test
//	public void httpPutSourceTest() throws OWLOntologyCreationException {
//		// ---- define RSP
//		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
//		AbstractionInf abstractor = new HermitAbstractionImpl();
//		CEPInf cepEngine = new EsperCEPImpl();
//		PipeLine pipeline = new PipeLine(rsp,abstractor,cepEngine);
//		PrintSink printSink = new PrintSink();
//		pipeline.registerSink(printSink);
//		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> " 
//				+ "CONSTRUCT{?work ?pred ?type.} "
//		// + "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
//		// <http://dbpedia.org/ontology/Work>.} "
//				+ "WHERE  {  ?work ?pred ?type. }";
//		rsp.registerContinuousQuery(query, 1, 1);
//		rsp.addListener(pipeline);
//		rsp.setStaticData(null);
//		SelectionInf engine = rsp.builtEngine();
//		// ---- define abstraction
//		// load ontology
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ONT_STRING));
//		abstractor.setOntology(ontology);
//		// register new DL query
//		String classExpression = "Observation and (observedProperty some LightIntensity)";
//
//		String newHead = "http://massif.test/EventA";
//		abstractor.registerDLQuery(newHead, classExpression, pipeline);
//		// ---- define CEP
//		// register query
//		String querySub = "every(a=EventA or b=EventB)";
//		//querySub="a=GraphEvent";
//		Set<String> eventTypes = new HashSet<String>();
//		eventTypes.add("EventA");
//		eventTypes.add("EventB");
//		
//		cepEngine.registerQuery(querySub, eventTypes, pipeline);
//		
//		
//		HTTPPostSource fsource = new HTTPPostSource("test");
//		fsource.registerPipeline(pipeline);
//		fsource.stream();
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

//	@Test
//	public void httpGetSourceTest() throws OWLOntologyCreationException {
//		// ---- define RSP
//		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
//		AbstractionInf abstractor = new HermitAbstractionImpl();
//		CEPInf cepEngine = new EsperCEPImpl();
//		PipeLine pipeline = new PipeLine(rsp,abstractor,cepEngine);
//		PrintSink printSink = new PrintSink();
//		pipeline.registerSink(printSink);
//		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> " 
//				+ "CONSTRUCT{?work ?pred ?type.} "
//		// + "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
//		// <http://dbpedia.org/ontology/Work>.} "
//				+ "WHERE  {  ?work ?pred ?type. }";
//		rsp.registerContinuousQuery(query, 1, 1);
//		rsp.addListener(pipeline);
//		rsp.setStaticData(null);
//		SelectionInf engine = rsp.builtEngine();
//		// ---- define abstraction
//		// load ontology
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ONT_STRING));
//		abstractor.setOntology(ontology);
//		// register new DL query
//		String classExpression = "Observation and (observedProperty some LightIntensity)";
//
//		String newHead = "http://massif.test/EventA";
//		abstractor.registerDLQuery(newHead, classExpression, pipeline);
//		// ---- define CEP
//		// register query
//		String querySub = "every(a=EventA or b=EventB)";
//		//querySub="a=GraphEvent";
//		Set<String> eventTypes = new HashSet<String>();
//		eventTypes.add("EventA");
//		eventTypes.add("EventB");
//		
//		cepEngine.registerQuery(querySub, eventTypes, pipeline);
//		
//		
//		HTTPGetSource fsource = new HTTPGetSource("http://localhost/test",1000);
//		fsource.registerPipeline(pipeline);
//		fsource.stream();
//
//	}
//	@Test
//	public void fileSourceTest() throws OWLOntologyCreationException {
//		// ---- define RSP
//		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
//		AbstractionInf abstractor = new HermitAbstractionImpl();
//		CEPInf cepEngine = new EsperCEPImpl();
//		PipeLine pipeline = new PipeLine(rsp,abstractor,cepEngine);
//		PrintSink printSink = new PrintSink();
//		pipeline.registerSink(printSink);
//		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> " 
//				+ "CONSTRUCT{?work ?pred ?type.} "
//		// + "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
//		// <http://dbpedia.org/ontology/Work>.} "
//				+ "WHERE  {  ?work ?pred ?type. }";
//		rsp.registerContinuousQuery(query, 1, 1);
//		rsp.addListener(pipeline);
//		rsp.setStaticData(null);
//		SelectionInf engine = rsp.builtEngine();
//		// ---- define abstraction
//		// load ontology
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ONT_STRING));
//		abstractor.setOntology(ontology);
//		// register new DL query
//		String classExpression = "Observation and (observedProperty some LightIntensity)";
//
//		String newHead = "http://massif.test/EventA";
//		abstractor.registerDLQuery(newHead, classExpression, pipeline);
//		// ---- define CEP
//		// register query
//		String querySub = "every(a=EventA or b=EventB)";
//		//querySub="a=GraphEvent";
//		Set<String> eventTypes = new HashSet<String>();
//		eventTypes.add("EventA");
//		eventTypes.add("EventB");
//		
//		cepEngine.registerQuery(querySub, eventTypes, pipeline);
//		
//		
//		FileSource fsource = new FileSource("/tmp/file.xml",1000);
//		fsource.registerPipeline(pipeline);
//		fsource.stream();
//
//	}
	private static String ONT_STRING = "<?xml version=\"1.0\"?>\n"
			+ "<rdf:RDF xmlns=\"http://IBCNServices.github.io/homelabPlus.owl#\"\n"
			+ "     xml:base=\"http://IBCNServices.github.io/homelabPlus.owl\"\n"
			+ "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
			+ "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
			+ "     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n"
			+ "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
			+ "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
			+ "    <owl:Ontology rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl\"/>\n" + "    \n" + "\n"
			+ "\n" + "    <!-- \n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "    //\n" + "    // Object Properties\n" + "    //\n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "     -->\n" + "\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasLocation -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasLocation\">\n"
			+ "        <owl:inverseOf rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#isLocationOf\"/>\n"
			+ "    </owl:ObjectProperty>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasRole -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasRole\"/>\n"
			+ "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#isLocationOf -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#isLocationOf\"/>\n"
			+ "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#hasValue -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#hasValue\"/>\n"
			+ "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observationResult -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observationResult\"/>\n"
			+ "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedBy -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedBy\"/>\n"
			+ "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedProperty -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedProperty\"/>\n"
			+ "    \n" + "\n" + "\n" + "    <!-- http://IBCNServices.github.io/homelab.owl#belongsTo -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/homelab.owl#belongsTo\">\n"
			+ "        <owl:inverseOf rdf:resource=\"http://IBCNServices.github.io/homelab.owl#isLinkedTo\"/>\n"
			+ "    </owl:ObjectProperty>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelab.owl#isLinkedTo -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/homelab.owl#isLinkedTo\"/>\n"
			+ "    \n" + "\n" + "\n" + "    <!-- http://IBCNServices.github.io/homelab.owl#updates -->\n" + "\n"
			+ "    <owl:ObjectProperty rdf:about=\"http://IBCNServices.github.io/homelab.owl#updates\"/>\n" + "    \n"
			+ "\n" + "\n" + "    <!-- \n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "    //\n" + "    // Data properties\n" + "    //\n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "     -->\n" + "\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasDataValue -->\n" + "\n"
			+ "    <owl:DatatypeProperty rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasDataValue\"/>\n"
			+ "    \n" + "\n" + "\n" + "    <!-- \n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "    //\n" + "    // Classes\n" + "    //\n"
			+ "    ///////////////////////////////////////////////////////////////////////////////////////\n"
			+ "     -->\n" + "\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/SSNiot#LightIntensity -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#LightIntensity\"/>\n"
			+ "    \n" + "\n" + "\n" + "    <!-- http://IBCNServices.github.io/Accio-Ontology/SSNiot#Motion -->\n"
			+ "\n" + "    <owl:Class rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Motion\"/>\n"
			+ "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/SSNiot#PersonDetected -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#PersonDetected\">\n"
			+ "        <owl:equivalentClass rdf:resource=\"http://IBCNServices.github.io/homelabPlus.owl#StaffPresent\"/>\n"
			+ "        <rdfs:subClassOf rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "    </owl:Class>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/Accio-Ontology/SSNiot#Sound -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Sound\"/>\n" + "    \n"
			+ "\n" + "\n" + "    <!-- http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation -->\n"
			+ "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "    \n" + "\n" + "\n" + "    <!-- http://IBCNServices.github.io/homelab.owl#ActuatorUpdate -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelab.owl#ActuatorUpdate\"/>\n" + "    \n"
			+ "\n" + "\n" + "    <!-- http://IBCNServices.github.io/homelab.owl#Patient -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelab.owl#Patient\"/>\n" + "    \n" + "\n"
			+ "\n" + "    <!-- http://IBCNServices.github.io/homelab.owl#StaffMember -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelab.owl#StaffMember\"/>\n" + "    \n" + "\n"
			+ "\n" + "    <!-- http://IBCNServices.github.io/homelabPlus.owl#LightThresholdObservation -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl#LightThresholdObservation\">\n"
			+ "        <owl:equivalentClass>\n" + "            <owl:Class>\n"
			+ "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
			+ "                    <rdf:Description rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "                    <owl:Restriction>\n"
			+ "                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observationResult\"/>\n"
			+ "                        <owl:someValuesFrom>\n" + "                            <owl:Restriction>\n"
			+ "                                <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#hasValue\"/>\n"
			+ "                                <owl:someValuesFrom>\n"
			+ "                                    <owl:Restriction>\n"
			+ "                                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasDataValue\"/>\n"
			+ "                                        <owl:someValuesFrom>\n"
			+ "                                            <rdfs:Datatype>\n"
			+ "                                                <owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#double\"/>\n"
			+ "                                                <owl:withRestrictions rdf:parseType=\"Collection\">\n"
			+ "                                                    <rdf:Description>\n"
			+ "                                                        <xsd:minExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">30.0</xsd:minExclusive>\n"
			+ "                                                    </rdf:Description>\n"
			+ "                                                </owl:withRestrictions>\n"
			+ "                                            </rdfs:Datatype>\n"
			+ "                                        </owl:someValuesFrom>\n"
			+ "                                    </owl:Restriction>\n"
			+ "                                </owl:someValuesFrom>\n"
			+ "                            </owl:Restriction>\n" + "                        </owl:someValuesFrom>\n"
			+ "                    </owl:Restriction>\n" + "                    <owl:Restriction>\n"
			+ "                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedProperty\"/>\n"
			+ "                        <owl:someValuesFrom rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#LightIntensity\"/>\n"
			+ "                    </owl:Restriction>\n" + "                </owl:intersectionOf>\n"
			+ "            </owl:Class>\n" + "        </owl:equivalentClass>\n"
			+ "        <rdfs:subClassOf rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "    </owl:Class>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelabPlus.owl#MotionThresholdObservation -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl#MotionThresholdObservation\">\n"
			+ "        <owl:equivalentClass>\n" + "            <owl:Class>\n"
			+ "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
			+ "                    <rdf:Description rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "                    <owl:Restriction>\n"
			+ "                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observationResult\"/>\n"
			+ "                        <owl:someValuesFrom>\n" + "                            <owl:Restriction>\n"
			+ "                                <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#hasValue\"/>\n"
			+ "                                <owl:someValuesFrom>\n"
			+ "                                    <owl:Restriction>\n"
			+ "                                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasDataValue\"/>\n"
			+ "                                        <owl:someValuesFrom>\n"
			+ "                                            <rdfs:Datatype>\n"
			+ "                                                <owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#double\"/>\n"
			+ "                                                <owl:withRestrictions rdf:parseType=\"Collection\">\n"
			+ "                                                    <rdf:Description>\n"
			+ "                                                        <xsd:minExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">0.0</xsd:minExclusive>\n"
			+ "                                                    </rdf:Description>\n"
			+ "                                                </owl:withRestrictions>\n"
			+ "                                            </rdfs:Datatype>\n"
			+ "                                        </owl:someValuesFrom>\n"
			+ "                                    </owl:Restriction>\n"
			+ "                                </owl:someValuesFrom>\n"
			+ "                            </owl:Restriction>\n" + "                        </owl:someValuesFrom>\n"
			+ "                    </owl:Restriction>\n" + "                    <owl:Restriction>\n"
			+ "                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedProperty\"/>\n"
			+ "                        <owl:someValuesFrom rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Motion\"/>\n"
			+ "                    </owl:Restriction>\n" + "                </owl:intersectionOf>\n"
			+ "            </owl:Class>\n" + "        </owl:equivalentClass>\n"
			+ "        <rdfs:subClassOf rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "    </owl:Class>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelabPlus.owl#SoundThresholdObservation -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl#SoundThresholdObservation\">\n"
			+ "        <owl:equivalentClass>\n" + "            <owl:Class>\n"
			+ "                <owl:intersectionOf rdf:parseType=\"Collection\">\n"
			+ "                    <rdf:Description rdf:about=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "                    <owl:Restriction>\n"
			+ "                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observationResult\"/>\n"
			+ "                        <owl:someValuesFrom>\n" + "                            <owl:Restriction>\n"
			+ "                                <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#hasValue\"/>\n"
			+ "                                <owl:someValuesFrom>\n"
			+ "                                    <owl:Restriction>\n"
			+ "                                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#hasDataValue\"/>\n"
			+ "                                        <owl:someValuesFrom>\n"
			+ "                                            <rdfs:Datatype>\n"
			+ "                                                <owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#double\"/>\n"
			+ "                                                <owl:withRestrictions rdf:parseType=\"Collection\">\n"
			+ "                                                    <rdf:Description>\n"
			+ "                                                        <xsd:minExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">40.0</xsd:minExclusive>\n"
			+ "                                                    </rdf:Description>\n"
			+ "                                                </owl:withRestrictions>\n"
			+ "                                            </rdfs:Datatype>\n"
			+ "                                        </owl:someValuesFrom>\n"
			+ "                                    </owl:Restriction>\n"
			+ "                                </owl:someValuesFrom>\n"
			+ "                            </owl:Restriction>\n" + "                        </owl:someValuesFrom>\n"
			+ "                    </owl:Restriction>\n" + "                    <owl:Restriction>\n"
			+ "                        <owl:onProperty rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedProperty\"/>\n"
			+ "                        <owl:someValuesFrom rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Sound\"/>\n"
			+ "                    </owl:Restriction>\n" + "                </owl:intersectionOf>\n"
			+ "            </owl:Class>\n" + "        </owl:equivalentClass>\n"
			+ "        <rdfs:subClassOf rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "    </owl:Class>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelabPlus.owl#StaffPresent -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl#StaffPresent\">\n"
			+ "        <rdfs:subClassOf rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n"
			+ "    </owl:Class>\n" + "    \n" + "\n" + "\n"
			+ "    <!-- http://IBCNServices.github.io/homelabPlus.owl#Warning -->\n" + "\n"
			+ "    <owl:Class rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl#Warning\"/>\n" + "    \n" + "\n"
			+ "\n" + "    <!-- \n"
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
			+ "    <!-- http://IBCNServices.github.io/homelab.owl#soundIntensity -->\n" + "\n" +

			"    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelab.owl#soundIntensity\">\n"
			+ "        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Sound\"/>\n"
			+ "    </owl:NamedIndividual>\n" + "</rdf:RDF>\n" + "\n" + "\n" + "\n"
			+ "<!-- Generated by the OWL API (version 4.2.8.20170104-2310) https://github.com/owlcs/owlapi -->\n" + "\n"
			+ "";
}
