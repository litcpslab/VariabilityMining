package constraints;

import java.util.List;

import variabilityMining.Feature;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class Group implements Constraint {

	protected List<Feature> features;
	
	protected Feature parentFeature;
	
	protected String type;
	
	public Group(List<Feature> features, Feature parentFeature, String type) {
		this.features = features;
		this.parentFeature = parentFeature;
		this.type = type;
	}
	
	public void addFeature(Feature feature) {
		features.add(feature);
	}
	
	public List<Feature> getFeatures() {
		return features;
	}

	public Feature getParent() {
		return parentFeature;
	}
	
	public String getType() {
		return type;
	}
	
}
