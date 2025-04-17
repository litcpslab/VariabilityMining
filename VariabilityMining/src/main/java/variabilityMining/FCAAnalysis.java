package variabilityMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import constraints.AlternativeGroup;
import constraints.Constraint;
import constraints.Equivalence;
import constraints.Group;
import constraints.Implication;
import constraints.MutualExclusion;
import constraints.OrRelation;
import fr.lirmm.fca4j.algo.AOC_poset_Hermes;
import fr.lirmm.fca4j.algo.Lattice_AddExtent;
import fr.lirmm.fca4j.cli.io.MyCSVReader;
import fr.lirmm.fca4j.core.ConceptOrder;
import fr.lirmm.fca4j.core.IBinaryContext;
import fr.lirmm.fca4j.iset.ISet;
import fr.lirmm.fca4j.iset.std.ArrayListSetFactory;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class FCAAnalysis {

	private File source;
	
	private Map<Integer, ISet> attributeConcepts = new HashMap<>();
	
	private IBinaryContext context;
	
	private ConceptOrder order;
	
	private ConceptOrder fullOrder;
	
	private Set<Constraint> constraints;
	
	private List<Feature> features;
	
	Map<Feature, Set<Feature>> implicationMap = new HashMap<>();
	
	private Feature base;
	
	public FCAAnalysis(File file) {
		this.source = file;
		this.constraints = new HashSet<>();
		this.features = new ArrayList<>();
	}
	
	public File getSource() {
		return source;
	}
	
	public void buildConceptLattice() {

		try {
			context = MyCSVReader.read(source, ';', new ArrayListSetFactory());
			
			for(int i = 0; i < context.getAttributeCount(); i++) {
				features.add(new Feature(context.getAttributeName(i)));
			}
			
			Lattice_AddExtent lattice = new Lattice_AddExtent(context);
			lattice.run();
			
			fullOrder = lattice.getResult();
						
			AOC_poset_Hermes hermes = new AOC_poset_Hermes(context);
			hermes.run();

			order = hermes.getResult();
			
			for(Integer i : fullOrder.getConcepts()) {
				
				ISet curr = fullOrder.getConceptReducedIntent(i);
					
				if(!curr.isEmpty()) {
					attributeConcepts.put(i, curr);
				}
			}
			
			base = features.stream().filter(f -> f.getName().equals(context.getAttributeName(order.getConceptIntent(order.getTop()).first()))).findAny().orElseGet(() -> null);
			
			findEquivalences();
			
			findImplications();
			
			buildImplicationMap();
			
			findMutualExclusions();
			
			List<MutualExclusion> mutEx = constraints.stream().filter(c -> (c instanceof MutualExclusion)).map(c -> (MutualExclusion) c).collect(Collectors.toList());
			
			buildAlternativeGroups(mutEx);
			
			findOrGroups();
		
			filterRedundantConstraints();
			
			writeConstraintsToFile();

			//Potential user feedback point
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void filterRedundantConstraints() {
		List<Implication> implications = constraints.stream().filter(c -> c instanceof Implication).map(c -> (Implication) c).collect(Collectors.toList());
		
		List<AlternativeGroup> alternatives = constraints.stream().filter(c -> (c instanceof AlternativeGroup)).map(c -> (AlternativeGroup) c).collect(Collectors.toList());
		
		for(Implication implication: implications) {
			checkForRedundancy(implication, alternatives);
			
			List<Implication> potentialImplications = new ArrayList<>();
			
			for(Feature key: implicationMap.keySet()) {
				if(implicationMap.get(key).contains(implication.getFeature2())) {
					potentialImplications.add(new Implication(implication.getFeature1(), key));
				}
			}
			
			for(Implication potentialImplication: potentialImplications) {
				checkForRedundancy(potentialImplication, alternatives);
			}
		}
				
		checkForGroupRedundancy(alternatives);
		
		
	}

	private void checkForRedundancy(Implication implication, List<AlternativeGroup> alternatives) {
		Feature left = implication.getFeature1();

		Set<MutualExclusion> relevantMutex = constraints.stream().filter(c -> c instanceof MutualExclusion)
				.map(c -> (MutualExclusion) c).filter(m -> m.getFeature1().equals(left) || m.getFeature2().equals(left)).collect(Collectors.toSet());
		
		Set<MutualExclusion> removedMutex = new HashSet<>();
		
		for(MutualExclusion mutex: relevantMutex) {
			MutualExclusion impliedMutex;
			if(mutex.getFeature1().equals(left)) {
				impliedMutex = new MutualExclusion(implication.getFeature2(), mutex.getFeature2());
			} else {
				impliedMutex = new MutualExclusion(implication.getFeature2(), mutex.getFeature1());
			}
			
			for(AlternativeGroup a: alternatives) {
				if(a.getFeatures().contains(impliedMutex.getFeature1()) && a.getFeatures().contains(impliedMutex.getFeature2())) {
					removedMutex.add(mutex);
				}
			}
			
			Set<MutualExclusion> allMutex = constraints.stream().filter(c -> c instanceof MutualExclusion).map(c -> (MutualExclusion) c).collect(Collectors.toSet());
			
			if(!removedMutex.contains(mutex)) {
				for(MutualExclusion compareMutex: allMutex) {
					if(compareMutex.getFeature1().equals(impliedMutex.getFeature1()) && compareMutex.getFeature2().equals(impliedMutex.getFeature2()) ||
							(compareMutex.getFeature1().equals(impliedMutex.getFeature2()) && compareMutex.getFeature2().equals(impliedMutex.getFeature1()))){
								removedMutex.add(mutex);
								break;
							}
				}
			}
		}
		
		if(!removedMutex.isEmpty()) {
			constraints.removeAll(removedMutex);
		}		
	}

	/*
	 * Checks whether mutex conditions are already implicitly present through the groupings
	 */
	private void checkForGroupRedundancy(List<AlternativeGroup> alternatives) {
		
		List<Group> groups = constraints.stream().filter(c -> c instanceof Group).map(c -> (Group) c).collect(Collectors.toList());
		
		List<MutualExclusion> allMutex = constraints.stream().filter(c -> c instanceof MutualExclusion).map(c -> (MutualExclusion) c).collect(Collectors.toList());
		
		Set<MutualExclusion> checkingMutex = new HashSet<>();
		
		Set<MutualExclusion> removedMutex = new HashSet<>();
		
		for(MutualExclusion mutex: allMutex) {
			for(Group group: groups) {
				Feature left = mutex.getFeature1();
				Feature right = mutex.getFeature2();
				MutualExclusion impliedConstraint = null;
				if(group.getFeatures().contains(left)) {
					impliedConstraint = new MutualExclusion(group.getParent(), right);
				} else if(group.getFeatures().contains(right)) {
					impliedConstraint = new MutualExclusion(left, group.getParent());
				}
				
				if(impliedConstraint != null) {
					for(AlternativeGroup a: alternatives) {
						if(a.getFeatures().contains(impliedConstraint.getFeature1()) && a.getFeatures().contains(impliedConstraint.getFeature2())) {
							removedMutex.add(mutex);
						}
					}
					
					if(!removedMutex.contains(mutex)) {
						checkingMutex.add(impliedConstraint);
					}
				}	
			}
		}
		
		for(MutualExclusion mutex: checkingMutex) {
			if(!removedMutex.contains(mutex)) {
				for(MutualExclusion compareMutex: allMutex) {
					if(compareMutex.getFeature1().equals(mutex.getFeature1()) && compareMutex.getFeature2().equals(mutex.getFeature2()) ||
							(compareMutex.getFeature1().equals(mutex.getFeature2()) && compareMutex.getFeature2().equals(mutex.getFeature1()))){
								removedMutex.add(compareMutex);
								break;
							}
				}
			}
				
		}
		
		
		if(!removedMutex.isEmpty()) {
			constraints.removeAll(removedMutex);
		}
		
		
	}

	private void writeConstraintsToFile() {
					
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
		List<Group> groupList = constraints.stream().filter(c -> c instanceof Group).map(c -> (Group) c).collect(Collectors.toList());
		
		List<Constraint> ctcs = constraints.stream().filter(c -> !(c instanceof Group)).collect(Collectors.toList());

		try(FileWriter writer = new FileWriter("constraints.json")) {
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

	private void buildImplicationMap() {
		
		List<Implication> implications = constraints.stream().filter(c -> c instanceof Implication).map(c -> (Implication) c).collect(Collectors.toList());
		
		for(Implication implication: implications) {
			
			if(!implicationMap.containsKey(implication.getFeature2())) {
				implicationMap.put(implication.getFeature2(), new HashSet<>());
			}
			
			implicationMap.get(implication.getFeature2()).add(implication.getFeature1());
		}
	}
	
	private void findOrGroups() {
		
		Set<Feature> usedFeatures = new HashSet<>();
		
		usedFeatures.addAll(constraints.stream().filter(c -> c instanceof AlternativeGroup).map(c -> (AlternativeGroup) c).map(g -> g.getFeatures()).reduce(new ArrayList<>(), (set1, set2) -> {
		    set1.addAll(set2);
		    return set1;
		}));
				
		for(Feature key: features) {
			boolean added = false;
			if(implicationMap.containsKey(key)) {
				
				List<Feature> matchSet = new ArrayList<>();
				matchSet.addAll(implicationMap.get(key));
				matchSet.removeAll(usedFeatures);
				if(matchSet.size() > 1 && checkCondition(key, matchSet)) {
					OrRelation orRelation = new OrRelation(matchSet, key);
						
					usedFeatures.addAll(matchSet);
					constraints.add(orRelation);
					added = true;

					if(added) {
						Set<Constraint> removedConstraints = constraints.stream().filter(c -> c instanceof Implication).filter(c -> ((Implication) c).getFeature2().equals(key)).collect(Collectors.toSet());
						
						constraints.removeAll(removedConstraints);
					}
					
				}
			}
		}
		
	}

	private boolean checkCondition(Feature key, List<Feature> subFeatures) {
		List<Integer> extents = context.getExtent(context.getAttributeIndex(key.getName())).toList();		
		
		Set<Integer> subExtents = new HashSet<>();
		
		for(Feature feature: subFeatures) {
			subExtents.addAll(context.getExtent(context.getAttributeIndex(feature.getName())).toList());
		}
		
		extents.removeAll(subExtents);
		
		return extents.isEmpty();
	}

	private void findMutualExclusions() {
		
		Set<Integer> concepts = order.getConcepts().stream().filter(i -> !order.getConceptReducedIntent(i).isEmpty()).collect(Collectors.toSet());
		Set<Integer> processedConcepts = new HashSet<>();
		
		for(Integer key: concepts) {
			for(Integer key2: concepts) {
				if(!processedConcepts.contains(key) && !processedConcepts.contains(key2) && key != key2) {
					ISet extent = order.getConceptExtent(key);
					ISet extent2 = order.getConceptExtent(key2);
					ISet intersect = extent.newIntersect(extent2);
					
					if(intersect.isEmpty()) {
						
						Feature currentFeature = getFeatureByName(context.getAttributeName(order.getConceptReducedIntent(key).first()));
						Feature mutexFeature = getFeatureByName(context.getAttributeName(order.getConceptReducedIntent(key2).first()));
						
						
						constraints.add(new MutualExclusion(currentFeature, mutexFeature));

					} 
				}
			}
			processedConcepts.add(key);
			
		}
		
	}

	private Feature getFeatureByName(String featureName) {
		
		for(Feature feature: features) {
			if(feature.getName().equals(featureName)) {
				return feature;
			}
		}
		
		return null;
	}

	private void buildAlternativeGroups(List<MutualExclusion> mutualExclusions) {
		Map<Feature, Set<Feature>> mutexMap = new HashMap<>();
		Set<AlternativeGroup> alternatives = new HashSet<>();
		Set<Feature> usedFeatures = new HashSet<>();
		
		for(MutualExclusion mutex: mutualExclusions) {
			Feature feature1 = mutex.getFeature1();
			Feature feature2 = mutex.getFeature2();
			
			if(mutexMap.get(feature1) == null && mutexMap.get(feature2) == null) {
				mutexMap.put(feature1, new HashSet<>());
				mutexMap.get(feature1).add(feature2);
			} else if(mutexMap.get(feature1) != null && mutexMap.get(feature2) == null) {
				mutexMap.get(feature1).add(feature2);
			} else if(mutexMap.get(feature1) == null && mutexMap.get(feature2) != null) {
				mutexMap.get(feature2).add(feature1);
			} else {
				mutexMap.get(feature1).add(feature2);
			}
		}
		
		Set<Feature> keySet = mutexMap.keySet();
		Set<Feature> duplicateKeys = new HashSet<>();
		
		for(Feature key: keySet) {
			Set<Feature> values = new HashSet<>();
			values.addAll(mutexMap.get(key));
			values.add(key);
			if(mutexMap.containsValue(values)) {
				duplicateKeys.add(key);
			}
		}
		
		duplicateKeys.stream().forEach(k -> mutexMap.remove(k));
		
		for(Feature key: mutexMap.keySet()) {
			Set<Feature> values = mutexMap.get(key);
			boolean isGroup = true;
			
			for(Feature f1: values) {
				for(Feature f2: values) {
					if(f1 != f2 && isGroup) {
						MutualExclusion testCondition1 = new MutualExclusion(f1, f2);
						MutualExclusion testCondition2 = new MutualExclusion(f2, f1);
						
						if(!mutualExclusions.contains(testCondition1) && !mutualExclusions.contains(testCondition2)) {
							isGroup = false;
						}
					}
				}
				
			}
			
			if(isGroup) {
				boolean added = false;
				for(AlternativeGroup group: alternatives) {
					Set<String> groupFeatures = group.getFeatures().stream().map(f -> f.getName()).collect(Collectors.toSet());
					Set<String> valueNames = values.stream().map(f -> f.getName()).collect(Collectors.toSet());
					
					if(groupFeatures.containsAll(valueNames) && valueNames.containsAll(groupFeatures)) {
						group.addFeature(key);
						added = true;
						break;
					} else if(groupFeatures.containsAll(valueNames) && groupFeatures.contains(key.getName())) {
						added = true;
						break;
					}
				} 
				
				if(!added) {
					
					List<Feature> reducedValues = new ArrayList<>();
					
					reducedValues.add(key);
					
					reducedValues.addAll(values);
					
					reducedValues.removeAll(usedFeatures);
					
					if(reducedValues.size() >= 2) {
											
						alternatives.add(new AlternativeGroup(reducedValues, findParent(reducedValues)));
												
						usedFeatures.addAll(reducedValues);
					}

				}
				
			}
		}
		
		constraints.addAll(alternatives);

		for(AlternativeGroup alternative: alternatives) {
			List<Feature> features = alternative.getFeatures();
			List<Implication> implications = constraints.stream().filter(c -> c instanceof Implication).map(c -> (Implication) c).toList();
			implications = implications.stream().filter(i -> features.contains(i.getFeature1()) && i.getFeature2().equals(alternative.getParent())).toList();
			
			List<MutualExclusion> mutExs = mutualExclusions.stream().filter(mutex -> (features.contains(mutex.getFeature1()) && features.contains(mutex.getFeature2()))).collect(Collectors.toList());
			constraints.removeAll(mutExs);
			constraints.removeAll(implications);
		}
		
	}

	private Feature findParent(List<Feature> reducedValues) {
		
		for(Feature key: implicationMap.keySet()) {
			if(implicationMap.get(key).containsAll(reducedValues)) {
				return key;
			}
		}
		
		return base;
	}

	private void findImplications() {
	
		Set<Integer> concepts = order.getConcepts().stream().filter(i -> !order.getConceptReducedIntent(i).isEmpty()).collect(Collectors.toSet());
		
		for(Integer key: concepts) {
			for(Integer key2: concepts) {
				if(key != key2) {
					if(order.getUpperCover(key).contains(key2)) {
						ISet curr = order.getConceptReducedIntent(key);
						ISet cover = order.getConceptReducedIntent(key2);
						
						if(!curr.isEmpty() && !cover.isEmpty()) {
							
							Feature currentFeature = getFeatureByName(context.getAttributeName(curr.first()));
							Feature impliedFeature = getFeatureByName(context.getAttributeName(cover.first()));
								
							constraints.add(new Implication(currentFeature, impliedFeature));
						}
					}
				}
			}
		}
		
	}

	private void findEquivalences() {
		
		for(Integer key: attributeConcepts.keySet()) {
			ISet current = attributeConcepts.get(key);
			
			if(current.cardinality() > 1) {
				Iterator<Integer> currentIterator = current.iterator();
				
				Feature featureName = getFeatureByName(context.getAttributeName(currentIterator.next()));
				
				while(currentIterator.hasNext()) {
					Feature feature2 = getFeatureByName(context.getAttributeName(currentIterator.next()));
					
					constraints.add(new Equivalence(featureName, feature2));
					
				}
			}
		}
	}
}
