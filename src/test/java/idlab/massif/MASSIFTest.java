package idlab.massif;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import idlab.massif.abstraction.hermit.HermitAbstractionImpl;
import idlab.massif.cep.esper.EsperCEPImpl;
import idlab.massif.interfaces.core.AbstractionInf;
import idlab.massif.interfaces.core.AbstractionListenerInf;
import idlab.massif.interfaces.core.CEPInf;
import idlab.massif.interfaces.core.CEPListener;
import idlab.massif.interfaces.core.SelectionInf;
import idlab.massif.interfaces.core.SelectionListenerInf;
import idlab.massif.selection.csparql_basic.CSparqlSelectionImpl;

public class MASSIFTest {

	@Test
	public void test() throws OWLOntologyCreationException {
		// ---- define RSP
		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
		AbstractionInf abstractor = new HermitAbstractionImpl();
		CEPInf cepEngine = new EsperCEPImpl();

		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> " 
				+ "CONSTRUCT{?work ?pred ?type.} "
		// + "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
		// <http://dbpedia.org/ontology/Work>.} "
				+ "WHERE  {  ?work ?pred ?type. }";
		SelectionListenerInf rsplistener = new RSPListener(abstractor);
		rsp.registerContinuousQuery(query, 1, 1);
		rsp.addListener(rsplistener);
		rsp.setStaticData(null);
		SelectionInf engine = rsp.builtEngine();
		// ---- define abstraction
		// load ontology
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ONT_STRING));
		abstractor.setOntology(ontology);
		AbstractionListenerInf absListener = new AbstractionListener(cepEngine);
		// register new DL query
		String classExpression = "Observation and (observedProperty some LightIntensity)";

		String newHead = "http://massif.test/EventA";
		abstractor.registerDLQuery(newHead, classExpression, absListener);
		// ---- define CEP
		// register query
		String querySub = "a=EventA or b=EventB";
		//querySub="a=GraphEvent";
		Set<String> eventTypes = new HashSet<String>();
		eventTypes.add("EventA");
		eventTypes.add("EventB");
		
		CEPListenerTest ceplistener = new CEPListenerTest();
		cepEngine.registerQuery(querySub, eventTypes, ceplistener);
		
		//add the event
		engine.addEvent(ONT_EVENT);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		engine.addEvent(ONT_EVENT);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		engine.addEvent(ONT_EVENT);

		System.out.println(ceplistener.getResults());
	}

	private class RSPListener implements SelectionListenerInf {

		private String result;
		private AbstractionInf abstractor;

		public RSPListener(AbstractionInf abstractor) {
			this.result = "";
			this.abstractor = abstractor;
		}

		@Override
		public void notify(String triples) {
			// TODO Auto-generated method stub
			this.result = triples;
			abstractor.addEvent(triples);
		}

		public String getResult() {
			return result;
		}

	}

	private class AbstractionListener implements AbstractionListenerInf {
		String receivedEvent;
		private CEPInf cep;

		public AbstractionListener(CEPInf cep) {
			this.cep = cep;
		}
		@Override
		public void notify(String event,String eventName) {
			receivedEvent = event;
			cep.addEvent(event, eventName);
		}

		public String getEVent() {
			return receivedEvent;
		}
	}

	private class CEPListenerTest implements CEPListener {
		private Map<String, Object> events;

		@Override
		public void notify(Map<String, Object> events) {
			this.events = events;

		}

		public Map<String, Object> getResults() {
			return events;
		}

	}
	private static String ONT_EVENT="<?xml version=\"1.0\"?>\n" + 
			"<rdf:RDF xmlns=\"http://IBCNServices.github.io/homelabPlus.owl#\"\n" + 
			"     xml:base=\"http://IBCNServices.github.io/homelabPlus.owl\"\n" + 
			"     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" + 
			"     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
			"     xmlns:ssn=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#\"\n" + 
			"     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" + 
			"     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" + 
			"     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n" + 
			"    <owl:Ontology rdf:about=\"http://IBCNServices.github.io/homelabPlus2.owl\">\n" + 
			"        <owl:imports rdf:resource=\"http://IBCNServices.github.io/homelabPlus.owl\"/>\n" + 
			"    </owl:Ontology>\n" + 
			"    \n" +  
			"\n" + 
			"\n" + 
			"    <!-- \n" + 
			"    ///////////////////////////////////////////////////////////////////////////////////////\n" + 
			"    //\n" + 
			"    // Individuals\n" + 
			"    //\n" + 
			"    ///////////////////////////////////////////////////////////////////////////////////////\n" + 
			"     -->\n" + 
			"\n" + 
			"    \n" + 
			"\n" + 
			"\n" + 
			"    <!-- http://IBCNServices.github.io/homelab.owl#lightIntensity -->\n" + 
			"\n" + 
			"    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelab.owl#lightIntensity\">\n" + 
			"        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#LightIntensity\"/>\n" + 
			"    </owl:NamedIndividual>\n" + 
			"    \n" + 
			"\n" + 
			"\n" + 
			"    <!-- http://IBCNServices.github.io/homelab.owl#motionIntensity -->\n" + 
			"\n" + 
			"    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelab.owl#motionIntensity\">\n" + 
			"        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Motion\"/>\n" + 
			"    </owl:NamedIndividual>\n" + 
			"    \n" + 
			"\n" + 
			"\n" + 
			"    <!-- http://IBCNServices.github.io/homelab.owl#soundIntensity -->\n" + 
			"\n" + 
			"    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelab.owl#soundIntensity\">\n" + 
			"        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/SSNiot#Sound\"/>\n" + 
			"    </owl:NamedIndividual>\n" + 
			"    \n" + 
			"\n" + 
			"\n" + 
			"    <!-- http://IBCNServices.github.io/homelabPlus.owl#obs -->\n" + 
			"\n" + 
			"    <owl:NamedIndividual rdf:about=\"http://IBCNServices.github.io/homelabPlus.owl#obs\">\n" + 
			"        <rdf:type rdf:resource=\"http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation\"/>\n" + 
			"        <ssn:observedProperty rdf:resource=\"http://IBCNServices.github.io/homelab.owl#lightIntensity\"/>\n" + 
			"    </owl:NamedIndividual>\n" + 
			"</rdf:RDF>";
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
