package variabilityMining;

import java.util.Set;
/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*Contributors:
*Alexander Stummer - initial API and implementation*
*/
public interface IVariabilityGroup {

	abstract Set<? extends IVariant> getOccurrences();
	
	abstract Set<? extends Object> getElements();
	
	abstract String getAttributeName();
	
}
