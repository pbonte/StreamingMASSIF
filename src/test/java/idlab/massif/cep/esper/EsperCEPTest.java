package idlab.massif.cep.esper;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

import idlab.massif.interfaces.core.CEPListener;
import junit.framework.Assert;




public class EsperCEPTest {

	@Test
	public void filtertest() throws OWLOntologyCreationException {
		EsperCEPImpl cepEngine = new EsperCEPImpl();
		//register query
		String querySub= "a=EventA -> b=EventB";
		Set<String> eventTypes=new HashSet<String>();
		eventTypes.add("EventA");
		eventTypes.add("EventB");
		CEPListenerTest listener = new CEPListenerTest();
		cepEngine.registerQuery(querySub, eventTypes, listener);
		//CEPListenerTest listener2 = new CEPListenerTest();
		//cepEngine.registerQuery("a=EventA or b=EventB", eventTypes, listener2);
		
		//add event
		cepEngine.addEvent(ONT_EVENT, "EventA");
		cepEngine.addEvent(ONT_EVENT, "EventB");
		System.out.println(listener.getResults());
		Assert.assertEquals( eventTypes.size(),listener.getResults().size());
		
	}
	@Test
	public void test() throws OWLOntologyCreationException {
		EsperCEPImpl cepEngine = new EsperCEPImpl();
		//register query
		String querySub= "a=EventA -> b=EventB";
		Set<String> eventTypes=new HashSet<String>();
		eventTypes.add("EventA");
		eventTypes.add("EventB");
		CEPListenerTest listener = new CEPListenerTest();
		cepEngine.registerQuery(querySub, eventTypes, listener);
		//load event

		//add event
		cepEngine.addEvent(ONT_EVENT, "EventA");
		cepEngine.addEvent(ONT_EVENT, "EventB");
		System.out.println(listener.getResults());
		Assert.assertEquals( eventTypes.size(),listener.getResults().size());
		
	}
	
	// TODO: class provided by template
		private class CEPListenerTest implements CEPListener {
			private Map<String, Object> events;
		@Override
		public void notify(Map<String, Object> events) {
			this.events = events;
			
		}
		public Map<String, Object> getResults(){
			return events;
		}
		    
		}
		private static String ONT_EVENT="@prefix : <http://IBCNServices.github.io/homelabPlus.owl#> .\n" + 
				"@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" + 
				"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
				"@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" + 
				"@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" + 
				"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
				"@base <http://IBCNServices.github.io/homelabPlus2.owl> .\n" + 
				"\n" + 
				"<http://IBCNServices.github.io/homelabPlus2.owl> rdf:type owl:Ontology ;\n" + 
				"                                                  owl:imports <http://IBCNServices.github.io/homelabPlus.owl> .\n" + 
				"\n" + 
				"\n" + 
				"#################################################################\n" + 
				"#    Individuals\n" + 
				"#################################################################\n" + 
				"\n" + 
				"###  http://IBCNServices.github.io/homelab.owl#lightIntensity\n" + 
				"<http://IBCNServices.github.io/homelab.owl#lightIntensity> rdf:type owl:NamedIndividual ,\n" + 
				"                                                                    <http://IBCNServices.github.io/Accio-Ontology/SSNiot#LightIntensity> .\n" + 
				"\n" + 
				"\n" + 
				"###  http://IBCNServices.github.io/homelab.owl#motionIntensity\n" + 
				"<http://IBCNServices.github.io/homelab.owl#motionIntensity> rdf:type owl:NamedIndividual ,\n" + 
				"                                                                     <http://IBCNServices.github.io/Accio-Ontology/SSNiot#Motion> .\n" + 
				"\n" + 
				"\n" + 
				"###  http://IBCNServices.github.io/homelab.owl#soundIntensity\n" + 
				"<http://IBCNServices.github.io/homelab.owl#soundIntensity> rdf:type owl:NamedIndividual ,\n" + 
				"                                                                    <http://IBCNServices.github.io/Accio-Ontology/SSNiot#Sound> .\n" + 
				"\n" + 
				"\n" + 
				"###  http://IBCNServices.github.io/homelabPlus.owl#obs\n" + 
				":obs rdf:type owl:NamedIndividual ,\n" + 
				"              <http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#Observation> ;\n" + 
				"     <http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#observedProperty> <http://IBCNServices.github.io/homelab.owl#lightIntensity> .\n" + 
				"\n" + 
				"\n" + 
				"###  Generated by the OWL API (version 4.5.9.2019-02-01T07:24:44Z) https://github.com/owlcs/owlapi\n" + 
				"";
}
