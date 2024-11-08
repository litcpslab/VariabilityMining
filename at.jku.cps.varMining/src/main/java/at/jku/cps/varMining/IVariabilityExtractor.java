package at.jku.cps.varMining;
/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*Contributors:
*Alexander Stummer - initial API and implementation*
*/
public interface IVariabilityExtractor {

	abstract ProductComparisonMatrix mineVariabilities(String variantPath);
	
}
