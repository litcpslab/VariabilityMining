/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Alexander Stummer - Initial Implementation
********************************************************************************/

package at.variabilityanalysisgui.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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
