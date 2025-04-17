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
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import variabilityMining.IVariability;
import variabilityMining.IVariabilityExtractor;
import variabilityMining.IVariabilityGroup;
import variabilityMining.IVariant;
import variabilityMining.ProductComparisonMatrix;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class IEC61499VariabilityExtractor implements IVariabilityExtractor {
	
	public final static String INPUT_PATH = "Put the path to the output file of the comparison tool here";
	
	private List<JSON1499VariabilityGroup> groupings;

	@Override
	public ProductComparisonMatrix mineVariabilities(String variantPath) {
		
		List<IEC61499Variant> variants = parseVariants("Put the path to the file containing the name of the variants here");
		
		groupings = readData(INPUT_PATH);
		
		int count = 1;
		for(JSON1499VariabilityGroup group: groupings) {
			group.setAttributeName("Group" + count);
			count++;
			mapOccurrences(group, variants);		
		}
		
		File mappingFile = new File("mapping.txt");
		try (FileWriter writer = new FileWriter(mappingFile.getAbsolutePath())) {
			for(JSON1499VariabilityGroup group: groupings) {
				writer.write(group.getAttributeName() + "\n");
				writer.write("Variants: \n");
				
				StringBuilder builder = new StringBuilder();
				for(IVariant variant: group.getOccurrences()) {
					builder.append(variant.getName() + "\n");
				}
				builder.deleteCharAt(builder.length() - 1);
				writer.write(builder.toString() + "\n");
				writer.write("Elements:\n ");
				
				List<? extends IVariability> variabilities = group.getElements();
				
				for(IVariability variability: variabilities) {
					writer.write(variability.toString() + "\n");
				}
				writer.write("\n");
			}     
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		feedbackLoop();
		
		ProductComparisonMatrix pcm = new ProductComparisonMatrix(variants, groupings);
		
		for(IVariabilityGroup group : groupings) {
			pcm.setElementOccurrences(group);
		}
		
		File pcmFile = new File("pcm.csv");
		try (FileWriter writer = new FileWriter(pcmFile.getAbsolutePath())) {
            writer.write(pcm.toCSV());     
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		try (FileWriter writer = new FileWriter(mappingFile.getAbsolutePath())) {
			for(JSON1499VariabilityGroup group: groupings) {
				writer.write(group.getAttributeName() + "\n");
				writer.write("Variants: \n");
				
				StringBuilder builder = new StringBuilder();
				for(IVariant variant: group.getOccurrences()) {
					builder.append(variant.getName() + "\n");
				}
				builder.deleteCharAt(builder.length() - 1);
				writer.write(builder.toString() + "\n");
				writer.write("Elements:\n ");
				
				List<? extends IVariability> variabilities = group.getElements();
				
				for(IVariability variability: variabilities) {
					writer.write(variability.toString() + "\n");
				}
				writer.write("\n");
			}     
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
	
		
		return pcm;
	}

	private void feedbackLoop() {
		do {
			System.out.println("Edit the groupings:");
			System.out.println("All groups can be found in the file \"mapping.txt\"");
			System.out.println();
			
			System.out.println("You have the following options: ");
			System.out.println("1 - Rename a group");
			System.out.println("2 - Remove groups");
			System.out.println("3 - Edit an existing grouping");
			System.out.println("4 - End editing");
			
			Scanner in = new Scanner(System.in);
			int option = in.nextInt();
				
			switch(option) {
				case 1:
					System.out.println("Please enter the name of the group that should be renamed: ");
					
					in.nextLine();	 
					String oldGroupName = in.nextLine();
					if(oldGroupName.equals("!end")) {
						break;
					}
					     
					JSON1499VariabilityGroup selected = groupings.stream().filter(group -> group.getAttributeName().equals(oldGroupName)).findAny().orElseGet(() -> null);
					     
					if(selected != null) {
						System.out.println("Enter the new name: ");
						String newGroupName = in.nextLine();
					    selected.setAttributeName(newGroupName);
					    System.out.println("Group was successfully renamed!");
					} else {
					  	System.out.println("No such group exists!");
					}
					break;
				case 2:
					System.out.println("Which groupings should be removed from consideration?");
					System.out.println();
					
					System.out.println("Please enter the names of the groups to be removed (the others will be kept)!");
					in.nextLine();
					do {
						System.out.println("Please enter a valid group name: (!end to stop)");
						 
					     String groupName = in.nextLine();
					     if(groupName.equals("!end")) {
					    	 break;
					     }
					     
					    JSON1499VariabilityGroup removeGroup = groupings.stream().filter(group -> group.getAttributeName().equals(groupName)).findAny().orElseGet(() -> null);
					     
					     if(removeGroup != null) {
					    	 groupings.remove(removeGroup);
					    	 System.out.println("Group was found successfully!");
					     } else {
					    	 System.out.println("No such group exists!");
					     }
					}while(true);
					break;
				case 3:
					System.out.println("Please enter the name of the group that should be edited: ");
					
					System.out.println("Please enter a valid group name: (!end to stop)");
					in.nextLine();	 
					String editGroupName = in.nextLine();
					if(editGroupName.equals("!end")) {
						break;
					}
						     
					JSON1499VariabilityGroup editGroup = groupings.stream().filter(group -> group.getAttributeName().equals(editGroupName)).findAny().orElseGet(() -> null);
						
					System.out.println("How would you like to edit this group?");
					System.out.println("1 - Remove an element from the group");
					System.out.println("2 - Move an element to another group");
						
					int editOption = in.nextInt();
											
					List<? extends IVariability> editElements = editGroup.getElements().stream().toList();
							
					for(int i = 0; i < editElements.size(); i++) {
						System.out.println(i + " - " + editElements.get(i).toString());		
					}
						
					int removeId = in.nextInt();
						
					while(removeId < 0 || removeId > editElements.size() - 1) {
						System.out.println("Invalid selection! Please select a valid element: (0 - " + (editElements.size() - 1) + ")");
						removeId = in.nextInt();
					}
							
					IVariability removeElement = editElements.get(removeId);
							
					editGroup.getElements().remove(removeElement);
							
					if(editOption == 2) {
							
						JSON1499VariabilityGroup addGroup = null;
						while(addGroup == null) {
							System.out.println("Please enter the name of the group you want to move the element to: ");
							String addGroupName = in.nextLine();
							addGroup = groupings.stream().filter(group -> group.getAttributeName().equals(addGroupName)).findAny().orElseGet(() -> null);
						} 
							
						List<IVariability> vars = (List<IVariability>) addGroup.getElements();
						vars.add(removeElement);
					}				
					break;
				default:
					return;
			}
		}while (true);
	}

	private void mapOccurrences(JSON1499VariabilityGroup group, List<IEC61499Variant> variants) {
		String label = group.getLabel();
		
		IEC61499Variant variant = variants.stream().filter(v -> v.getName().equals(label)).findAny().orElse(null);
		
		if(variant != null) {
			group.addOccurrence(variant);
		} else {
			for(IEC61499Variant current: variants) {
				if(label.contains(current.getName())) {
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
		
		Gson gson = new Gson();
			
		try(FileReader reader = new FileReader(INPUT_PATH)) {
			TypeToken<Map<String, List<IEC61499Variability>>> mapType = new TypeToken<Map<String, List<IEC61499Variability>>>(){};
			
			Map<String, List<IEC61499Variability>> map = gson.fromJson(reader, mapType);
			
			List<JSON1499VariabilityGroup> groupings = new ArrayList<>();
			
			for(String key: map.keySet()) {
				if(!map.get(key).isEmpty()) {
					groupings.add(new JSON1499VariabilityGroup(key, map.get(key)));
				}
			}
				
			return groupings;
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return new ArrayList<>();
	}
		

}
