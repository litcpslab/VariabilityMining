package at.variabilityanalysisgui.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import constraints.AlternativeGroup;
import constraints.Constraint;
import constraints.Equivalence;
import constraints.Group;
import constraints.Implication;
import constraints.MutualExclusion;
import constraints.OrRelation;
import constraints.SimpleConstraint;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import variabilityMining.ConstraintFileIO;
import variabilityMining.Feature;
import variabilityMining.JSONConstraints;
import variabilityMining.VarflixAPI;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class ConstraintsViewController {

	private ConstraintInfoController infoController;
	
	//Filter items
    @FXML private TextField searchTextField;
    @FXML private Button filterButton;
    private Set<TreeItem<Constraint>> unfilteredItems; 
    private CheckComboBox<String> typeCheckComboBox;
    private CheckComboBox<Feature> featureCheckComboBox;
    
	
	//GroupInfoView InfoController
    @FXML private ScrollPane infoScrollPane;
    @FXML private HBox detailsNameHBox;
    @FXML private Label groupInfoLabel;
    @FXML private Label parentFeatureLabel;
    @FXML private Text infoText;
    @FXML private ListView<Feature> groupFeatureListView;
    @FXML private TextField detailGroupNameTextField;
    @FXML private Button removeFeatureButton;
    @FXML private Label editLabel;
    @FXML private ComboBox<Feature> featureComboBox;
    @FXML private HBox editButtonBox;
    @FXML private Button infoCloseButton;
    @FXML private TreeView<Constraint> groupTreeView;
    
    //Menu Buttons
    @FXML private Button extractionViewButton;
    @FXML private Button proceedButton;
    @FXML private Button addConstraintButton;
    @FXML private Button generateButton;
    
    private ContextMenu constraintFilterMenu;
    
    private VarflixAPI model;
    private Set<Constraint> constraints; 
    private List<Feature> features;
    private Feature currentBase;
    
    
    private boolean isGroupView = true;
	
    @FXML
    public void initialize() {
    	this.infoController = new ConstraintInfoController(this, infoScrollPane, groupInfoLabel, parentFeatureLabel, infoText, groupFeatureListView, removeFeatureButton, editLabel, featureComboBox, editButtonBox, infoCloseButton);
    	searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
    	    if(newValue != null && !newValue.isEmpty()) {
    	    	filterConstraints(newValue);
    	    } else {
    	    	groupTreeView.getRoot().getChildren().clear();
    			groupTreeView.getRoot().getChildren().addAll(unfilteredItems);
    	    }
    	});
    	unfilteredItems = new HashSet<>();
    	
    	infoScrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
    		if(newScene != null) {
    	        newScene.windowProperty().addListener((obs2, oldWin, newWin) -> {
    	            if (newWin != null) {
    	            	Stage stage = (Stage) infoScrollPane.getScene().getWindow();
    	        		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){

    	    				@Override
    	    				public void handle(WindowEvent event) {
    	    					model.generateModel(currentBase, features, new ArrayList<>(constraints));
    	    				}
    	        			
    	        		});
    	            }
    	        });
    	    }
    	});
    }
    
    /*
	 * Method to handle the exit action
	 */
	@FXML
	private void handleExit() {
		model.generateModel(currentBase, features, new ArrayList<>(constraints));
		Platform.exit();
	}
	
	/*
	 * Method to load the constraints from a file and display items in the GUI 
	 */
	@FXML
    private void handleLoadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Constraints file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialDirectory(new File("./"));
        File selectedFile = fileChooser.showOpenDialog(groupTreeView.getScene().getWindow());

        JSONConstraints jsonConstraints = ConstraintFileIO.readInformationfromFile(selectedFile);
        constraints = new HashSet<>();
        constraints.addAll(jsonConstraints.getConstraints());
        constraints.addAll(jsonConstraints.getGroups());
        features = jsonConstraints.getFeatures();
        currentBase = jsonConstraints.getBase();
        
        featureCheckComboBox.getItems().clear();
        featureCheckComboBox.getItems().addAll(features);
        featureCheckComboBox.getCheckModel().clearChecks();
        
        List<Constraint> currentItems = isGroupView ? constraints.stream().filter(c -> c instanceof Group).toList():constraints.stream().filter(c -> c instanceof SimpleConstraint).toList();
        setUpTreeItems(currentItems);
    }
	
	/*
	 * Method to save the current status of the constraints to a .json file
	 */
	@FXML
    private void handleSaveAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Constraints to file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setInitialFileName("constraints");
        File selectedFile = fileChooser.showSaveDialog(groupTreeView.getScene().getWindow());

        ConstraintFileIO.writeConstraintsToFile(selectedFile, constraints, features, currentBase);
    }
	
	
	/*
	 * Defining the action performed when pressing the add button to add a new feature to a group
	 */
	@FXML
	public void handleAddAction(ActionEvent e) {
		Feature addFeature = featureComboBox.getValue();
		
		Constraint constraint = groupTreeView.getSelectionModel().getSelectedItem().getValue();
		boolean shouldBeAdded = true;
		if(constraint instanceof Group) {
			Group group = (Group) constraint;
			
			List<Group> groups = constraints.stream().filter(c -> c instanceof Group).map(c -> ((Group) c)).toList();
			
			for(Group g: groups) {
				if(g != group && g.getFeatures().contains(addFeature)) {
					ButtonType buttonKeepConstraints = new ButtonType("Keep Constraints");
					Alert warningAlert = new Alert(AlertType.WARNING, "The feature you are trying to add is already part of another group. Should it be moved here? (It will be removed from its current group)", ButtonType.YES, buttonKeepConstraints, ButtonType.NO);
					warningAlert.setHeaderText("Addition Warning");
					
					Optional<ButtonType> result = warningAlert.showAndWait();
				    if(result.isPresent() && result.get() == ButtonType.YES) {
				    	g.removeFeature(addFeature);
				    	
				    } else if(result.isPresent() && result.get() == buttonKeepConstraints) {
				    	if(group instanceof AlternativeGroup) {
				    		 
				    		for(Feature f: g.getFeatures()) {
				    			if(f != addFeature) {
				    				constraints.add(new MutualExclusion(addFeature, f));
				    			}
				    		}
				    	}
				    	 		
				    	constraints.add(new Implication(addFeature, g.getParent()));
				    	g.removeFeature(addFeature);
				    	 
				    } else {
				    	shouldBeAdded = false;
				    }
				     
				}
			}
			if(shouldBeAdded && !group.getFeatures().contains(addFeature) && !group.getParent().equals(addFeature)) {
				group.getFeatures().add(addFeature);
				featureComboBox.getItems().remove(addFeature);
				groupFeatureListView.getItems().add(addFeature);
			}
			
		} 
		
		groupTreeView.refresh();
		featureComboBox.setValue(null);
		
	}
	
	/*
	 * Handling the action performed to remove a group from a feature
	 */
	@FXML
	public void handleRemoveAction(ActionEvent e) {
		infoController.removeGroupFeature();
		groupTreeView.refresh();
	}
	
	/*
	 * Defining the action that is performed when pressing the proceed button (switching scenes and loading the new data)
	 */
	@FXML
	public void handleProceedAction() {
		groupTreeView.setRoot(null);
		
		List<Constraint> simpleConstraints = constraints.stream().filter(c -> !(c instanceof Group)).toList();
		setUpTreeItems(simpleConstraints);
		
		generateButton.setVisible(true);
		
		addConstraintButton.setVisible(true);
		
		addConstraintButton.setOnAction(e -> addConstraint(e));
		
		infoController.hideInfoPane();
		
		proceedButton.setText("Go back");
		
		proceedButton.setOnAction(e -> {
			handleGoBackAction();
		});
		
		isGroupView = false;
		
		typeCheckComboBox.getItems().clear();
		typeCheckComboBox.getItems().addAll("Mutual Exclusion", "Implication", "Equivalence");
	}
	
	/*
	 * Performing the generate model action
	 */
	@FXML
	public void handleGenerateAction() {
		List<Constraint> constraintsList = new ArrayList<>();
		constraintsList.addAll(constraints);
		model.generateModel(currentBase, features, constraintsList);
		Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, "The model was successfully generated in the file model.uvl!", ButtonType.FINISH);
		confirmationAlert.setHeaderText("Generation Confirmation");
		 
		Optional<ButtonType> result = confirmationAlert.showAndWait();
	    if(result.isPresent() && result.get() == ButtonType.FINISH) {
	    	//Platform.exit();
	    }
	}
	
	@FXML
	public void switchToExtractionView(ActionEvent event) {
    	Scene scene = ((Node)event.getSource()).getScene();
    	scene.setRoot((Parent)SceneManager.getExtractionScene());
	}
	
    /*
     * Filter items based on the input string entered in the search field
     */
	private void filterConstraints(String newValue) {
		
		List<TreeItem<Constraint>> filteredItems = unfilteredItems.stream().filter(e -> e.getValue().toString().toLowerCase().contains(newValue.toLowerCase())).toList();
		
		groupTreeView.getRoot().getChildren().clear();
		groupTreeView.getRoot().getChildren().addAll(filteredItems);
	}

	
	/*
	 * Setup and logic for the Add Constraint window
	 */
	private void addConstraint(ActionEvent e) {
		
		Stage popupStage = new Stage();
        popupStage.initOwner((Stage)((Node) e.getSource()).getScene().getWindow()); 
        popupStage.initModality(Modality.NONE); 
        popupStage.setAlwaysOnTop(true); 
        popupStage.setTitle("Add a constraint!");
		
		ComboBox<String> constraintTypeComboBox = new ComboBox<>(FXCollections.observableArrayList("Implication", "Equivalence", "Mutual Exclusion"));
		
		constraintTypeComboBox.setPromptText("Select constraint type");
		
		ComboBox<Feature> leftFeatureComboBox = new ComboBox<>(FXCollections.observableArrayList(features));
		leftFeatureComboBox.setPromptText("Select left feature");
		
		ComboBox<Feature> rightFeatureComboBox = new ComboBox<>(FXCollections.observableArrayList(features));
		rightFeatureComboBox.setPromptText("Select right feature");
		
		Button addButton = new Button("Add");
		
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String type = constraintTypeComboBox.getValue();
				
				Feature left = leftFeatureComboBox.getValue();
				Feature right = rightFeatureComboBox.getValue();
				if(left != right) {
					SimpleConstraint constraint = null;//new SimpleConstraint(left, right, type);
					if(type.equals("Implication")) {
						constraint = new Implication(left, right);
					} else if(type.equals("Mutual Exclusion")) {
						constraint = new MutualExclusion(left, right);
					} else if(type.equals("Equivalence")) {
						constraint = new Equivalence(left, right);
					}
					
					if(constraint != null) {
						if(constraints.add(constraint)) {
							TreeItem<Constraint> addItem = new TreeItem<Constraint>(constraint);
							Button button = new Button("X");
							button.setOnAction(e -> {

								Alert removeAlert = new Alert(AlertType.CONFIRMATION, "Should the constraint " + addItem.getValue() + " be removed?", ButtonType.YES, ButtonType.NO);
								removeAlert.setHeaderText("Removal Confirmation");
								 
								Optional<ButtonType> result = removeAlert.showAndWait();
							    if(result.isPresent() && result.get() == ButtonType.YES) {
							    	groupTreeView.getRoot().getChildren().remove(addItem);
							    	constraints.remove(addItem.getValue());
							    }							
							});
							addItem.setGraphic(button);
							
							groupTreeView.getRoot().getChildren().add(addItem);
							unfilteredItems.add(addItem);
							groupTreeView.refresh();
						}	
					}
					
				} else {
					Alert addError = new Alert(AlertType.ERROR, "The same feature can't be chosen for both sides of the logical constraint!");
					addError.show();
				}
				
				popupStage.close();
			}
		};
		
		addButton.setOnAction(event);
		
		VBox layout = new VBox(10, constraintTypeComboBox, leftFeatureComboBox, rightFeatureComboBox, addButton);

		popupStage.setScene(new Scene(layout, 250, 200));
	    popupStage.show();
	}

	/*
	 * Handling the action to press the go back button
	 */
	private void handleGoBackAction() {
		groupTreeView.setRoot(null);
		
		List<Constraint> groups = constraints.stream().filter(c -> c instanceof Group).toList();
		setUpTreeItems(groups);
		
		proceedButton.setText("Proceed");
		
		proceedButton.setOnAction(e -> {
			handleProceedAction();
		});
		
		addConstraintButton.setVisible(false);
		
		infoController.hideInfoPane();
		
		isGroupView = true;
		
		typeCheckComboBox.getItems().clear();
		typeCheckComboBox.getItems().addAll("Or Group", "Alternative");
	}

	

	/*
	 * Loading all the elements that should be displayed initially in the window and the logic to show the info window
	 */
	private void initializeTreeView(List<Constraint> groups) {
		setUpTreeItems(groups);
		
		groupTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(oldValue != null) {
                oldValue.setValue(oldValue.getValue()); 
            }
            if(newValue != null && newValue.getValue() != null) {
                Constraint constraint = newValue.getValue();
                showInfoPane(constraint, newValue);
            }
        });		
	}
	
	/*
	 * Method to show the Info window
	 */
	private void showInfoPane(Constraint constraint, TreeItem<Constraint> newValue) {
		infoController.showInfoPane(constraint, newValue);
	}
	
	public void setModel(VarflixAPI model) {
		//if(this.model == null) {
		this.model = model;
		initData();
		//}
	}

	/*
	 * Initializing the constraints derivation and setting up the window initially
	 */
	private void initData() {
		constraints = model.performFCA();
		features = model.getFeatures();
		currentBase = model.getBaseFeature();
		model.generateModel(currentBase, features, new ArrayList<>(constraints));
		initializeTreeView(constraints.stream().filter(c -> c instanceof Group).collect(Collectors.toList()));	
		setUpFilterMenu();
		groupTreeView.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if(newScene != null) {
				Scene scene = groupTreeView.getScene();
	        	groupTreeView.styleProperty().bind(Bindings.createStringBinding(() -> String.format("-fx-font-size: %.1fpx;", scene.getWidth()/80), scene.widthProperty()));
			}	
	    });	
	}
	
	/*
	 * Initializing the filter button and the submenu with the different filters
	 */
	private void setUpFilterMenu() {
		constraintFilterMenu = new ContextMenu();
		
		constraintFilterMenu.setMinWidth(300);
		constraintFilterMenu.setMaxWidth(300);
		constraintFilterMenu.setPrefWidth(300);
		    
		featureCheckComboBox = new CheckComboBox<>();
		featureCheckComboBox.getItems().addAll(features);
		featureCheckComboBox.setMinWidth(150);
		featureCheckComboBox.setPrefWidth(150);
		featureCheckComboBox.setMaxWidth(150);
		Label featureLabel = new Label("Features");
		HBox spacer = new HBox();
	    spacer.setMinWidth(21);
	    spacer.setMaxWidth(21);
	    spacer.setPrefWidth(21);
	    
		HBox featureFilterBox = new HBox(featureLabel, spacer, featureCheckComboBox);
		featureCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Feature>) c -> {
			
			ObservableList<Feature> checkedItems = featureCheckComboBox.getCheckModel().getCheckedItems();
			groupTreeView.getRoot().getChildren().clear();
			
			Set<TreeItem<Constraint>> addItems = new HashSet<>();
			
			for(Feature feature: checkedItems) {
				addItems.addAll(findRelevantItems(feature));	 
			}
			
			groupTreeView.getRoot().getChildren().addAll(addItems);
			
			if(featureCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
				groupTreeView.getRoot().getChildren().addAll(unfilteredItems);
			}
		});
		
		
		Label typeLabel = new Label("Type");
		HBox typeSpacer = new HBox();
	    typeSpacer.setMinWidth(40);
	    typeSpacer.setMaxWidth(40);
	    typeSpacer.setPrefWidth(40);
	    
		typeCheckComboBox = new CheckComboBox<>();
		typeCheckComboBox.getItems().addAll("Or Group", "Alternative");
		typeCheckComboBox.setMinWidth(150);
		typeCheckComboBox.setPrefWidth(150);
		typeCheckComboBox.setMaxWidth(150);
		HBox typeFilterBox = new HBox(typeLabel, typeSpacer, typeCheckComboBox);
		
		typeCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
			
			ObservableList<String> checkedItems = typeCheckComboBox.getCheckModel().getCheckedItems();
			groupTreeView.getRoot().getChildren().clear();
			
			Set<TreeItem<Constraint>> addItems = new HashSet<>();
			
			for(String type: checkedItems) {
				addItems.addAll(unfilteredItems.stream().filter(constraint -> constraint.getValue().getType().equals(type)).toList());	 
			}
			
			groupTreeView.getRoot().getChildren().addAll(addItems);
			
			if(typeCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
				groupTreeView.getRoot().getChildren().addAll(unfilteredItems);
			}
		});
		
		
		CustomMenuItem featureMenuItem = new CustomMenuItem(featureFilterBox, false);
        constraintFilterMenu.getItems().add(featureMenuItem);
        
        CustomMenuItem typeMenuItem = new CustomMenuItem(typeFilterBox, false);
        constraintFilterMenu.getItems().add(typeMenuItem);
		
		constraintFilterMenu.hide();
	    filterButton.setOnAction(event -> {
	    	if(constraintFilterMenu.isShowing()) {
	    		constraintFilterMenu.hide();
	        } else {
	        	constraintFilterMenu.show(filterButton, javafx.geometry.Side.BOTTOM, 0, 0);
	        }
	    });
		
	}
	
	/*
	 * Helper method for finding all display items that contain a specific feature
	 */
	private Set<TreeItem<Constraint>> findRelevantItems(Feature feature){
		Set<TreeItem<Constraint>> relevantItems = new HashSet<>();
		
    	if(isGroupView) {
    		for(TreeItem<Constraint> addConstraint: unfilteredItems) {
    			Group group = (Group) addConstraint.getValue();
    			if(group.getParent().equals(feature) || group.getFeatures().contains(feature)) {
    				relevantItems.add(addConstraint);
    			}
    		}
    	} else {
    		for(TreeItem<Constraint> addConstraint: unfilteredItems) {
    			SimpleConstraint sc = (SimpleConstraint) addConstraint.getValue();
    			if(sc.getFeature1().equals(feature) || sc.getFeature2().equals(feature)) {
    				relevantItems.add(addConstraint);
    			}
    		}
    	}
    		
    	return relevantItems;
	}

	/*
	 * Setting up the single items for the view
	 */
	private void setUpTreeItems(List<Constraint> constraints) {
		TreeItem<Constraint> root = new TreeItem<>();
		
		unfilteredItems = new HashSet<>();
		
		for(Constraint constraint: constraints) {
			Button button = new Button("X");
			
			TreeItem<Constraint> constraintItem = new TreeItem<>(constraint);
			button.setOnAction(e -> {

				Alert removeAlert = new Alert(AlertType.CONFIRMATION, "Should the constraint " + constraint + " be removed?", ButtonType.YES, ButtonType.NO);
				removeAlert.setHeaderText("Removal Confirmation");
				
				ButtonType buttonKeepConstraints = new ButtonType("Keep constraints");
				
				if(constraint instanceof Group) {
					removeAlert.getButtonTypes().add(buttonKeepConstraints);
				}
				 
				Optional<ButtonType> result = removeAlert.showAndWait();
			    if(result.isPresent() && result.get() == ButtonType.YES) {
			    	groupTreeView.getRoot().getChildren().remove(constraintItem);
			    	unfilteredItems.remove(constraintItem);
			    	this.constraints.remove(constraint);
			    } else if(result.isPresent() && result.get() == buttonKeepConstraints) {
			    	groupTreeView.getRoot().getChildren().remove(constraintItem);
			    	this.constraints.remove(constraint);
			    	resolveGroupConstraint(constraint);
			    	unfilteredItems.remove(constraintItem);
			    }
				
			});
			constraintItem.setGraphic(button);
			unfilteredItems.add(constraintItem);
			root.getChildren().add(constraintItem);
		}
		
		groupTreeView.setRoot(root);
		groupTreeView.setShowRoot(false);
	}
	
	/*
	 * Logic to keep the constraints when removing (a feature from) a group
	 */
	private void resolveGroupConstraint(Constraint constraint) {
		
		if(constraint instanceof AlternativeGroup) {
			AlternativeGroup alternative = (AlternativeGroup) constraint;
			
			Set<Feature> processed = new HashSet<>();
			
			for(Feature f: alternative.getFeatures()) {
				constraints.add(new Implication(f, alternative.getParent()));
				
				for(Feature f2: processed) {
					constraints.add(new MutualExclusion(f, f2));
				}
				
				processed.add(f);
			}
		} else {
			OrRelation orGroup = (OrRelation) constraint;
			
			for(Feature f: orGroup.getFeatures()) {
				constraints.add(new Implication(f, orGroup.getParent()));
			}
		}
	}
	
	/*
	 * Helper method to get the list of all features
	 */
	public List<Feature> getFeatures(){
		return features;
	}
}
