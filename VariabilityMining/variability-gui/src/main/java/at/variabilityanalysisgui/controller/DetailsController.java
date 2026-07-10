/***
 
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
 	Alexander Stummer - Adapted name changing operations
    Kejda Domi- Added the visualization section
   Sophie Öttl - Change Tracking
**/

package at.variabilityanalysisgui.controller;

import at.variabilityanalysisgui.changeTracking.RenameGroup;
import at.variabilityanalysisgui.view.DifferenceDirectory;
import at.variabilityanalysisgui.view.FeatureTreeNode;
import guiModel.Difference;
import guiModel.Element;
import guiModel.Group;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.stream.Collectors;

public class DetailsController {

    Controller controller;
    private final ListView<Element> detailSubElementListView;
    private final ScrollPane detailScrollPane;
    private final HBox detailsNameHBox;
    private final Label detailLocationLabel;
    private final TextArea detailLocationTextArea;
    private final TextField detailGroupNameTextField;
    private final Button detailChangeNameButton;
    private final Label detailOccurrenceLabel;
    private final ListView<String> detailOccurrencesListView;
    private final Label detailElementLabel;
    private final TextArea detailElementData;
    private final Label detailSubElementLabel;
    private final Label detailNamingHistoryLabel;
    private final ListView<String> detailNamingHistoryListView;
    private final Button detailChangeBackButton;
    private final HBox detailNamingHistoryHBox;

    private TreeItem<FeatureTreeNode> currentDetailItem = null;

