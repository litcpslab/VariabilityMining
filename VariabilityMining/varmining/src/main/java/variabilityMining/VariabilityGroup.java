/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Alexander Stummer - Initial API and Implementation
********************************************************************************/

package variabilityMining;

import java.util.List;
import java.util.Set;

import varflixModel.IVariability;
import varflixModel.IVariabilityGroup;

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

	@Override
	public void setElements(List<IVariability> elements) {
		this.elements = elements;		
	}
	
}
