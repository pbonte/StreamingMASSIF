/**
 * 
 */
package idlab.massif.selection.csparql_basic.utils;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

/**
 * @author pbonte
 *
 */
public class GraphEvent {

	private long id;
	private List<Statement> triples;

	public GraphEvent(long id, List<Statement> triples){
		this.id = id;
		this.triples=triples;
	}
	public List<Statement> getTriples(){
		return triples;
	}
	public long getId(){
		return id;
	}
}
