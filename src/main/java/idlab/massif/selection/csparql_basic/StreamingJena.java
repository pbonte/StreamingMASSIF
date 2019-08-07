/**
 * 
 */
package idlab.massif.selection.csparql_basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.time.CurrentTimeEvent;

import idlab.massif.interfaces.core.SelectionListenerInf;
import idlab.massif.selection.csparql_basic.utils.GraphEvent;


/**
 * @author pbonte
 *
 */
public class StreamingJena {

	private EPRuntime cepRT;
	private long counter = 0;
	private JenaReasoner reasoner;
	private boolean isDone=false;

	public StreamingJena(String ontologyFile, String windowSize, String windowSlide,String rules) {
		this.reasoner = new JenaReasoner(ontologyFile,rules);
		Configuration cep_config = new Configuration();

		cep_config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
		cep_config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		cep_config.getEngineDefaults().getLogging().setEnableTimerDebug(true);
		cep_config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
		cep_config.getEngineDefaults().getMetricsReporting().setEnableMetricsReporting(true);
		cep_config.getEngineDefaults().getLogging().setEnableQueryPlan(true);

		EPServiceProvider cep = EPServiceProviderManager.getDefaultProvider(cep_config);
		this.cepRT = cep.getEPRuntime();
		EPAdministrator cepAdm = cep.getEPAdministrator();
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
		EPDeploymentAdmin deployAdmin = cepAdm.getDeploymentAdmin();
		ClassLoader classLoader = StreamingJena.class.getClassLoader();
		cep.getEPAdministrator().getConfiguration().addEventType(GraphEvent.class);

		String eplStatement = String.format("select * from GraphEvent#time(%s sec) output snapshot every %s seconds",
				windowSize, windowSlide);

		try {
			EPStatement statement = cepAdm.createEPL(eplStatement);

			statement.addListener(new UpdateListener() {
				public void update(EventBean[] newEvents, EventBean[] oldEvents) {
					String fullMsg = "";
					if (newEvents != null) {
						try {
							long time1 = System.currentTimeMillis();
							StringBuilder builder = new StringBuilder();
							System.out.println("IN:\t" + System.currentTimeMillis());
							System.out.println("#events:\t" + newEvents.length);
							int counter = 0;
							List<Statement> add = new ArrayList<Statement>();
							for (EventBean e : newEvents) {
								counter++;
								add.addAll((List<Statement>) e.get("triples"));
								

							}
							reasoner.setupSimpleAdd(add);
							System.out.println("Added");
							System.out.println("Start query");
							reasoner.query();
							System.out.println("done query");
							// String result = builder.toString();
							// System.out.println("Result lenght:" +
							// result.length());
							// reasoner.setupAdd(result);
							counter = 0;
							builder = new StringBuilder();

							reasoner.setupSimpleDelete(add);

							System.out.println("Processing Time:\t" + (System.currentTimeMillis() - time1));
							System.out.println("Setup complete");
							System.out.println("OUT:\t" + System.currentTimeMillis());
							System.out.println("OUTNANO:\t" + System.nanoTime());
							if(add.size()==0&&isDone){
								System.out.println("Done at:\t"+System.nanoTime());
								System.exit(0);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// cepRT.sendEvent(new CurrentTimeEvent(System.currentTimeMillis()));

		long time1 = System.currentTimeMillis();

	}
	public StreamingJena(String ontologyFile, String windowSize, String windowSlide) {
		if(ontologyFile!=null) {
			this.reasoner = new JenaReasoner(ontologyFile);

		}else {
			this.reasoner = new JenaReasoner();
		}
		Configuration cep_config = new Configuration();

		cep_config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
		cep_config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		cep_config.getEngineDefaults().getLogging().setEnableTimerDebug(true);
		cep_config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
		cep_config.getEngineDefaults().getMetricsReporting().setEnableMetricsReporting(true);
		cep_config.getEngineDefaults().getLogging().setEnableQueryPlan(true);

		EPServiceProvider cep = EPServiceProviderManager.getDefaultProvider(cep_config);
		this.cepRT = cep.getEPRuntime();
		EPAdministrator cepAdm = cep.getEPAdministrator();
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
		EPDeploymentAdmin deployAdmin = cepAdm.getDeploymentAdmin();
		ClassLoader classLoader = StreamingJena.class.getClassLoader();
		cep.getEPAdministrator().getConfiguration().addEventType(GraphEvent.class);

		String eplStatement = String.format("select * from GraphEvent#time(%s sec) output snapshot every %s seconds",
				windowSize, windowSlide);

		try {
			EPStatement statement = cepAdm.createEPL(eplStatement);

			statement.addListener(new UpdateListener() {
				public void update(EventBean[] newEvents, EventBean[] oldEvents) {
					String fullMsg = "";
					System.out.println("triggering");
					if (newEvents != null) {
						try {
							long time1 = System.currentTimeMillis();
							StringBuilder builder = new StringBuilder();
							System.out.println("IN:\t" + System.currentTimeMillis());
							System.out.println("#events:\t" + newEvents.length);
							int counter = 0;
							List<Statement> add = new ArrayList<Statement>();
							for (EventBean e : newEvents) {
								counter++;
								add.addAll((List<Statement>) e.get("triples"));

							}
							reasoner.setupSimpleAdd(add);
							System.out.println("Added");
							System.out.println("Start query");
							reasoner.query();
							System.out.println("done query");
							// String result = builder.toString();
							// System.out.println("Result lenght:" +
							// result.length());
							// reasoner.setupAdd(result);
							counter = 0;
							builder = new StringBuilder();

							reasoner.setupSimpleDelete(add);

							System.out.println("Processing Time:\t" + (System.currentTimeMillis() - time1));
							System.out.println("Setup complete");
							System.out.println("OUT:\t" + System.currentTimeMillis());
							System.out.println("OUTNANO:\t" + System.nanoTime());
							if(add.size()==0&&isDone){
								System.out.println("Done at:\t"+System.nanoTime());
								System.exit(0);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// cepRT.sendEvent(new CurrentTimeEvent(System.currentTimeMillis()));

		long time1 = System.currentTimeMillis();

	}
	public void setDone(){
		this.isDone=true;
	}

	public void addEvent(List<Statement> event) {
		cepRT.sendEvent(new GraphEvent(counter++, event));
	}

	public void advanceTime(long time) {
		cepRT.sendEvent(new CurrentTimeEvent(time));
	}

	public void addContinuousQuery(String query) {
		reasoner.addContinuousQuery(query);
	}
	public void addContinuousQuery(String query,SelectionListenerInf listener) {
		reasoner.addContinuousQuery(query,listener);
	}
}
