/***
 * 
    This Source Code Form is subject to the terms of the Mozilla
    Public License, v. 2.0. If a copy of the MPL was not distributed
    with this file, You can obtain one at
    https://mozilla.org/MPL/2.0/.*
    Contributors:
    Michael Schmidhammer
    
    Modifications: 
    Copyright (c) 2025 Johannes Kepler University Linz
  	LIT Cyber-Physical Systems Lab
 	Contributors:
 	Alexander Stummer - Changed scene and loading process
**/

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