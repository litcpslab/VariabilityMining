package constraints;

import java.util.Iterator;
import java.util.List;

import variabilityMining.Feature;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class AlternativeGroup extends Group {
	
	public AlternativeGroup(List<Feature> features, Feature parent) { 
		super(features, parent, "Alternative");
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		Iterator<Feature> featureIterator = features.iterator();
		
		while(featureIterator.hasNext()) {
			Feature current = featureIterator.next();
			builder.append(current);
			
			if(featureIterator.hasNext()) {
				builder.append(" XOR ");
			}
		}
		
		builder.append(" (Parent: " + parentFeature + ")");
		
		return builder.toString();
	}
	

}
