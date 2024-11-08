package variabilityMining;
/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*Contributors:
*Alexander Stummer - initial API and implementation*
*/
public class DiffAndFilter {
	
	
	public static void main(String[] args) {
		 
		String directoryPath = "C:\\Users\\AK122272\\Desktop\\example_Notepad\\variants\\variants";
		
		IVariabilityExtractor extractor = new JavaVariabilityExtractor();

		ProductComparisonMatrix pcm = extractor.mineVariabilities(directoryPath);
		
		System.out.println(pcm.toCSV());
	}

	
}
