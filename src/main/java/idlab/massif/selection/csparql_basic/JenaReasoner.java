package idlab.massif.selection.csparql_basic;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;

import idlab.massif.interfaces.core.SelectionListenerInf;



/**
 * @author pbonte
 *
 */
public class JenaReasoner {
	private Model infModel;
	private List<Query> queries;
	private Map<Query,SelectionListenerInf> listenerMap;

	public JenaReasoner() {
		this.infModel = ModelFactory.createDefaultModel();
		this.listenerMap= new HashMap<Query,SelectionListenerInf>();

	}
	public JenaReasoner(String staticOntFile){
		Model dataModel = ModelFactory.createDefaultModel();
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();

			dataModel.read(staticOntFile, "RDF/XML");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.infModel = dataModel;
		this.listenerMap= new HashMap<Query,SelectionListenerInf>();

	}
	public JenaReasoner(String staticOntFile,String rules){
		Model dataModel = ModelFactory.createDefaultModel();
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();

			dataModel.read(staticOntFile, "RDF/XML");
			Reasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL(rules));
			InfModel model= ModelFactory.createInfModel(reasoner, dataModel);
			model.prepare();
			this.infModel=model;
			this.listenerMap= new HashMap<Query,SelectionListenerInf>();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	public Query addContinuousQuery(String queryString){
		if(queries == null){
			queries = new ArrayList<Query>();
		}
		Query query = QueryFactory.create(queryString);
		queries.add(query);
		return query;
	}
	public void addContinuousQuery(String queryString, SelectionListenerInf listener){
		Query query = addContinuousQuery(queryString);
		listenerMap.put(query,listener);
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
			if(query.isSelectType()){
			try (QueryExecution qexec = QueryExecutionFactory.create(query, infModel)) {
				ResultSet result = qexec.execSelect();
				while(result.hasNext()){
					counter++;
					QuerySolution sol = result.nextSolution();
					System.out.println(sol);
				}
			}
		}else{
			try (QueryExecution qexec = QueryExecutionFactory.create(query, infModel)) {
				Model result = qexec.execConstruct();
				String syntax = "TURTLE"; // also try "N-TRIPLE" and "TURTLE"
				StringWriter out = new StringWriter();
				result.write(out, syntax);
				String resultString = out.toString();
				if(listenerMap.containsKey(query)) {
					listenerMap.get(query).notify(resultString);
				}
			}
		}
		}
		System.out.println("#Results:\t"+ counter);
		return results;
	}

	
}
