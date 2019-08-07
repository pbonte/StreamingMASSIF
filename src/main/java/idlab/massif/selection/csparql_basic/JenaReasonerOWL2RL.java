package idlab.massif.selection.csparql_basic;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;



/**
 * @author pbonte
 *
 */
public class JenaReasonerOWL2RL {
	private InfModel infModel;
	private List<Query> queries;

	
	public JenaReasonerOWL2RL(String staticOntFile,String ontRules){
		GenericRuleReasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL(ontRules));
		reasoner.setMode(GenericRuleReasoner.FORWARD_RETE);
		reasoner.setDerivationLogging(true);
		Model dataModel = ModelFactory.createDefaultModel();
		try {
			dataModel.read(new FileInputStream(new File(staticOntFile)), "RDF/XML");
			System.out.println(dataModel.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.infModel = ModelFactory.createInfModel(reasoner, dataModel);
		System.out.println(infModel.size());
		infModel.prepare();
		System.out.println(infModel.size());

	}
	public void addContinuousQuery(String queryString){
		if(queries == null){
			queries = new ArrayList<Query>();
		}
		Query query = QueryFactory.create(queryString);
		queries.add(query);
	}
	


	public void setupSimpleAdd(List<Statement> add) {

		infModel.add(add);
	}
	public void setupSimpleDelete(List<Statement> delete) {
		infModel.remove(delete);

	}
	
	
		
	public List<ResultSet> query() {
		long counter = 0;
		List<ResultSet> results = new ArrayList<ResultSet>();
		for (Query query : queries) {
			try (QueryExecution qexec = QueryExecutionFactory.create(query, infModel)) {
				ResultSet result = qexec.execSelect();
				while(result.hasNext()){
					counter++;
					QuerySolution sol = result.nextSolution();
					//System.out.println(sol);
				}
			}
		}
		System.out.println("#Results:\t"+ counter);
		return results;
	}

	
}
