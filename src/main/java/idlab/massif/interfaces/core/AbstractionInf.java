package idlab.massif.interfaces.core;



import org.semanticweb.owlapi.model.OWLOntology;

public interface AbstractionInf extends PipeLineElement{

	public boolean setOntology(OWLOntology ontology);
	@Deprecated
	public boolean registerDLQuery(String newClass, String classExpression, AbstractionListenerInf listener);
	public int registerDLQuery(String newClass, String classExpression);
}
