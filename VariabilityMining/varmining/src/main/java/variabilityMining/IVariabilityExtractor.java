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

package variabilityMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import varflixModel.IVariability;
import varflixModel.IVariabilityGroup;
import varflixModel.IVariant;


public interface IVariabilityExtractor<V extends IVariant, E extends IVariability> {


	default ProductComparisonMatrix buildPCM(List<V> variants, List<IVariabilityGroup<V, E>> groups) {
				
		ProductComparisonMatrix pcm = new ProductComparisonMatrix(variants, groups);
		
		for(IVariabilityGroup<V, E> group : groups) {
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
	
	List<IVariabilityGroup<V, E>> performAutomaticMining(String variantPath, String inputPath);
	
	List<V> getVariants();

	List<E> getElements();
	
}
