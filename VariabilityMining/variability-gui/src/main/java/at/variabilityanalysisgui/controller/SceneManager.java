package at.variabilityanalysisgui.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SceneManager {
	
	private static FXMLLoader extractionLoader;
	
	private static FXMLLoader constraintsLoader;
	
	private static Parent extractionScene;
	
	private static Parent constraintsScene;
	
	static {
		extractionLoader = new FXMLLoader(SceneManager.class.getResource("/MainViewExtended.fxml"));
		constraintsLoader = new FXMLLoader(SceneManager.class.getResource("/ConstraintsView.fxml"));		
		try {
			extractionScene = extractionLoader.load();
			constraintsScene = constraintsLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Parent getConstraintScene() {
		return constraintsScene;
	}

	public static Parent getExtractionScene() {
		return extractionScene;
	}
	
	public static FXMLLoader getConstraintsLoader() {
		return constraintsLoader;
	}
	
	public static FXMLLoader getExtractionLoader() {
		return extractionLoader;
	}
}
