// JRDFox(c) Copyright University of Oxford, 2013. All Rights Reserved.

package idlab.massif.selection.csparql_basic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.stream.Collectors;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;



public class DBPediaTest {

	public static void main(String[] args) throws Exception {
		
		String windowSize = args[0];
		String dataset = args[1];
		String url = args[2];
		String rules =args[3];
		
		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> "
				+ "Select * "
				//+ "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Work>.} "
				+ "WHERE  {  ?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Work>. }";
		//"/tmp/jasper2/rdfs-rules-rhodf.rules"
		if(args.length>4){
			query = "PREFIX : <http://streamreasoning.org/iminds/massif/> "
					+ "Select * "
					//+ "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Work>.} "
					+ "WHERE  {  ?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Work_TOP>. }";
		}
		if(args.length>5){
			query = args[5];
		}
		
		StreamingJena reasoner = new StreamingJena(dataset,windowSize,windowSize,rules);
		reasoner.addContinuousQuery(query);
		
		
	}
	
	
}
