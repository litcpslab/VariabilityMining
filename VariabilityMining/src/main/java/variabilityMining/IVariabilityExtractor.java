package variabilityMining;
/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public interface IVariabilityExtractor {

	abstract ProductComparisonMatrix mineVariabilities(String variantPath);
	
}
