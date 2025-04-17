package variabilityMining;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class SimpleConstraint {

	private Feature feature1;
	
	private Feature feature2;
	
	private String type;
	
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
	public String toString() {
		switch (type) {
			case "Equivalence":
				return feature1 + " <=> " + feature2;
			case "Implication":
				return feature1 + " => " + feature2;
			case "Mutual Exclusion":
				return feature1 + " => !" + feature2;
		}
		return super.toString();
	}
}
