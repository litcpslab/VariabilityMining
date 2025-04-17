package iec61499Mining;

import java.io.File;

import variabilityMining.FCAAnalysis;
import variabilityMining.VariabilityModelGenerator;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class IECMiningMain {

	public static void main(String[] args) {
		
		IEC61499VariabilityExtractor extractor = new IEC61499VariabilityExtractor();
		
		extractor.mineVariabilities("");
		
		FCAAnalysis analysis = new FCAAnalysis(new File("pcm.csv"));
		
		analysis.buildConceptLattice();
		
		VariabilityModelGenerator generator = new VariabilityModelGenerator("constraints.json");
		
		generator.generateVariabilityModel();
		
	}
	
}
