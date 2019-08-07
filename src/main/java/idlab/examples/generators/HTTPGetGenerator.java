package idlab.examples.generators;

import spark.Spark;

public class HTTPGetGenerator {
	public static void main(String[] args) {
		new HTTPGetGenerator();
	}

	public HTTPGetGenerator() {
        Spark.get("/test", (req, res) -> ONT_EVENT);

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
}
