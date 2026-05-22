/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Alexander Stummer - Initial Implementation
********************************************************************************/

package iec61499Mining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import constraints.Constraint;
import constraints.Implication;
import constraints.SimpleConstraint;
import varflixModel.IEC61499.IEC61499Variability;
import varflixModel.IEC61499.JSON1499VariabilityGroup;
import variabilityMining.Feature;

public class DeltaModelGenerator {
	
	public static void generateDeltas(List<JSON1499VariabilityGroup> groupings, String outputPath) {		
		for(JSON1499VariabilityGroup group: groupings) {
			String deltaName = "D" + group.getAttributeName();
			Path directoryPath = Paths.get(outputPath);
			try {
				Files.createDirectories(directoryPath);
				Path filePath = directoryPath.resolve(deltaName + ".delta");
				//File deltaFile = new File(outputPath + deltaName + ".delta");
				try (FileWriter writer = new FileWriter(filePath.toFile())) {
					writer.write("delta " + deltaName + ";\n");
					writer.write("uses VariabilityMiningDeltaApp;\n\n");
					
					writer.write("{\n");
					List<IEC61499Variability> elements = group.getElements();
					
					List<IEC61499Variability> functionBlocks = elements.stream().filter(e -> e.getNode_id() != null && e.getType() != null).toList();
					List<IEC61499Variability> connections = elements.stream().filter(e -> e.getEdge_source() != null).toList();
					
					for(IEC61499Variability functionBlock: functionBlocks) {
						writer.write("\t<Add> FB name=" + functionBlock.getNode_id().substring(functionBlock.getNode_id().lastIndexOf(";")+1) + " type=" + functionBlock.getType() + ";\n");
					}
					
					for(IEC61499Variability connection: connections) {
						String[] sourceParts = connection.getEdge_source().split(";");
						String source = sourceParts[sourceParts.length - 2] + "." + sourceParts[sourceParts.length - 1];
						String[] targetParts = connection.getEdge_target().split(";");
						String target = targetParts[targetParts.length - 2] + "." + targetParts[targetParts.length - 1];
						
						if(connection.getType() != null && connection.getType().equals("event")) {
							writer.write("\t<Add> EventConnection source=" + source + " dest=" + target + ";\n");
						} else {
							writer.write("\t<Add> DataConnection source=" + source + " dest=" + target + ";\n");
						}
					}
					writer.write("}");
				
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public static void generateDeltaConfigFile(List<Feature> features, List<Constraint> constraints, String outputPath) {
		
		Path directoryPath = Paths.get(outputPath);
		try {
			Files.createDirectories(directoryPath);
			Path configFilePath = directoryPath.resolve("DeltaConfiguration.deltaconf");//new File(outputPath + "DeltaConfiguration.deltaconf");
			try (FileWriter writer = new FileWriter(configFilePath.toFile())) {
				writer.write("productline MinedPL;\n");
				writer.write("variations ");
				
				Map<Feature, String> featurePathMap = new HashMap<>();
				StringBuilder variationsBuilder = new StringBuilder();
				for(Feature feature: features) {
							
					if(!(feature.getName().startsWith("ALT") || feature.getName().startsWith("OR"))) {
						
						
						Stack<Feature> featureStack = new Stack<>();
						Feature current = feature.getParent();
						
						while(current != null) {
							featureStack.push(current);
							current = current.getParent();
						}
						
						String path = "";
						
						while(!featureStack.empty()) {
							path += (featureStack.pop().getName() + "#");
						}
						path += feature.getName();
						featurePathMap.put(feature, path);
						variationsBuilder.append(path + ",\n");
					}
				}
				variationsBuilder.delete(variationsBuilder.length() - 2, variationsBuilder.length());
				variationsBuilder.append(";\n");
				
				writer.write(variationsBuilder.toString());
				
				for(Feature feature: featurePathMap.keySet()) {
					String deltaName = "D" + feature.getName();
					writer.write("delta " + deltaName);
					
					String path = featurePathMap.get(feature);
					String[] featureNames = path.split("#");

					if(featureNames.length > 1) {
						writer.write(" after ");
						for(int i = featureNames.length -2; i >= 0; i--) {
							if(!(featureNames[i].startsWith("ALT")|| featureNames[i].startsWith("OR"))) {
								writer.write("D" + featureNames[i]);
								if(i != 0) {
									writer.write(" && ");
								}
							}
						}
					}
					
					List<Implication> relevantImplications = constraints.stream().filter(c -> c instanceof Implication).map(c -> (Implication)c).filter(implication -> implication.getFeature1().equals(feature)).toList();
					
					for(Implication implication: relevantImplications) {
						if(!path.contains("#" + implication.getFeature2().getName() + "#")) {
							writer.write(" && D" + implication.getFeature2().getName());
						}
					}
					
					
					writer.write(" when " + featurePathMap.get(feature) + ";\n");
				}
						
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
