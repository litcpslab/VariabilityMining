/**
  Modified from Variability Analyser GUI
  Original license: MIT License (c) 2025 Michael Schmidhammer
 */

package at.variabilityanalysisgui.view;

import java.util.ArrayList;
import java.util.List;

import at.variabilityanalysisgui.controller.TreeViewController;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class FeatureTreeCell extends TreeCell<FeatureTreeNode> {

    private final HBox hbox;
    private final Label nameLabel = new Label();
    private final Label typeLabel = new Label();
    private final Button deleteButton = new Button("X");
    private final TreeViewController controller;

    public FeatureTreeCell(TreeViewController controller) {
        this.controller = controller;

        // Buttons
        deleteButton.setStyle("-fx-padding: 2 5 2 5;");
        deleteButton.setTooltip(new Tooltip("Remove this group/element"));

        // Layout
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER_LEFT);

        deleteButton.setOnAction(event -> {
            if (getItem() != null) {
                controller.handleDeleteAction(getTreeItem());
            }
        });

        setupDragAndDrop();
    }

    @Override
    protected void updateItem(FeatureTreeNode item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            nameLabel.setText(item.getDisplayName());
            if (item.getType() == FeatureTreeNode.DataType.ELEMENT) {
                typeLabel.setText("E");
                typeLabel.setStyle("-fx-text-fill: #00AA00;");
            } else if (item.getType() == FeatureTreeNode.DataType.CONTAINER) {
                typeLabel.setText("D");
                typeLabel.setStyle("-fx-text-fill: #0000FF;");
            } else if (item.getType() == FeatureTreeNode.DataType.GROUP) {
                typeLabel.setText("G");
                typeLabel.setStyle("-fx-text-fill: #A0A0A0;");
            }
            hbox.getChildren().setAll(nameLabel, typeLabel);

            if (item.getData() != null) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                hbox.getChildren().add(spacer);
                hbox.getChildren().add(deleteButton);
                hbox.setMinWidth(0);
                hbox.setPrefWidth(100);
            }

            setGraphic(hbox);
            setText(null);
        }
    }

    private void setupDragAndDrop() {
        setOnDragDetected(event -> {
            if (getItem() != null && (getItem().getType() == FeatureTreeNode.DataType.ELEMENT || getItem().getType() == FeatureTreeNode.DataType.GROUP)) {
            	List<TreeItem<FeatureTreeNode>> selectedItems = new ArrayList<>(controller.getFeatureTreeView().getSelectionModel().getSelectedItems());
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("DnD started");
                db.setContent(content);
                controller.setDraggedItems(selectedItems);
                event.consume();
            }
        });

        setOnDragOver(event -> {
            TreeItem<FeatureTreeNode> targetItem = getTreeItem();
            List<TreeItem<FeatureTreeNode>> sourceItems = controller.getDraggedItems();

            boolean canDrop = false;
            if (targetItem != null && targetItem.getValue() != null && sourceItems != null) { 
                FeatureTreeNode targetNode = targetItem.getValue();

                if((targetNode.getType() == FeatureTreeNode.DataType.GROUP || targetNode.getType() == FeatureTreeNode.DataType.ELEMENT) 
                        && !sourceItems.contains(targetItem)) {
                    canDrop = true;
                }

                if(sourceItems.contains(targetItem) || sourceItems.contains(targetItem.getParent()) || targetIsSameGroup(sourceItems, targetItem)) {
                    canDrop = false;
                }
            }

            if(canDrop) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        setOnDragDropped(event -> {
            if(controller.getDraggedItems() != null) {
            	List<TreeItem<FeatureTreeNode>> sourceItems = controller.getDraggedItems();
                TreeItem<FeatureTreeNode> targetItem = getTreeItem();
                if((targetItem.getValue().getType() == FeatureTreeNode.DataType.GROUP || targetItem.getValue().getType() == FeatureTreeNode.DataType.ELEMENT)) {
                	controller.moveElementsToGroup(sourceItems, targetItem);
                }
                
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

	private boolean targetIsSameGroup(List<TreeItem<FeatureTreeNode>> sourceItems, TreeItem<FeatureTreeNode> targetItem) {
		for(TreeItem<FeatureTreeNode> sourceItem: sourceItems) {
			if(targetItem.getChildren().contains(sourceItem) || targetItem.getParent().equals(sourceItem.getParent())) {
				return true;
			}
		}
		return false;
	}
}
