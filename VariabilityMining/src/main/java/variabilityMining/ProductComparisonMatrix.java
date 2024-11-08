package variabilityMining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
/*
*Copyright (c) 2024 Johannes Kepler University Linz*
*Contributors:
*Alexander Stummer - initial API and implementation*
*/

public class ProductComparisonMatrix {
	
	private boolean[][] occurrenceMatrix;
	
	private final List<IVariant> variants;
	
	private final List<IVariabilityGroup> variabilities;

	public ProductComparisonMatrix(Set<? extends IVariant> variants, Set<? extends IVariabilityGroup> variabilities) {
		this.variants = new ArrayList<>();
		this.variants.addAll(variants);
		this.variabilities = new ArrayList<>();
		this.variabilities.addAll(variabilities);
		this.occurrenceMatrix = new boolean[variants.size()][variabilities.size()];
	}
	
	public void setElementOccurrences(IVariabilityGroup feature) {
		Set<? extends IVariant> infos = feature.getOccurrences();
		for(IVariant information : infos) { 
			int index = variants.indexOf(information);
			
			if(index != -1) { 
				int position = variabilities.indexOf(feature);
				
				if(position != -1) { 
					occurrenceMatrix[index][position] = true; 
				}
				
			} 
		} 
	}
	
	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Features;");
		for(IVariabilityGroup element : variabilities) {
			sb.append(element.getAttributeName());
			sb.append(";");
		}
		sb.append("\n");
		int index = 0;
        for (boolean[] row : occurrenceMatrix) {
        	sb.append(variants.get(index).getName());
            sb.append(Arrays.toString(row)).append("\n");
            index++;
        }

         
        return sb.toString().replaceAll("[\\[,\\]]", ";").replaceAll("true", "x").replaceAll("false", "");
	}
}
