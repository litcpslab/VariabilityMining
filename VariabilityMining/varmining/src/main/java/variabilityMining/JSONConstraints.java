/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2025 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Alexander Stummer - Initial Implementation
********************************************************************************/

package variabilityMining;

import java.util.List;

import constraints.Group;
import constraints.SimpleConstraint;

public class JSONConstraints {

	private List<Feature> features;
	
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
	
	public List<Feature> getFeatures() {
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
	
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
