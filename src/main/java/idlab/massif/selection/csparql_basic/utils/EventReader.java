/**
 * 
 */
package idlab.massif.selection.csparql_basic.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * @author pbonte
 *
 */
public class EventReader {

	
	private List<String> jsonld;
	public EventReader(String fileLocation) {
		this.jsonld = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get(fileLocation))) {

			stream.forEach(a -> jsonld.add(a));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List<Statement> getStringStatements(int numEvents){
		List<Statement> stmnts = new ArrayList<Statement>();
		for(int i=0;i<numEvents;i++){
			String message = jsonld.get(i);
			String converted = convert(message);
			String[] triples = converted.split("\n");
			for(String triple:triples){
				String[] spo=triple.split(" ");
				if(spo[1].contains("hasValue")){
					Double value = Double.parseDouble(spo[2].substring(1, spo[2].length()-1));
					stmnts.add(ResourceFactory.createStatement(ResourceFactory.createResource(spo[0]), ResourceFactory.createProperty(spo[1]), ResourceFactory.createTypedLiteral(value)));

				}else{
				stmnts.add(ResourceFactory.createStatement(ResourceFactory.createResource(spo[0]), ResourceFactory.createProperty(spo[1]), ResourceFactory.createResource(spo[2])));
				}			}
		}
		return stmnts;
	}
	public String convert(String message) {
		// Open a valid json(-ld) input file
		InputStream inputStream;
		try {

			// Read the file into an Object (The type of this object will be a
			// List, Map, String, Boolean,
			// Number or null depending on the root object in the file).
			Object jsonObject = JsonUtils.fromString(message);
			// Create a context JSON map containing prefixes and definitions
			Map context = new HashMap();
			// Customise context...
			// Create an instance of JsonLdOptions with the standard JSON-LD
			// options
			JsonLdOptions options = new JsonLdOptions();
			// options.format="turtle";
			// Customise options...
			// Call whichever JSONLD function you want! (e.g. compact)
			Object compact = JsonLdProcessor.toRDF(((Map) jsonObject).get("@graph"), options);
			// Print out the result (or don't, it's your call!)
			return toTTL((List<Map<String, Map<String, String>>>) ((Map) compact).get("@default"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String toTTL(List<Map<String, Map<String, String>>> tripleList) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Map<String, Map<String, String>> tripleMap : tripleList) {
			builder.append(tripleMap.get("subject").get("value")).append(" ")
					.append(tripleMap.get("predicate").get("value")).append(" ");

			if (tripleMap.get("object").get("type").equals("literal")) {
				builder.append("\"").append(tripleMap.get("object").get("value")).append("\"")
//				 .append("^^")
//				 .append("<http://www.w3.org/2001/XMLSchema#string>")

//				 append("<").append(tripleMap.get("object").get("datatype")).append(">")
				;

			} else {
				builder.append(tripleMap.get("object").get("value"));
			}
			builder.append(" .\n");

		}

		return builder.toString();
	}

}
