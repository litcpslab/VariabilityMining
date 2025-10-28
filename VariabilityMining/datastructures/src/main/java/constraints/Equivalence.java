package constraints;

import variabilityMining.Feature;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class Equivalence extends SimpleConstraint {

	public Equivalence(Feature feature1, Feature feature2) {
		super(feature1, feature2, "Equivalence");
	}
	
	@Override
	public String toString() {
		return feature1 + " <=> " + feature2;
	}

}
