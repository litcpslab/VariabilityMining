package variabilityMining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import varflixModel.IVariant;

/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public class Variant implements IVariant{
	
	private String path;
	
	private String name;
	
	private Launcher launcher;
	
	private CtModel model;
	
	private List<CtType<?>> uniqueClasses = new ArrayList<>();

	private Path directory;
	
	private Set<Path> files;
	
	private Set<Path> variablePaths = new HashSet<>();
	
	public Variant(String name, String path) {
		this.path = path;
		this.name = name;
		
	}
	
	public Variant(String name, String path, Path directory) {
		this.path = path;
		this.name = name;
		this.directory = directory;
		try {
			files = Files.walk(directory).filter(file -> !file.equals(directory) &&
						Files.isDirectory(file) || (Files.isRegularFile(file) && file.toString().endsWith(".java"))).map(filePath -> directory.relativize(filePath))
					.collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void buildModel() {
		launcher = new Launcher();
		Set<Path> relevantPaths = new HashSet<>();
		relevantPaths.addAll(files);
		relevantPaths.removeAll(variablePaths);
		if(relevantPaths.isEmpty()) {
			relevantPaths = files;
		}
		for(Path relevantPath : relevantPaths) {
			launcher.addInputResource(path + "\\" + relevantPath.toString());
		}
		
		launcher.getEnvironment().setCommentEnabled(false);

		System.out.println("Building model: " + name);
		launcher.buildModel();
		this.model = launcher.getModel();

		model.processWith(new RelevanceProcessor());

	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public Path getDirectories() {
		return directory;
	}
	
	public Set<Path> getFiles() {
		return files;
	}
	
	public List<CtType<?>> getUniqueClasses() {
		return uniqueClasses;
	}
	
	public void setUniqueClasses(List<CtType<?>> uniqueClasses) {
		this.uniqueClasses = uniqueClasses;
	}
	
	public CtModel getModel() {
		return model;
	}
	
	public Set<CtType<?>> getClassDifferences(List<CtType<?>> compare){
		Set<CtType<?>> temp = new HashSet<>();
		
		temp.addAll(compare);
		
		temp.removeAll(uniqueClasses);
		
		return temp;
	}
	
	public Set<Path> getVariablePaths() {
		return variablePaths;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void compareFiles(Set<Path> other) {
		variablePaths.addAll(files.stream().filter(fileName -> !other.contains(fileName)).collect(Collectors.toSet()));
		variablePaths = variablePaths.stream().filter(pathElement -> {
			Path parent = pathElement.getParent();
			return !variablePaths.contains(parent);
		}).collect(Collectors.toSet());
	}

}
