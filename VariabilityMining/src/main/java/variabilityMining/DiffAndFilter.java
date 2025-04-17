package variabilityMining;
/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation*
*/
public class DiffAndFilter {
	
	
	public static void main(String[] args) {
		 
		String directoryPath = "add path to directory containing variants here!";
		
		IVariabilityExtractor extractor = new JavaVariabilityExtractor();

		ProductComparisonMatrix pcm = extractor.mineVariabilities(directoryPath);
		
		System.out.println(pcm.toCSV());
	}

	
}
