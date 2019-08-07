package idlab.massif.interfaces.core;

import java.util.Map;
import java.util.Set;


public interface CEPInf extends PipeLineElement{
	
	public boolean registerQuery(String CEPquery, Set<String> eventTypes, CEPListener listener);
	public void addEvent(String event, String eventName);
	
	public int registerQuery(String complexName,String CEPquery, Set<String> eventTypes,Map<String,String> indComplexPropMapping);

	
}
