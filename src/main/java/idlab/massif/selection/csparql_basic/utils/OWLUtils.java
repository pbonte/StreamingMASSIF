package idlab.massif.selection.csparql_basic.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLEntityRemover;

public class OWLUtils {

	/**
	 * Finds the overlapping axioms in an axiom set and an ontology
	 * 
	 * @param ontology
	 * @param newEvent
	 * @return
	 */
	public static Set<OWLAxiom> getOverlappingAxioms(OWLOntology ontology, Set<OWLAxiom> newEvent) {
		// Find updated object and data properties.
		Set<OWLAxiom> overlappingAxioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : newEvent) {
			if (ontology.containsAxiom(ax)) {
				overlappingAxioms.add(ax);
			}
		}
		return overlappingAxioms;
	}

	/**
	 * Finds the axioms in an axiom set that are not present in an ontology
	 * 
	 * @param ontology
	 * @param newEvent
	 * @return
	 */
	public static Set<OWLAxiom> getUniqueAxioms(OWLOntology ontology, Set<OWLAxiom> newEvent) {
		// Find updated object and data properties.
		Set<OWLAxiom> uniqueAxioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : newEvent) {
			if (!ontology.containsAxiom(ax)) {
				uniqueAxioms.add(ax);
			}
		}
		return uniqueAxioms;
	}

	public static OWLOntology copyOntology(OWLOntology baseOntology) {
		OWLOntologyManager manger = baseOntology.getOWLOntologyManager();
		IRI ontIRI = IRI.create(baseOntology.getOntologyID().getOntologyIRI().toString() + "_copy");
		try {
			OWLOntology ontology = manger.createOntology(ontIRI);

			// add imports
			OWLDataFactory factory = manger.getOWLDataFactory();
			baseOntology.getImportsDeclarations().forEach(i -> manger.applyChange(new AddImport(ontology, i)));
			for (OWLOntology importOnt : baseOntology.getImports()) {
				// OWLImportsDeclaration id =
				// factory.getOWLImportsDeclaration(importOnt.getOntologyID().getOntologyIRI().get());
				// AddImport ai = new AddImport(ontology, id);
				// manger.applyChange(ai);
				manger.addAxioms(ontology, importOnt.getAxioms());
			}
			manger.applyChange(new AddImport(ontology, manger.getOWLDataFactory()
					.getOWLImportsDeclaration(IRI.create("http://orca.test/ontology/TaskAccio.owl"))));
			// add defined axioms
			// manger.addAxioms(ontology, baseOntology.getAxioms());
			return ontology;
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static OWLOntology removeABox(OWLOntology ontology) {
		OWLOntologyManager manger = ontology.getOWLOntologyManager();

		OWLDataFactory factory = manger.getOWLDataFactory();
		OWLEntityRemover remover = new OWLEntityRemover(ontology);
		for (OWLNamedIndividual ind : ontology.getIndividualsInSignature()) {
			remover.visit(ind);
			// or ind.accept(remover);
		}
		manger.applyChanges(remover.getChanges());
		return ontology;
	}

	public static void saveOntology(OWLOntology ontology, String name) {
		if (ontology != null) {
			String location = "/tmp/massif/";
			try {
				File file = new File(location + name);
				if (!file.canExecute()) {
					File mkdir = new File(location);
					mkdir.mkdirs();
				}
				file.createNewFile();
				ontology.getOWLOntologyManager().saveOntology(ontology,
						new FileOutputStream(file));
			} catch (OWLOntologyStorageException | IOException e) {
				e.printStackTrace();
			}
		}

	}
	public static void removeOnlyAbox(OWLOntology ontology,Set<OWLAxiom> axioms){
		Set<OWLAxiom> filtered = axioms.stream().filter(a-> (a instanceof OWLClassAssertionAxiom ||
				a instanceof OWLDataPropertyAssertionAxiom||
				a instanceof OWLObjectPropertyAssertionAxiom||
				a instanceof OWLAnnotationAssertionAxiom)).collect(Collectors.toSet());
		ontology.getOWLOntologyManager().removeAxioms(ontology, filtered);
	}
}
