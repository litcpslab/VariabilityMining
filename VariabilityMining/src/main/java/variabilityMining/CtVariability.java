package variabilityMining;

import java.util.Objects;
import spoon.reflect.declaration.CtElement;
/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public class CtVariability implements IVariability {
	
	private CtElement variability;
	
	
	public CtVariability(CtElement element) {
		this.variability = element;
	}

	@Override
	public String getLocation() {
		return variability.getPosition().toString();
	}
	
	public CtElement getVariability() {
		return variability;
	}

	@Override
	public int hashCode() {
		return Objects.hash(variability);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CtVariability other = (CtVariability) obj;
		return variability.equals(other.variability);
	}
	
	

}
