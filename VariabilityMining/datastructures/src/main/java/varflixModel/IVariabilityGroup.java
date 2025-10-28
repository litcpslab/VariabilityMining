package varflixModel;

import java.util.List;
import java.util.Set;

/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public interface IVariabilityGroup {

	abstract Set<? extends IVariant> getOccurrences();
	
	abstract List<? extends IVariability> getElements();
	
	abstract String getAttributeName();
	
}
