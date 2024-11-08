package variabilityMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*Contributors:
*Alexander Stummer - initial API and implementation*
*/

public class JavaVariabilityExtractor implements IVariabilityExtractor {

	private Map<Set<Variant>, Set<CtVariability>> occurrenceMap = new HashMap<>();
	private Set<Variant> variants = new HashSet<>(); 
	private Map<Variant, Set<CtVariability>> variabilityMap = new HashMap<>();	
	
	
	@Override
	public ProductComparisonMatrix mineVariabilities(String variantPath) {
		File directory = new File(variantPath);
		String[] directoryNames = new String[1];
		
		if(directory.isDirectory()) {
			directoryNames = directory.list();
		}
		
		Map<Variant, Set<Variant>> finishedComparisons = new HashMap<>();
		  
		
		for(int i = 0; i < directoryNames.length; i++) { 
			String variant = directoryNames[i];  
				
			String path = directory + "\\" + variant; ;
			Path variantDirectory = Paths.get(path);
				
			Variant info = new Variant(variant, path, variantDirectory);
			variants.add(info);
			finishedComparisons.put(info, new HashSet<>());
		}

		CustomAstComparator comparator = new CustomAstComparator();
		
		for(Variant info : variants) {
			variabilityMap.put(info, new HashSet<>());
			
			for(Variant info2 : variants) {
				info.compareFiles(info2.getFiles());
			}
			
			info.buildModel();
		}
		
		for(Variant variant : variants) {
			CtModel model1 = variant.getModel();

			for(Variant variant2 : variants) {
				if(variant != variant2 && !finishedComparisons.get(variant).contains(variant2)) {
					System.out.println("Diffing " + variant + " with " + variant2);
					CtModel model2 = variant2.getModel();

					
					List<Operation<?>> operations = comparator.compare(model1.getRootPackage(), model2.getRootPackage());
					
					for(Operation<?> operation : operations) {
						if(operation instanceof InsertOperation || operation instanceof DeleteOperation) {
							
							CtElement element = operation.getSrcNode();	
							
							CtVariability variability = new CtVariability(element);
							
							if(operation instanceof InsertOperation) {
								variabilityMap.get(variant2).add(variability);

							} else {
								variabilityMap.get(variant).add(variability);
							}
							
						} else if(operation instanceof UpdateOperation) {
							CtElement src = operation.getSrcNode();
							CtElement dst = operation.getDstNode();
							CtVariability srcVariability = new CtVariability(src);
							CtVariability dstVariability = new CtVariability(dst);
							
							variabilityMap.get(variant).add(srcVariability);

							variabilityMap.get(variant2).add(dstVariability);
						}
					
						
					}
					System.out.println("Compared " + variant + " with " + variant2);
					finishedComparisons.get(variant).add(variant2);
					finishedComparisons.get(variant2).add(variant);
				}
			}
		}
		System.out.println("Comparisons finished");
		
		Set<CtVariability> uniqueVariabilities = new HashSet<>();
		
		for(Variant variant : variants) {	
			uniqueVariabilities.addAll(variabilityMap.get(variant));
		}
		
		System.out.println("Processed all elements!");
		
		for(CtVariability element : uniqueVariabilities) {
			
				Set<Variant> key = new HashSet<>();
				
				for(Variant info : variants) {
					if(variabilityMap.get(info).contains(element)) {
						key.add(info);
					} else {
						CtVariability equalElement = getEqualKey(element, variabilityMap.get(info));
						if(equalElement != null) {
							key.add(info);
						} 						
					}
				}
				
				if(!occurrenceMap.containsKey(key)) {
					occurrenceMap.put(key, new HashSet<>());
				}
				occurrenceMap.get(key).add(element);
		}
		
		System.out.println("Built occurrence map!");
		
		
		occurrenceMap.remove(variants);
			
		System.out.println("Building groups");
		Set<VariabilityGroup> groups = new HashSet<>();
		int i = 1;
		File file = new File("groupMappings.txt");
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Set<Variant> information : occurrenceMap.keySet()) {
			
			Set<IVariability> elements = new HashSet<>();
			
			Set<Path> commonVariableFiles = new HashSet<>();
			information.stream().map(info -> info.getVariablePaths()).forEach(paths -> commonVariableFiles.addAll(paths));
			for(Variant info : information) {
				commonVariableFiles.retainAll(info.getVariablePaths());
			}
			for(Path path : commonVariableFiles) {
				boolean maxGroup = true;
				//Check if current group is the max group for the path
				Set<Variant> infos = new HashSet<>();
				infos.addAll(variants);
				infos.removeAll(information);
				for(Variant info : infos) {
					if(info.getVariablePaths().contains(path)) {
						maxGroup = false;
						break;
					}
				}
				
				if(maxGroup) {
					elements.add(new FileVariability(path.toString()));
				}
			}
			
			elements.addAll(occurrenceMap.get(information));
			VariabilityGroup group = new VariabilityGroup("Group" + i, information, elements);
			groups.add(group);
			i++;
			
			writeGroupInformationFile(file, information, group);

		}
		
