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
import constraints.Constraint;
import constraints.Group;
import constraints.SimpleConstraint;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class VariabilityModelGenerator {
	
	private Feature root;
	
	private List<Feature> features;
	
	private List<SimpleConstraint> coveredConstraints;
	
	private int altCount = 0;

	public VariabilityModelGenerator() {
		this.coveredConstraints = new ArrayList<>();
	}
	
	
	/*
	 * Method to generate a variability model given the base/root feature, a list of all features and a list of all the constraints.
	 * The result is written to a uvl file
	 */
	public void generateVariabilityModel(Feature base, List<Feature> features, List<Constraint> constraints) {
		
		this.features = features;
		
		features.forEach(feature -> {
			feature.setChildren(new ArrayList<>());
			feature.setParent(null);
		});
		
		root = base == null? new Feature("Base"): base;
		root.setMandatory(true);
			
		this.coveredConstraints = new ArrayList<>();
		
		List<SimpleConstraint> baseEquivalences = constraints.stream()
				.filter(c -> c.getType().equals("Equivalence")).map(c -> (SimpleConstraint) c).filter(c -> (c.getFeature1().equals(base) || c.getFeature2().equals(base))).toList();
			
		generateMandatoryFeatures(base, baseEquivalences);
			
		coveredConstraints.addAll(baseEquivalences);
			
		buildGroups(base, new ArrayList<>(constraints.stream().filter(c -> c instanceof Group).map(c -> (Group) c).toList()));
			
		List<Feature> uncoveredFeatures = features.stream().filter(f -> f.getParent() == null).toList();
			
		List<SimpleConstraint> relevantConstraints = constraints.stream().filter(c -> c instanceof SimpleConstraint).map(c -> (SimpleConstraint) c)
				.filter(f -> uncoveredFeatures.contains(f.getFeature1()) || uncoveredFeatures.contains(f.getFeature2())).toList();
			
		buildOtherRelations(uncoveredFeatures, relevantConstraints);
			
		List<SimpleConstraint> ctcs = constraints.stream().filter(c -> !coveredConstraints.contains(c) && c instanceof SimpleConstraint).map(c -> (SimpleConstraint) c).toList();
			
		UVLGenerator.createUVLModel(root, ctcs);		
	}



	/*
	 * Check the remaining constraints and determine which are CTCs and which can directly be integrated in the variability model
	 */
	private void buildOtherRelations(List<Feature> uncoveredFeatures, List<SimpleConstraint> relevantConstraints) {
		
		List<SimpleConstraint> relevantEquivalences = relevantConstraints.stream().filter(c -> c.getType().equals("Equivalence")).toList();
		
		if(relevantConstraints.isEmpty()) {
			root.addChildren(uncoveredFeatures);
			for(Feature feature: uncoveredFeatures) {
				feature.setParent(root);
			}
		}
		
		featureLoop:
		for(Feature feature: uncoveredFeatures) {
			for(SimpleConstraint equivalence: relevantEquivalences) {
				if(equivalence.getFeature2().equals(feature)) {
					Feature other = equivalence.getFeature1();
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
				Feature other;
				if(mutex.getFeature1().getName().equals(feature.getName())) {
					other = features.stream().filter(f -> f.getName().equals(mutex.getFeature2().getName())).findFirst().get();
				} else {
					other = features.stream().filter(f -> f.getName().equals(mutex.getFeature1().getName())).findFirst().get();
				}

				if(feature.getParent() == null && other.getParent() == null) {
					if(other.getParent() == null) {
						altCount++;
						Feature altParent = new Feature("ALT" + altCount, root);
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
					Feature other = features.stream().filter(f -> f.getName().equals(implication.getFeature2().getName())).findFirst().get();
					
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
		
		 Map<String, Feature> fmFeatureMap = features.stream()
	                .collect(Collectors.toMap(Feature::getName, f -> f));
		
		List<Feature> parentCandidates = new ArrayList<>();
		List<Feature> groupChildren = new ArrayList<>();
		for(Group group: baseGroups) {
			
			switch (group.getType()) {
				case "Or Group":
					orCount++;
					String parentName = "OR" + orCount;
					Feature orParent = features.stream().filter(f -> f.getName().equals(parentName))
							.findFirst().orElse(new Feature(parentName, root));
					if(!features.contains(orParent)) {
						features.add(orParent);
					} else {
						orParent.setParent(root);
					}
					
					root.addChild(orParent);
					
					orParent.setOrParent(true);
					groupChildren = group.getFeatures();//.stream().map(f -> fmFeatureMap.get(f.getName())).toList();
					groupChildren.stream().forEach(f -> f.setParent(orParent));
					orParent.setChildren(groupChildren);
					break;
				case "Alternative":
					altCount++;
					parentName = "ALT" + altCount;
					Feature altParent = features.stream().filter(f -> f.getName().equals(parentName))
							.findFirst().orElse(new Feature(parentName, root));
					if(!features.contains(altParent)) {
						features.add(altParent);
					} else {
						altParent.setParent(root);
					}
					root.addChild(altParent);
					
					altParent.setAlternativeParent(true);
					groupChildren = group.getFeatures();//.stream().map(f -> fmFeatureMap.get(f.getName())).toList();
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
			for(Feature feature: parentCandidates) {
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
				Feature parent = features.stream().filter(f -> group.getParent().getName().equals(f.getName())).findAny().orElse(null);
				
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
			Feature feature = features.stream().filter(f -> f.getName().equals(other.getName())).findFirst().get();
			
			feature.setParent(root);
			feature.setMandatory(true);
			root.addChild(feature);
		}
	}

	
	
}
