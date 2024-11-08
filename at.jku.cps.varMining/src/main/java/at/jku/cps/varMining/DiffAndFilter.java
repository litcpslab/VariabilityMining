package at.jku.cps.varMining;
/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*Contributors:
*Alexander Stummer - initial API and implementation*
*/
public class DiffAndFilter {
	
	
	public static void main(String[] args) {
		 
		String directoryPath = "path to the directory containing the variants here";
		
		IVariabilityExtractor extractor = new JavaVariabilityExtractor();

		ProductComparisonMatrix pcm = extractor.mineVariabilities(directoryPath);
		
		System.out.println(pcm.toCSV());
	}

	
}