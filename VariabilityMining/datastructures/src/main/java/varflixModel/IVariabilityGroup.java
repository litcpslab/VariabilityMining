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

package varflixModel;

import java.util.List;
import java.util.Set;

public interface IVariabilityGroup<V extends IVariant, E extends IVariability> {

	abstract Set<V> getOccurrences();
	
	abstract List<E> getElements();
	
	abstract String getAttributeName();
	
	abstract void setOccurrences(Set<V> variants);
	
	abstract void setAttributeName(String name);
	
	abstract void setElements(List<E> elements);
	
}
