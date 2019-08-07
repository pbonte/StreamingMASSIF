package idlab.massif.listeners;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

import idlab.massif.interfaces.core.AbstractionListenerInf;

public class AbstractionListener implements AbstractionListenerInf{

	@Override
	public void notify(String event,String eventName) {
		// TODO Auto-generated method stub
		System.out.println(event);
	}

}
