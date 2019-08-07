package idlab.massif.selection.csparql_basic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.vocabulary.RDF;

import idlab.massif.interfaces.core.SelectionInf;
import idlab.massif.interfaces.core.SelectionListenerInf;

public class CSparqlSelectionImpl implements SelectionInf {
	private SelectionListenerInf listner;
	private int window, windowSlide;
	private String dataSource;
	private String rules;
	private String query;
	private StreamingJena engine;

	public CSparqlSelectionImpl() {

	}

	@Override
	public boolean addListener(SelectionListenerInf listener) {
		this.listner = listener;
		return false;
	}

	@Override
	public boolean registerContinuousQuery(String query, int window, int windowSlide) {
		this.query = query;
		this.window = window;
		this.windowSlide = windowSlide;
		return false;
	}

	@Override
	public boolean setStaticData(String dataSource) {
		this.dataSource = dataSource;
		return false;
	}

	@Override
	public boolean setRules(String rules) {
		this.rules = rules;
		return false;
	}

	@Override
	public SelectionInf builtEngine() {
		if (dataSource != null && rules != null) {
			this.engine = new StreamingJena(dataSource, window + "", windowSlide + "", rules);
		} else {
			this.engine = new StreamingJena(null, window + "", windowSlide + "");

		}
		engine.addContinuousQuery(query, listner);
		return this;
	}

	@Override
	public boolean addEvent(Set<List<String>> triples) {
		engine.advanceTime(System.currentTimeMillis());
		List<Statement> statements = new ArrayList<Statement>();
		for (List<String> triple : triples) {
			statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(triple.get(0)),
					ResourceFactory.createProperty(triple.get(1)), ResourceFactory.createResource(triple.get(2))));
		}
		engine.addEvent(statements);
		return false;
	}

	@Override
	public boolean addEvent(String triples) {
		engine.advanceTime(System.currentTimeMillis());
		Model dataModel = ModelFactory.createDefaultModel();
		try {
			InputStream targetStream = new ByteArrayInputStream(triples.getBytes());
			dataModel.read(targetStream,"TTL");
			StmtIterator it = dataModel.listStatements();
			List<Statement> statements = new ArrayList<Statement>();
			while (it.hasNext()) {
				statements.add(it.next());
			}
			engine.addEvent(statements);
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
