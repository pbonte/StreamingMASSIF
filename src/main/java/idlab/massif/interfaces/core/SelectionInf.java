package idlab.massif.interfaces.core;

import java.util.List;
import java.util.Set;

public interface SelectionInf {
	
	public boolean addListener(SelectionListenerInf listener);
	public boolean registerContinuousQuery(String query,int window, int windowSlide);
	public boolean setStaticData(String dataSource);
	public boolean setRules(String rules);
	public SelectionInf builtEngine();
	public boolean addEvent(Set<List<String>> triples);
	public boolean addEvent(String triples);


}
