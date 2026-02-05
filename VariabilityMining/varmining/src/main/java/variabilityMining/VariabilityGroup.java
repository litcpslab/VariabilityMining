package variabilityMining;

import java.util.List;
import java.util.Set;

import varflixModel.IVariability;
import varflixModel.IVariabilityGroup;

/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public class VariabilityGroup implements IVariabilityGroup<Variant, IVariability> {

	
	private Set<Variant> occurrences;
	
	private List<IVariability> elements;
	
	private String attributeName;
	
	public VariabilityGroup(String attribute, Set<Variant> occurrences, List<IVariability> elements) {
		this.attributeName = attribute;
		this.occurrences = occurrences;
		this.elements = elements;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public List<IVariability> getElements() {
		return elements;
	}
	
	public Set<Variant> getOccurrences() {
		return occurrences;
	}

	@Override
	public void setOccurrences(Set<Variant> variants) {
		this.occurrences = variants;		
	}

	@Override
	public void setAttributeName(String name) {
		this.attributeName = name;		
	}
	
}
