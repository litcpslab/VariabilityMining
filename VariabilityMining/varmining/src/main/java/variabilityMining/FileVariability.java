/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *	Alexander Stummer - initial API and implementation
********************************************************************************/

package variabilityMining;

import java.util.Objects;

import varflixModel.IVariability;

public class FileVariability implements IVariability{

	private String path;
	
	public FileVariability(String path) {
		this.path = path;
	}
	
	@Override
	public String getLocation() {	
		return path;
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileVariability other = (FileVariability) obj;
		return Objects.equals(path, other.path);
	}

	@Override
	public String getElementName() {
		return path;
	}

	//TODO Add an id
	@Override
	public int getId() {
		return 0;
	}
	
}
