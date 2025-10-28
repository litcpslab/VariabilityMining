package variabilityMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import varflixModel.IVariabilityGroup;
import varflixModel.IVariant;
import varflixModel.IEC61499.JSON1499VariabilityGroup;

/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public interface IVariabilityExtractor {


	default ProductComparisonMatrix buildPCM(List<? extends IVariant> variants, List<? extends IVariabilityGroup> groups) {
				
		ProductComparisonMatrix pcm = new ProductComparisonMatrix(variants, groups);
		
		for(IVariabilityGroup group : groups) {
			pcm.setElementOccurrences(group);
		}
		
		File pcmFile = new File("pcm.csv");
		try (FileWriter writer = new FileWriter(pcmFile.getAbsolutePath())) {
            writer.write(pcm.toCSV());     
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return pcm;
	}
	
	List<? extends IVariabilityGroup> performAutomaticMining(String variantPath, String inputPath);
	
	List<? extends IVariant> getVariants();
	
}
