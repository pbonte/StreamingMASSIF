package idlab.massif.interfaces.core;

public interface FilterInf extends PipeLineElement{
	/**
	 * Registers a query to the filter component.
	 * @param query: the query to be executed.
	 * @return query id: the id of the query
	 */
	public int registerContinuousQuery(String query);
	public boolean setStaticData(String dataSource);
	public boolean setRules(String rules);
}