    public DetailsController(Controller controller, ListView<Element> detailSubElementListView, ScrollPane detailScrollPane,
                             HBox detailsNameHBox, Label detailLocationLabel, TextArea detailLocationTextArea,
                             TextField detailGroupNameTextField, Button detailChangeNameButton, Label detailOccurrenceLabel, ListView<String> detailOccurrencesListView,
                             Label detailElementLabel, TextArea detailElementData, Label detailSubElementLabel, Button detailCloseButton, Label detailNamingHistoryLabel,
                             ListView<String> detailNamingHistoryListView, Button detailChangeBackButton, HBox detailNamingHistoryHBox) {
        this.controller = controller;
        this.detailSubElementListView = detailSubElementListView;
        this.detailScrollPane = detailScrollPane;
        this.detailsNameHBox = detailsNameHBox;
        this.detailLocationLabel = detailLocationLabel;
        this.detailLocationTextArea = detailLocationTextArea;
        this.detailGroupNameTextField = detailGroupNameTextField;
        this.detailChangeNameButton = detailChangeNameButton;
        this.detailOccurrenceLabel = detailOccurrenceLabel;
        this.detailOccurrencesListView = detailOccurrencesListView;
        this.detailElementLabel = detailElementLabel;
        this.detailElementData = detailElementData;
        this.detailSubElementLabel = detailSubElementLabel;

        this.detailNamingHistoryLabel = detailNamingHistoryLabel;
        this.detailNamingHistoryListView = detailNamingHistoryListView;
        this.detailChangeBackButton = detailChangeBackButton;
        this.detailNamingHistoryHBox = detailNamingHistoryHBox;

        this.detailChangeNameButton.setOnAction(event -> updateGroupName(false));
        this.detailGroupNameTextField.setOnAction(event -> updateGroupName(false));
        this.detailChangeBackButton.setOnAction(event -> updateGroupName(true));
        // Configure the optional detail elements list view display
        detailSubElementListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Element item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(2);
                    Label label = new Label(item.getLocation());
                    label.setStyle("-fx-font-weight: bold;");
                    vbox.getChildren().add(label);
                    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        Label descLabel = new Label(item.getDescription());
                        descLabel.setWrapText(true);
                        vbox.getChildren().add(descLabel);
                    }
                    setGraphic(vbox);
                    setText(null);
                }
            }
        });
        detailCloseButton.setOnAction(event -> hideDetailsPane());
    }

	// Detail Pane
    public void showDetailsPane(Difference diff, TreeItem<FeatureTreeNode> item) {
        currentDetailItem = item;
        if (diff instanceof Group) {
            showDetailsPaneGroup((Group) diff);
        } else if (diff instanceof Element) {
            showDetailsPaneElement((Element) diff);
        } else if (diff instanceof DifferenceDirectory) {
            showDetailsPaneDifferenceDirectory((DifferenceDirectory) diff);
        } else {
            System.out.println("No details available for this item.");
            hideDetailsPane();
        }
    }

    private void showDetailsPaneDifferenceDirectory(DifferenceDirectory dir) {

        setNodeVisibility(true, detailLocationLabel, detailLocationTextArea, detailSubElementLabel, detailSubElementListView, detailScrollPane);
        setNodeVisibility(false, detailsNameHBox, detailOccurrenceLabel, detailOccurrencesListView, detailElementLabel, detailElementData);

        detailElementData.setEditable(false);
        detailLocationTextArea.setText(dir.getPath());
        detailSubElementListView.setItems(currentDetailItem.getChildren().stream().map(i -> (Element) i.getValue().getData()).collect(Collectors.toCollection(FXCollections::observableArrayList)));

    }

    private void showDetailsPaneGroup(Group group) {
        detailGroupNameTextField.setText(group.getName().get());
        //group.getName().bind(detailGroupNameTextField.textProperty());
        detailSubElementListView.setItems(FXCollections.observableArrayList(group.getElements()));
        detailOccurrencesListView.setItems(FXCollections.observableArrayList(group.getOccurrences()));
        detailNamingHistoryListView.setItems(group.getPreviousNames());

        setNodeVisibility(true, detailOccurrenceLabel, detailOccurrencesListView, detailSubElementLabel, detailSubElementListView, detailScrollPane, detailsNameHBox, detailNamingHistoryLabel, detailNamingHistoryListView, detailChangeBackButton, detailNamingHistoryHBox, detailChangeNameButton);
        setNodeVisibility(false, detailLocationLabel, detailLocationTextArea, detailElementLabel, detailElementData);

        detailGroupNameTextField.setEditable(true);
        detailElementData.setEditable(false);
    }

    private void showDetailsPaneElement(Element element) {
        detailGroupNameTextField.setText(element.getName().get());
        element.getName().bind(detailGroupNameTextField.textProperty());
        detailElementData.setText(element.getDescription());
        detailSubElementListView.setItems(FXCollections.observableArrayList(element));
        detailLocationTextArea.setText(element.getLocation());

        setNodeVisibility(false, detailOccurrenceLabel, detailOccurrencesListView, detailNamingHistoryLabel, detailNamingHistoryListView, detailChangeBackButton, detailNamingHistoryHBox, detailChangeNameButton);
        setNodeVisibility(true, detailsNameHBox, detailLocationLabel, detailLocationTextArea, detailElementLabel, detailElementData, detailScrollPane);
        setNodeVisibility(currentDetailItem.getValue().isDirectory(), detailSubElementLabel, detailSubElementListView);

        detailGroupNameTextField.setEditable(false);
        detailLocationTextArea.setEditable(false);
        detailElementData.setEditable(false);

    }

    private void setNodeVisibility(boolean visible, Node... node) {
        for (Node n : node) {
            if (n != null) {
                n.setVisible(visible);
                n.setManaged(visible);
            }
        }
    }

    public void hideDetailsPane() {
        detailScrollPane.setVisible(false);
        detailScrollPane.setManaged(false);
        currentDetailItem = null;
        detailOccurrencesListView.setItems(FXCollections.emptyObservableList());
        detailSubElementListView.setItems(FXCollections.emptyObservableList());
    }
    
    private void updateGroupName(boolean changeBack) {
    	FeatureTreeNode currentTreeNode = currentDetailItem.getValue();
        String candidateName = detailGroupNameTextField.getText();
        if (changeBack) {
            candidateName = detailNamingHistoryListView.getSelectionModel().getSelectedItem();
            if (candidateName == null) {
                return;
            }
        }
		Group group = (Group) currentDetailItem.getValue().getData();
        if(isValidName(candidateName)) {
            String oldName = group.getName().get();
            group.addPreviousName(oldName);
            controller.getChangeTracker().addUndo(new RenameGroup(this, group, oldName, candidateName));
			group.getName().setValue(candidateName);
			currentDetailItem.setValue(null);
			currentDetailItem.setValue(currentTreeNode);
            controller.redrawVisualization();
            setDetailGroupNameTextField(candidateName);
		} else {
			Alert errorAlert = new Alert(AlertType.ERROR, "Invalid name. Duplicate names are not allowed, as well as names starting with a number or containing whitespaces!", ButtonType.OK);
			errorAlert.setHeaderText("Invalid Name Error");
			
			Optional<ButtonType> result = errorAlert.showAndWait();
		}	
   	}

    public void setDetailGroupNameTextField(String name) {
        detailGroupNameTextField.setText(name);
    }

    private boolean isValidName(String candidateName) {
		if(candidateName.isEmpty() || candidateName.contains(" ") || Character.isDigit(candidateName.charAt(0))) {
			return false;
		}
		
		return !controller.getOriginalGroups().stream().anyMatch(group -> group.getName().getValue().equals(candidateName));
	}

}
