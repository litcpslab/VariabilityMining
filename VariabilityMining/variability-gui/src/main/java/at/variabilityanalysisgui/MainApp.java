/**
 * Modified from Variability Analyser GUI
 * Original license: MIT License (c) 2025 Michael Schmidhammer
 */

package at.variabilityanalysisgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

import at.variabilityanalysisgui.controller.SceneManager;


public class MainApp extends Application {

	private Stage primaryStage;
	
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlLocation = getClass().getResource("/MainViewExtended.fxml");
        if (fxmlLocation == null) {
            System.err.println("Cannot find FXML file.");
            return;
        }
        Scene scene = new Scene(SceneManager.getExtractionScene(), 900, 700);
        
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Variability Analyser");
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void run(String[] args) {
    	launch(args);
    }

}