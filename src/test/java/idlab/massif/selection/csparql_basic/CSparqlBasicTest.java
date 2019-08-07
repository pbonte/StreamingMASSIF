package idlab.massif.selection.csparql_basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import idlab.massif.interfaces.core.SelectionInf;
import idlab.massif.interfaces.core.SelectionListenerInf;
import junit.framework.Assert;

public class CSparqlBasicTest {

	@Test
	public void test() {
		int windowSize = 1;
		String dataset = null;
		
		CSparqlSelectionImpl rsp = new CSparqlSelectionImpl();
		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> "
				+ "CONSTRUCT{?work ?pred ?type.} "
				//+ "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Work>.} "
				+ "WHERE  {  ?work ?pred ?type. }";
		MyListener listener = new MyListener();
		rsp.registerContinuousQuery(query, windowSize, windowSize);
		rsp.addListener(listener);
		rsp.setStaticData(dataset);
		SelectionInf engine = rsp.builtEngine();
		//Add the data
		String[] statements = new String[] {"http://massif.test/pbonte","http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://massif.test/esearcher"};
		String[] statements2 = new String[] {"http://massif.test/rictom","http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://massif.test/researcher"};
		Set<List<String>> event1 = new HashSet<List<String>>();
		event1.add(Arrays.asList(statements));
		Set<List<String>> event2 = new HashSet<List<String>>();
		event2.add(Arrays.asList(statements2));
		rsp.addEvent(event1);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rsp.addEvent(event2);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rsp.addEvent(event2);
		//System.out.println(listener.getResult());

		Assert.assertTrue(listener.getResult().length()>0);

	}
	private class MyListener implements SelectionListenerInf{

		private String result;
		public MyListener() {
			this.result="";
		}
		@Override
		public void notify(String triples) {
			// TODO Auto-generated method stub
			this.result=triples;
		}
		public String getResult() {
			return result;
		}
		
	}
//	@Test
//	public void test() {
//		String windowSize = "1";
//		String dataset = null;
//		
//		
//		String query = "PREFIX : <http://streamreasoning.org/iminds/massif/> "
//				+ "Select * "
//				//+ "{?work <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Work>.} "
//				+ "WHERE  {  ?work ?pred ?type. }";
//		
//		
//		StreamingJena reasoner = new StreamingJena(dataset,windowSize,windowSize);
//		reasoner.addContinuousQuery(query);
//		//Add the data
//		List<Statement> statements = new ArrayList<Statement>();
//		
//		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource("pbonte"), RDF.type, ResourceFactory.createResource("researcher")));
//		List<Statement> statements2 = new ArrayList<Statement>();
//		statements2.add(ResourceFactory.createStatement(ResourceFactory.createResource("rictom"), RDF.type, ResourceFactory.createResource("researcher")));
//		reasoner.advanceTime(0);
//		reasoner.addEvent(statements);
//		reasoner.advanceTime(1001);
//		reasoner.addEvent(statements2);
//		reasoner.advanceTime(2001);
//		reasoner.addEvent(statements);
//		reasoner.advanceTime(3001);
//		//
//
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
