package varflixModel.IEC61499;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import varflixModel.IVariability;
import varflixModel.IVariabilityGroup;
import varflixModel.IVariant;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class JSON1499VariabilityGroup implements IVariabilityGroup<IEC61499Variant, IEC61499Variability> {
	
	private int id;
	
	private String label;
	
	private List<IEC61499Variability> elements;
	
	private Set<IEC61499Variant> occurrences;
	
	private String attributeName;
	
	public JSON1499VariabilityGroup(int id, String label, List<IEC61499Variability> elements) {
		this.id = id;
		this.label = label;
		this.elements = elements;
		this.occurrences = new HashSet<>();
	}
	
	public JSON1499VariabilityGroup() {
		
	}
	
	public List<IEC61499Variability> getElements() {
		return elements;
	}
	
	public void setGroups(List<IEC61499Variability> elements) {
		this.elements = elements;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public Set<IEC61499Variant> getOccurrences() {
		return occurrences;
	}
	
	@Override
	public void setOccurrences(Set<IEC61499Variant> occurrences) {
		this.occurrences = occurrences;
	}
	
	public void addOccurrences(List<IEC61499Variant> variants) {
		occurrences.addAll(variants);
	}
	

	public void addOccurrence(IEC61499Variant variant) {
		occurrences.add(variant);
	}
	
	@Override
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	@Override
	public String getAttributeName() {
		return attributeName;
	}
	
	public int getId() {
		return id;
	}
	

}
