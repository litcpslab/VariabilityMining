package iec61499Mining;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import varflixModel.IVariability;
import varflixModel.IVariabilityGroup;
import varflixModel.IVariant;
import varflixModel.IEC61499.IEC61499Variability;
import varflixModel.IEC61499.IEC61499Variant;
import varflixModel.IEC61499.JSON1499VariabilityGroup;
import variabilityMining.IVariabilityExtractor;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class IEC61499VariabilityExtractor implements IVariabilityExtractor<IEC61499Variant, IEC61499Variability> {
	
	private List<JSON1499VariabilityGroup> groupings;
	
	private List<IEC61499Variant> variants;

	@Override
	public List<IVariabilityGroup<IEC61499Variant, IEC61499Variability>> performAutomaticMining(String variantPath, String inputPath){
	//public List<JSON1499VariabilityGroup> performAutomaticMining(String variantPath, String inputPath){
		variants = parseVariants(variantPath);
		
		groupings = readData(inputPath);
		
		Set<String> groupNames = new HashSet<>();
		
		int count = 1;
		for(JSON1499VariabilityGroup group: groupings) {
			mapOccurrences(group, variants);	
			
			if(group.getElements().size() == 1 && !groupNames.contains(group.getElements().get(0).getElementName())) {
				String name = group.getElements().get(0).getElementName();
				group.setAttributeName(name);
				groupNames.add(name);
			} else if(group.getOccurrences().size() == variants.size()){
				group.setAttributeName("Core");
			} else if(group.getElements().size() > 1 && group.getElements().size() <= 5){
				String groupName = "";
				for(IEC61499Variability element: (List<IEC61499Variability>)group.getElements()) {
					if(element.getNode_id() != null) {
						groupName += "_" + element.getElementName();
					}
				}
				
				if(!groupName.isEmpty() && !groupNames.contains(groupName.substring(1))) {
					groupName = groupName.substring(1);
					group.setAttributeName(groupName);
					groupNames.add(groupName);
				} else {
					group.setAttributeName("Group" + count);
				}
				
			} else {
				group.setAttributeName("Group" + count);
			}
			count++;
			
		}
		
		File mappingFile = new File("mapping.txt");
		try (FileWriter writer = new FileWriter(mappingFile.getAbsolutePath())) {
			for(JSON1499VariabilityGroup group: groupings) {
				writer.write(group.getAttributeName() + "\n");
				writer.write("Variants:\n");
				
				StringBuilder builder = new StringBuilder();
				for(IVariant variant: group.getOccurrences()) {
					builder.append(variant.getName() + "\n");
				}
				builder.deleteCharAt(builder.length() - 1);
				writer.write(builder.toString() + "\n");
				writer.write("Elements:\n");
				
				List<? extends IVariability> variabilities = group.getElements();
				
				for(IVariability variability: variabilities) {
					writer.write(variability.toString() + "\n");
				}
				writer.write("\n");
			}     
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return new ArrayList<>(groupings);
	}
	
	private void mapOccurrences(JSON1499VariabilityGroup group, List<IEC61499Variant> variants) {
		String label = group.getLabel();
		
		IEC61499Variant variant = variants.stream().filter(v -> v.getName().equals(label)).findAny().orElse(null);
		
		if(variant != null) {
			group.addOccurrence(variant);
		} else {
			for(IEC61499Variant current: variants) {
				if(label.contains(current.getName() + "_") || label.indexOf(current.getName()) + current.getName().length() == label.length()) {
					group.addOccurrence(current);
				}
			}
		}
	}

	private List<IEC61499Variant> parseVariants(String fileName) {
		List<IEC61499Variant> variants = new ArrayList<>();
		
		try(Stream<String> stream = Files.lines(Paths.get(fileName))) {
			stream.forEach(s -> variants.add(new IEC61499Variant(s)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return variants;
	}

	private List<JSON1499VariabilityGroup> readData(String inputPath) {
		int groupId = 0;
		
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(IEC61499Variability.class, new IEC61499VariabilityInstanceCreator());
			
		try(FileReader reader = new FileReader(inputPath)) {
			TypeToken<Map<String, List<IEC61499Variability>>> mapType = new TypeToken<Map<String, List<IEC61499Variability>>>(){};
			
			
			Map<String, List<IEC61499Variability>> map = gson.create().fromJson(reader, mapType);
			
			List<JSON1499VariabilityGroup> groupings = new ArrayList<>();
			
			for(String key: map.keySet()) {
				if(!map.get(key).isEmpty()) {
					groupings.add(new JSON1499VariabilityGroup(groupId, key, map.get(key)));
					groupId++;
				}
			}
				
			return groupings;
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return new ArrayList<>();
	}
	
	public List<IEC61499Variant> getVariants() {
		return variants;
	}
	
	public List<IEC61499Variability> getElements(){
		List<IEC61499Variability> elements = new ArrayList<>();
		
		for(JSON1499VariabilityGroup group: groupings) {
			elements.addAll(group.getElements());
		}
		
		return elements;
	}
		

}
