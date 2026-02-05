package variabilityMining;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import constraints.Constraint;
import guiModel.Group;
import iec61499Mining.IEC61499VariabilityExtractor;
import mappers.DataMapper;
import mappers.DataMapper1499;
import varflixModel.IVariabilityGroup;
import varflixModel.IEC61499.IEC61499Variability;
import varflixModel.IEC61499.IEC61499Variant;
import varflixModel.IEC61499.JSON1499VariabilityGroup;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class VarflixAPI {
	
	private IVariabilityExtractor<IEC61499Variant, IEC61499Variability> extractor;
	
	private DataMapper mapper;
	
	private FormalConceptAnalysis analysis;
	
	private VariabilityModelGenerator generator = new VariabilityModelGenerator();
	
	public List<Group> computeInitialGroups(){
		
		extractor = new IEC61499VariabilityExtractor();
		
		List<IVariabilityGroup<IEC61499Variant, IEC61499Variability>> initialGroups = extractor.performAutomaticMining("<Enter path to file with list of variants here>", "<Enter path to file with 1499 diff results here>");
		
		mapper = new DataMapper();
		
		return mapper.mapVariabilityGroup(initialGroups);
	}
	
	public void computePCM(List<Group> editedGroups) {
		
		List<IVariabilityGroup<IEC61499Variant, IEC61499Variability>> updatedGroups = mapper.mapGUIGroupTo1499VariabilityGroup(editedGroups, extractor.getVariants(), extractor.getElements());	
		
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
