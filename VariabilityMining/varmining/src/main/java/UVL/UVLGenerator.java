package UVL;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import constraints.SimpleConstraint;
import variabilityMining.Feature;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class UVLGenerator {
	
	private static final String PATH = "model.uvl";

	public static void createUVLModel(Feature root, List<SimpleConstraint> ctcs) {
		
		 try (FileWriter writer = new FileWriter(PATH, false)) { 
			 
			writer.write("features\n");
			
			processNode(root, writer, 1);
	        
			if(!ctcs.isEmpty()) {
				writer.write("constraints\n");
			}
			
			for(SimpleConstraint constraint: ctcs) {
				writer.write("\t" + constraint.toString() + "\n");
			}

	     } catch(IOException e) {
	    	 e.printStackTrace();
	     }
	}
	
	private static void processNode(Feature current, FileWriter writer, int indentCount) throws IOException {
		if(current == null) {
			return;
		}
		
		if(current.getName().startsWith("ALT") || current.getName().startsWith("OR")) {
			writer.write("\t".repeat(indentCount) + current.getName() + "  {abstract}\n");
		} else {
			writer.write("\t".repeat(indentCount) + current.getName() + "\n");
		}
		
		
		indentCount++;
		
		List<Feature> children = current.getChildren();
		
		if(children == null) {
			return;
		}
		
		List<Feature> mandatoryChildren = children.stream().filter(f -> f.isMandatory()).toList();
		
		if(!mandatoryChildren.isEmpty()) {
			writer.write("\t".repeat(indentCount) + "mandatory" + "\n");
			indentCount++;
			for(Feature mandatoryChild: mandatoryChildren) {
				processNode(mandatoryChild, writer, indentCount);
			}
			indentCount--;
		}
		
		List<Feature> optionalChildren = children.stream().filter(f -> !f.isMandatory()).toList();
		
		if(!optionalChildren.isEmpty()) {
			if(current.isOrParent()) {
				writer.write("\t".repeat(indentCount) + "or" + "\n");
			} else if(current.isAlternativeParent()) {
				writer.write("\t".repeat(indentCount) + "alternative" + "\n");
			} else {
				writer.write("\t".repeat(indentCount) + "optional" + "\n");
			}
			
			indentCount++;
			for(Feature optionalChild: optionalChildren) {
				processNode(optionalChild, writer, indentCount);
			}
			indentCount--;
		}
	}

}
