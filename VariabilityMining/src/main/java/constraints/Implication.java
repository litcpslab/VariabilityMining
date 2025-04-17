package constraints;

import variabilityMining.Feature;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class Implication implements Constraint {

	private Feature feature1;
	
	private Feature feature2;
	
	private String type;
	
	public Implication(Feature feature1, Feature feature2) {
		this.feature1 = feature1;
		this.feature2 = feature2;
		this.type = "Implication";
	}

	public Feature getFeature1() {
		return feature1;
	}
	
	public Feature getFeature2() {
		return feature2;
	}
	
	@Override
	public String toString() {
		return feature1 + " -> " + feature2;
	}
	
}
