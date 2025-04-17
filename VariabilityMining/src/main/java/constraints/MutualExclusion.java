package constraints;

import java.util.Objects;

import variabilityMining.Feature;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class MutualExclusion implements Constraint {

	private Feature feature1;
	
	private Feature feature2;
	
	private String type;
	
	public MutualExclusion(Feature feature1, Feature feature2) {
		this.feature1 = feature1;
		this.feature2 = feature2;
		this.type = "Mutual Exclusion";
	}

	public Feature getFeature1() {
		return feature1;
	}
	
	public Feature getFeature2() {
		return feature2;
	}
	
	@Override
	public String toString() {
		return feature1 + " MUTEX " + feature2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(feature1, feature2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutualExclusion other = (MutualExclusion) obj;
		return Objects.equals(feature1, other.feature1) && Objects.equals(feature2, other.feature2) || 
				Objects.equals(feature2, other.feature1) && Objects.equals(feature1, other.feature2);
	}
	
	
}
