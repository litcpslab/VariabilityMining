package variabilityMining;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import constraints.AlternativeGroup;
import constraints.Constraint;
import constraints.Equivalence;
import constraints.Group;
import constraints.Implication;
import constraints.MutualExclusion;
import constraints.OrRelation;
import constraints.SimpleConstraint;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class ConstraintFileIO {

	
	public static JSONConstraints readInformationfromFile(File source) {
		
		RuntimeTypeAdapterFactory<SimpleConstraint> simpleConstraintAdapterFactory =
			    RuntimeTypeAdapterFactory
			        .of(SimpleConstraint.class, "type") 
			        .registerSubtype(Equivalence.class, "Equivalence")
			        .registerSubtype(Implication.class, "Implication")
			        .registerSubtype(MutualExclusion.class, "Mutual Exclusion");
		
		RuntimeTypeAdapterFactory<Group> groupConstraintAdapterFactory =
			    RuntimeTypeAdapterFactory
			        .of(Group.class, "type") 
			        .registerSubtype(OrRelation.class, "Or Group")
			        .registerSubtype(AlternativeGroup.class, "Alternative");
		
		Gson gson = new GsonBuilder()
				.registerTypeAdapterFactory(simpleConstraintAdapterFactory)
				.registerTypeAdapterFactory(groupConstraintAdapterFactory).create();
		
		try(FileReader reader = new FileReader(source)) {
			JSONConstraints jsonConstraints = gson.fromJson(reader, JSONConstraints.class);
			
			for(Group g: jsonConstraints.getGroups()) {
				if(g instanceof AlternativeGroup) {
					g.setType("Alternative");
				} else {
					g.setType("Or Group");
				}
			}
			
			for(SimpleConstraint c: jsonConstraints.getConstraints()) {
				if(c instanceof Equivalence) {
					c.setType("Equivalence");
				} else if(c instanceof Implication) {
					c.setType("Implication");
				} else {
					c.setType("Mutual Exclusion");
				}
			}
			
			return jsonConstraints;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void writeConstraintsToFile(File selectedFile, Set<Constraint> constraints, List<Feature> features, Feature base) {
		
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		
		List<Group> groupList = constraints.stream().filter(c -> c instanceof Group).map(c -> (Group) c).collect(Collectors.toList());
		
		List<Constraint> ctcs = constraints.stream().filter(c -> !(c instanceof Group)).collect(Collectors.toList());

		try(FileWriter writer = new FileWriter(selectedFile)) {
			JsonObject jsonObject = new JsonObject();
			
			JsonArray featureArray = gson.toJsonTree(features).getAsJsonArray();
			
			jsonObject.add("features", featureArray);
			
			if(base != null) {
				JsonElement baseFeature = gson.toJsonTree(base);
				
				jsonObject.add("base", baseFeature);
			}
			
			JsonArray groupArray = gson.toJsonTree(groupList).getAsJsonArray();
			
			jsonObject.add("groups", groupArray);
			
			JsonArray ctcArray = gson.toJsonTree(ctcs).getAsJsonArray();
			
			jsonObject.add("constraints", ctcArray);
			
			gson.toJson(jsonObject, writer);
			
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
	}
}
