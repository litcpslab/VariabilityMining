package constraints;

import java.util.Objects;

import com.google.gson.annotations.Expose;

import variabilityMining.Feature;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class SimpleConstraint implements Constraint {

	@Expose
	protected Feature feature1;
	
	@Expose
	protected Feature feature2;
	
	@Expose
	protected String type;
	
	public SimpleConstraint(Feature feature1, Feature feature2, String type) {
		this.feature1 = feature1;
		this.feature2 = feature2;
		this.type = type;
	}
	
	public Feature getFeature1() {
		return feature1;
	}
	
	public Feature getFeature2() {
		return feature2;
	}
	
	public String getType() {
		return type;
	}
	
	public void setFeature1(Feature feature1) {
		this.feature1 = feature1;
	}
	
	public void setFeature2(Feature feature2) {
		this.feature2 = feature2;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(feature1, feature2, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleConstraint other = (SimpleConstraint) obj;
		return Objects.equals(feature1, other.feature1) && Objects.equals(feature2, other.feature2)
				&& Objects.equals(type, other.type);
	}
	
	

}
