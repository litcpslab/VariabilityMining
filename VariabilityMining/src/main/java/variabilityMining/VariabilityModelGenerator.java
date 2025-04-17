package variabilityMining;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import UVL.UVLGenerator;
import constraints.Group;
import iec61499Mining.JSON1499VariabilityGroup;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class VariabilityModelGenerator {

	private String source;
	
	private FMFeature root;
	
	private List<FMFeature> features;
	
	private List<SimpleConstraint> coveredConstraints;
	
	private int altCount = 0;
	
	private boolean changesMade = false;

	public VariabilityModelGenerator(String filePath) {
		this.source = filePath;
		this.coveredConstraints = new ArrayList<>();
	}
	
	
	public void generateVariabilityModel() {
		
		JSONConstraints constraints = readInformationfromFile();
		
		Feature base = constraints.getBase() == null? new Feature("Base"): constraints.getBase();
	
		features = constraints.getFeatures();
		
		do{
			root = features.stream().filter(f -> f.getName().equals(base.getName())).findFirst().orElseGet(() -> new FMFeature("Base"));
			root.setMandatory(true);
			
			List<SimpleConstraint> baseEquivalences = constraints.getConstraints().stream()
					.filter(c -> c.getType().equals("Equivalence") && (c.getFeature1().equals(base) || c.getFeature2().equals(base))).toList();
			
			generateMandatoryFeatures(base, baseEquivalences);
			
			coveredConstraints.addAll(baseEquivalences);
			
			buildGroups(base, constraints.getGroups());
			
			List<FMFeature> uncoveredFeatures = features.stream().filter(f -> f.getParent() == null).toList();
			
			List<String> uncoveredFeatureNames = uncoveredFeatures.stream().map(f -> f.getName()).toList();
			
			List<SimpleConstraint> relevantConstraints = constraints.getConstraints().stream()
					.filter(f -> uncoveredFeatureNames.contains(f.getFeature1().getName()) || uncoveredFeatureNames.contains(f.getFeature2().getName())).toList();
			
			buildOtherRelations(uncoveredFeatures, relevantConstraints);
			
			List<SimpleConstraint> ctcs = constraints.getConstraints().stream().filter(c -> !coveredConstraints.contains(c)).toList();
			
			UVLGenerator.createUVLModel(root, ctcs);
			changesMade = false;
			involveUser(constraints);
		} while(changesMade);
		
	}


	private void involveUser(JSONConstraints constraints) {
		
		
		System.out.println("Edit the constraints");
		System.out.println("All groups can be found in the file \"constraints.json\"");
		System.out.println("The current variability model can be found in \"model.uvl\"");
		System.out.println();
		
		System.out.println("You have the following options: ");
		System.out.println("1 - Add additional constraint");
		System.out.println("2 - Remove a constraint");
		System.out.println("3 - Edit an existing grouping");
		System.out.println("4 - End editing");
		userInputLoop:
		do {
			System.out.println("Please enter a valid option: (4 to stop)");
			Scanner in = new Scanner(System.in);

		    Integer line = in.nextInt();

		    switch (line) {
				case 1:
					String type = "";
					while(!(type.equals("Implication") || type.equals("Mutual Exclusion") || type.equals("Equivalence"))) {
						System.out.println("Enter type of constraint: (Implication, Mutex or Equivalence)");
						type = in.next();
					} 
					FMFeature leftFeature = null;
					do {
						System.out.println("Enter left feature: ");
						String name = in.nextLine();
						leftFeature = features.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
					} while(leftFeature == null);
					FMFeature rightFeature = null;
					do {
						System.out.println("Enter right feature: ");
						String name = in.next();
						rightFeature = features.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
					} while(rightFeature == null);
					
					constraints.getConstraints().add(new SimpleConstraint(new Feature(leftFeature.getName()), new Feature(rightFeature.getName()), type));
					changesMade = true;
					break;
				case 2:
					String input;
					do {
						System.out.println("Enter type of constraint: (Implication, Mutex or Equivalence)");
						in.nextLine();
						input = in.nextLine();
					} while(!(input.equals("Implication") || input.equals("Mutual Exclusion") || input.equals("Equivalence")));
					
					
					String constraintType = input;
					List<SimpleConstraint> relevant = constraints.getConstraints().stream().filter(c -> c.getType().equals(constraintType)).toList();
					
					System.out.println("Choose the constraint to remove: ");
					int index = 0;
					for(SimpleConstraint candidate: relevant) {
						System.out.println(index + " - " + candidate.toString());
						index++;
					}
				
					int choice = in.nextInt();
					
					SimpleConstraint removedConstraint = relevant.get(choice);
					constraints.getConstraints().remove(removedConstraint);
					changesMade = true;
					break;
				case 3:
					Group group = null;
					do {
						System.out.println("Enter parent of the group you want to edit: ");
						String name = in.next();
						FMFeature groupParent = features.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
						group = constraints.getGroups().stream().filter(g -> g.getParent().getName().equals(groupParent.getName())).findFirst().orElse(null);
					} while(group == null);
					
					String operation = in.next();
					
					if(operation.equals("Add")) {
						FMFeature addFeature = null;
						do {
							System.out.println("Enter feature to add: ");
							String name = in.next();
							addFeature = features.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
						} while(addFeature == null);
						
						resolveChange(constraints, addFeature, group);
						group.addFeature(new Feature(addFeature.getName()));
					} else if(operation.equals("Remove")) {
						List<Feature> removeCandidates = group.getFeatures();	
						
						System.out.println("Choose the feature to remove: ");
						int count = 1;
						for(Feature candidate: removeCandidates) {
							System.out.println(count + " - " + candidate.getName());
							count++;
						}
					
						Integer option = in.nextInt();
						
						Feature removeFeature = removeCandidates.get(option - 1);
						
						removeCandidates.remove(option - 1);
						
						constraints.getConstraints().add(new SimpleConstraint(removeFeature, group.getParent(), "Implication"));
						
						if(group.getType().equals("Alternative")) {
							for(Feature candidate: removeCandidates) {
								constraints.getConstraints().add(new SimpleConstraint(removeFeature, candidate, "Mutual Exclusion"));
							}
						}
					}
					
					changesMade = true;				
					break;
				case 4:
					break userInputLoop;
		    }
		    
		}while(true);
	}


	private void resolveChange(JSONConstraints constraints, FMFeature addFeature, Group newGroup) {
		List<Group> groups = constraints.getGroups();
		
		Feature member = new Feature(addFeature.getName());
		Group matchedGroup = null;
		
		for(Group group: groups) {
			if(group.getFeatures().contains(member)) {
				matchedGroup = group;
				break;
			}
		}
		
		if(matchedGroup != null) {
			Feature oldParent = matchedGroup.getParent();
			
			constraints.getConstraints().add(new SimpleConstraint(member, oldParent, "Implication"));
			matchedGroup.getFeatures().remove(member);
			
			if(matchedGroup.getType().equals("Alternative")) {
				for(Feature feature: matchedGroup.getFeatures()) {
					constraints.getConstraints().add(new SimpleConstraint(member, feature, "Mutual Exclusion"));
				}
			}
		}
		
		List<SimpleConstraint> relevantConstraints = constraints.getConstraints().stream().filter(c -> c.getFeature1().equals(member) || c.getFeature2().equals(member)).toList();
		
		List<SimpleConstraint> removeCandidates = new ArrayList<>();
		
		for(SimpleConstraint relConstraint: relevantConstraints) {
			
			
			if(relConstraint.getType().equals("Implication") && relConstraint.getFeature2().equals(newGroup.getParent())) {
				removeCandidates.add(relConstraint);
			}
			
			Feature other = relConstraint.getFeature1().equals(member) ? relConstraint.getFeature2():relConstraint.getFeature1();
			
			if(newGroup.getType().equals("Alternative") && relConstraint.getType().equals("Mutual Exclusion") && newGroup.getFeatures().contains(other)) {
				removeCandidates.add(relConstraint);
			}
		}
		
	}


	/*
	 * Check the remaining constraints and determine which are CTCs and which can directly be integrated in the variability model
	 */
	private void buildOtherRelations(List<FMFeature> uncoveredFeatures, List<SimpleConstraint> relevantConstraints) {
		
		List<SimpleConstraint> relevantEquivalences = relevantConstraints.stream().filter(c -> c.getType().equals("Equivalence")).toList();
		
		if(relevantConstraints.isEmpty()) {
			root.addChildren(uncoveredFeatures);
			for(FMFeature feature: uncoveredFeatures) {
				feature.setParent(root);
			}
		}
		
		featureLoop:
		for(FMFeature feature: uncoveredFeatures) {
			for(SimpleConstraint equivalence: relevantEquivalences) {
				if(equivalence.getFeature2().getName().equals(feature.getName())) {
					FMFeature other = features.stream().filter(f -> f.getName().equals(equivalence.getFeature1().getName())).findFirst().get();
					feature.setMandatory(true);
					feature.setParent(other);
					other.addChild(feature);
					coveredConstraints.add(equivalence);
					continue featureLoop;
				}
			}
			
			List<SimpleConstraint> relevantMutex = relevantConstraints.stream().filter(c -> c.getType().equals("Mutual Exclusion"))
					.filter(c -> c.getFeature1().getName().equals(feature.getName()) || c.getFeature2().getName().equals(feature.getName())).toList();		
			for(SimpleConstraint mutex: relevantMutex) {
				FMFeature other;
				if(mutex.getFeature1().getName().equals(feature.getName())) {
					other = features.stream().filter(f -> f.getName().equals(mutex.getFeature2().getName())).findFirst().get();
				} else {
					other = features.stream().filter(f -> f.getName().equals(mutex.getFeature1().getName())).findFirst().get();
				}

				if(feature.getParent() == null && other.getParent() == null) {
					if(other.getParent() == null) {
						altCount++;
						FMFeature altParent = new FMFeature("ALT" + altCount, root);
						altParent.setAlternativeParent(true);
						root.addChild(altParent);
						features.add(altParent);
						altParent.addChild(feature);
						feature.setParent(altParent);
						altParent.addChild(other);
						other.setParent(altParent);
						coveredConstraints.add(mutex);
						continue featureLoop;
					}
				}
				
			}
			
			List<SimpleConstraint> relevantImplications = relevantConstraints.stream().filter(c -> c.getType().equals("Implication"))
					.filter(c -> c.getFeature1().getName().equals(feature.getName()) || c.getFeature2().getName().equals(feature.getName())).toList();
			
			for(SimpleConstraint implication: relevantImplications) {
				if(implication.getFeature1().getName().equals(feature.getName())) {
					FMFeature other = features.stream().filter(f -> f.getName().equals(implication.getFeature2().getName())).findFirst().get();
					
					if(feature.getParent() == null && !other.isAlternativeParent() && !other.isOrParent()) {
						other.addChild(feature);
						feature.setParent(other);
						coveredConstraints.add(implication);
						continue featureLoop;
					}
				}
			}
		}
	}

	/*
	 * Building all the feature groups given in the groups list and establishing their relations
	 */
	private void buildGroups(Feature base, List<Group> groups) {
		
		int orCount = 0;
		
		List<Group> baseGroups = groups.stream().filter(g -> g.getParent().equals(base)).toList();
		
		 Map<String, FMFeature> fmFeatureMap = features.stream()
	                .collect(Collectors.toMap(FMFeature::getName, f -> f));
		
		List<FMFeature> parentCandidates = new ArrayList<>();
		List<FMFeature> groupChildren = new ArrayList<>();
		for(Group group: baseGroups) {
			
			switch (group.getType()) {
				case "Or Group":
					orCount++;
					FMFeature orParent = new FMFeature("OR" + orCount, root);
					root.addChild(orParent);
					features.add(orParent);
					
					orParent.setOrParent(true);
					groupChildren = group.getFeatures().stream().map(f -> fmFeatureMap.get(f.getName())).toList();
					groupChildren.stream().forEach(f -> f.setParent(orParent));
					orParent.setChildren(groupChildren);
					break;
				case "Alternative":
					altCount++;
					FMFeature altParent = new FMFeature("ALT" + altCount, root);
					root.addChild(altParent);
					features.add(altParent);
					
					altParent.setAlternativeParent(true);
					groupChildren = group.getFeatures().stream().map(f -> fmFeatureMap.get(f.getName())).toList();
					groupChildren.stream().forEach(f -> f.setParent(altParent));
					altParent.setChildren(groupChildren);
					break;
				default:
					break;
			}
			
			parentCandidates.addAll(groupChildren);
		}
		
		groups.removeAll(baseGroups);
		
		groupLoop:
		for(Group group: groups) {
			boolean mapped = false;
			for(FMFeature feature: parentCandidates) {
				if(group.getParent().getName().equals(feature.getName())) {
					switch (group.getType()) {
						case "Or Group":
							orCount++;
							feature.setOrParent(true);
							break;
						case "Alternative":
							altCount++;
							feature.setAlternativeParent(true);
							break;
						default:
							break;
					}
					groupChildren = group.getFeatures().stream().map(f -> fmFeatureMap.get(f.getName())).toList();
					groupChildren.stream().forEach(f -> f.setParent(feature));
					feature.addChildren(groupChildren);
					continue groupLoop;
				}
			}
			if(!mapped) {
				FMFeature parent = features.stream().filter(f -> group.getParent().getName().equals(f.getName())).findAny().orElse(null);
				
				if(parent != null) {
					switch (group.getType()) {
					case "Or Group":
						orCount++;
						parent.setOrParent(true);
						break;
					case "Alternative":
						altCount++;
						parent.setAlternativeParent(true);
						break;
					default:
						break;
					}
					groupChildren = group.getFeatures().stream().map(f -> fmFeatureMap.get(f.getName())).toList();
					groupChildren.stream().forEach(f -> f.setParent(parent));
					parent.addChildren(groupChildren);
				}
			}
		}
		
	}
		

	/*
	 * Generating all features of the variability model that are mandatory, i.e. present in every possible configuration
	 */
	private void generateMandatoryFeatures(Feature base, List<SimpleConstraint> baseEquivalences) {
		
		for(SimpleConstraint constraint: baseEquivalences) {
			Feature other = constraint.getFeature1().equals(base) ? constraint.getFeature2():constraint.getFeature1();
			FMFeature feature = features.stream().filter(f -> f.getName().equals(other.getName())).findFirst().get();
			
			feature.setParent(root);
			feature.setMandatory(true);
			root.addChild(feature);
		}
	}

	private JSONConstraints readInformationfromFile() {
		Gson gson = new Gson();
		
		try(FileReader reader = new FileReader(source)) {
			JSONConstraints constraints = gson.fromJson(reader, JSONConstraints.class);
			
			return constraints;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
