package variabilityMining;

import java.util.List;

import constraints.Group;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class JSONConstraints {

	private List<FMFeature> features;
	
	private Feature base;
	
	private List<Group> groups;
	
	private List<SimpleConstraint> constraints;
	
	public JSONConstraints() {
		
	}
	
	public Feature getBase() {
		return base;
	}
	
	public List<SimpleConstraint> getConstraints() {
		return constraints;
	}
	
	public List<FMFeature> getFeatures() {
		return features;
	}
	
	public List<Group> getGroups() {
		return groups;
	}
	
	public void setBase(Feature base) {
		this.base = base;
	}
	
	public void setConstraints(List<SimpleConstraint> constraints) {
		this.constraints = constraints;
	}
	
	public void setFeatures(List<FMFeature> features) {
		this.features = features;
	}
	
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
