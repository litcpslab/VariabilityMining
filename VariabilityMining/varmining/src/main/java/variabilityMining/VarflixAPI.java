package variabilityMining;

import java.io.File;
import java.util.List;
import java.util.Set;

import constraints.Constraint;
import guiModel.Group;
import iec61499Mining.IEC61499VariabilityExtractor;
import mappers.DataMapper1499;
import varflixModel.IVariabilityGroup;
import varflixModel.IEC61499.IEC61499Variant;
import varflixModel.IEC61499.JSON1499VariabilityGroup;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class VarflixAPI {
	
	private IVariabilityExtractor extractor;
	
	private DataMapper1499 mapper;
	
	private FormalConceptAnalysis analysis;
	
	private VariabilityModelGenerator generator = new VariabilityModelGenerator();
	
	public List<Group> computeInitialGroups(){
		
		extractor = new IEC61499VariabilityExtractor();
		
		List<JSON1499VariabilityGroup> initialGroups = (List<JSON1499VariabilityGroup>) extractor.performAutomaticMining("<Provide a path to the variant file here>", "<Provide a path to the IEC61499 diff result here>");
		
		mapper = new DataMapper1499();
		
		return mapper.map1499VariabilityGroup(initialGroups);
	}
	
	public void computePCM(List<Group> editedGroups) {
		
		List<JSON1499VariabilityGroup> updatedGroups = mapper.mapGUIGroupTo1499Group(editedGroups, (List<IEC61499Variant>)extractor.getVariants());	
		
		extractor.buildPCM(extractor.getVariants(), updatedGroups);
				
	}
	
	public Set<Constraint> performFCA(){
		analysis = new FormalConceptAnalysis(new File("pcm.csv"));
		
		analysis.buildConceptLattice();
		
		return analysis.getConstraints();
	}
	
	public List<Feature> getFeatures() {
		return analysis.getFeatures();
	}
	
	public Feature getBaseFeature() {
		return analysis.getBaseFeature();
	}
	
	public void generateModel(Feature base, List<Feature> features, List<Constraint> constraints) {
		generator.generateVariabilityModel(base, features, constraints);
	}

}
