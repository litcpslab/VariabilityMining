package at.jku.cps.varMining;

import java.util.Set;

/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*Contributors:
*Alexander Stummer - initial API and implementation*
*/
public class VariabilityGroup implements IVariabilityGroup {

	
	private Set<Variant> occurrences;
	
	private Set<IVariability> elements;
	
	private String attributeName;
	
	public VariabilityGroup(String attribute, Set<Variant> occurrences, Set<IVariability> elements) {
		this.attributeName = attribute;
		this.occurrences = occurrences;
		this.elements = elements;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public Set<IVariability> getElements() {
		return elements;
	}
	
	public Set<Variant> getOccurrences() {
		return occurrences;
	}
	
}