		groups = feedbackLoop(groups);
		
		File filteredFile = new File("reducedGroupMappings.txt");
		filteredFile.delete();
		try {
			filteredFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(VariabilityGroup group : groups) {
			writeGroupInformationFile(filteredFile, group.getOccurrences(), group);
		}
		
		
		System.out.println("Building pcm!");
		ProductComparisonMatrix pcm = buildPCM(groups);
		
		File pcmFile = new File("pcm.csv");
		try (FileWriter writer = new FileWriter(pcmFile.getAbsolutePath())) {
            writer.write(pcm.toCSV());  
           
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
		
		return pcm;
	}

	private void writeGroupInformationFile(File file, Set<Variant> information, VariabilityGroup group) {		
		try (FileWriter writer = new FileWriter(file.getAbsolutePath(), true)) {
			 writer.write(group.getAttributeName() + ": \n"); 
		     writer.write("Occurrences: \n"); 
		     
		     for(Variant info : information) {
		    	 writer.write(info.getName() + "\n");
		        
		     }
		     writer.write("Elements: \n"); 
		     for(IVariability element : group.getElements()) {
		    	 writer.write(element.getLocation() + "\n");
		     }
		          
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	private Set<VariabilityGroup> feedbackLoop(Set<VariabilityGroup> groups) {
		
		Set<VariabilityGroup> filteredGroups = new HashSet<>();
		
		System.out.println("Which groupings should be considered for the rest of the process?");
		System.out.println("All groups can be found in the file \"groupMappings.txt\"");
		System.out.println();
		
		System.out.println("Please enter the names of the groups to be kept (the others will be dropped)!");
		Scanner in = new Scanner(System.in);
		do {
			System.out.println("Please enter a valid group name: (!end to stop)");
			 
		     String groupName = in.nextLine();
		     if(groupName.equals("!end")) {
		    	 break;
		     }
		     
		     VariabilityGroup selected = groups.stream().filter(group -> group.getAttributeName().equals(groupName)).findAny().orElseGet(() -> null);
		     
		     if(selected != null) {
		    	 filteredGroups.add(selected);
		    	 System.out.println("Group was found successfully!");
		     }
		}while(true);
		in.close();
		return filteredGroups;
	}

	private ProductComparisonMatrix buildPCM(Set<VariabilityGroup> groups) {

		ProductComparisonMatrix pcm = new ProductComparisonMatrix(variants, groups);
		
		for(IVariabilityGroup group : groups) {
			pcm.setElementOccurrences(group);
		}

		return pcm;		
	}
	
	private CtVariability getEqualKey(CtVariability element, Set<CtVariability> candidates) {
		String elementString = element.getVariability().prettyprint();
		for(CtVariability key : candidates) {
			String compareString = key.getVariability().prettyprint();
			if(elementString.equals(compareString) || compareString.contains(elementString)) {
				return key;
			}
		}
		
		return null;
	}
	

	
}
