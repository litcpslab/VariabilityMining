package iec61499Mining;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import variabilityMining.IVariability;
import variabilityMining.IVariabilityGroup;
import variabilityMining.IVariant;
/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class JSON1499VariabilityGroup implements IVariabilityGroup {
	
	private String label;
	
	private List<IEC61499Variability> elements;
	
	private Set<IEC61499Variant> occurrences;
	
	private String attributeName;
	
	public JSON1499VariabilityGroup(String label, List<IEC61499Variability> elements) {
		this.label = label;
		this.elements = elements;
		this.occurrences = new HashSet<>();
	}
	
	public List<? extends IVariability> getElements() {
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
	public Set<? extends IVariant> getOccurrences() {
		return occurrences;
	}
	
	public void addOccurrences(List<IEC61499Variant> variants) {
		occurrences.addAll(variants);
	}
	

	public void addOccurrence(IEC61499Variant variant) {
		occurrences.add(variant);
	}
	
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	@Override
	public String getAttributeName() {
		return attributeName;
	}
	

}
