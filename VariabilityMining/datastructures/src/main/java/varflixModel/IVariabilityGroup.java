package varflixModel;

import java.util.List;
import java.util.Set;

/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public interface IVariabilityGroup<V extends IVariant, E extends IVariability> {

	abstract Set<V> getOccurrences();
	
	abstract List<E> getElements();
	
	abstract String getAttributeName();
	
	abstract void setOccurrences(Set<V> variants);
	
	abstract void setAttributeName(String name);
	
}
