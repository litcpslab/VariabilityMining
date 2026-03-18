/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2025 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Alexander Stummer - Initial Implementation
********************************************************************************/

package constraints;

import variabilityMining.Feature;

public class Equivalence extends SimpleConstraint {

	public Equivalence(Feature feature1, Feature feature2) {
		super(feature1, feature2, "Equivalence");
	}
	
	@Override
	public String toString() {
		return feature1 + " <=> " + feature2;
	}

}
