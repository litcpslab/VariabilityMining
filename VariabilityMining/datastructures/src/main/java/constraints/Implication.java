package constraints;

import variabilityMining.Feature;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class Implication extends SimpleConstraint {
	
	public Implication(Feature feature1, Feature feature2) {
		super(feature1, feature2, "Implication");
	}
	
	@Override
	public String toString() {
		return feature1 + " => " + feature2;
	}
	
}
