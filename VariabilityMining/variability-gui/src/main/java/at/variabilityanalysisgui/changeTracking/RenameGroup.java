/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Sophie Öttl - Change Tracking
 ********************************************************************************/

package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.Controller;
import at.variabilityanalysisgui.controller.DetailsController;
import at.variabilityanalysisgui.controller.FeatureViewController;
import at.variabilityanalysisgui.controller.TreeViewController;
import at.variabilityanalysisgui.view.FeatureTreeNode;
import guiModel.Group;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

public class RenameGroup implements ChangeModel<FeatureViewController, TreeViewController> {
    private final Group group;
    private final String oldName;
    private final String newName;
    private final DetailsController detailsController;

    public RenameGroup(DetailsController detailsController, Group group, String oldName, String newName) {
        this.group = group;
        this.oldName = oldName;
        this.detailsController = detailsController;
        this.newName = newName;
    }

    @Override
    public void undo(FeatureViewController controller, TreeViewController treeViewController) {
        group.setName(new SimpleStringProperty(oldName));
        
        TreeItem<FeatureTreeNode> currentDetailItem = detailsController.getCurrentDetailItem();
        FeatureTreeNode currentTreeNode = currentDetailItem.getValue();
        currentDetailItem.setValue(null);
        currentDetailItem.setValue(currentTreeNode);
        detailsController.setDetailGroupNameTextField(oldName);
        group.removePreviousName(oldName);
    }

    @Override
    public void redo(FeatureViewController controller, TreeViewController treeViewController) {
        group.setName(new SimpleStringProperty(newName));
               
        TreeItem<FeatureTreeNode> currentDetailItem = detailsController.getCurrentDetailItem();
        FeatureTreeNode currentTreeNode = currentDetailItem.getValue();
        currentDetailItem.setValue(null);
        currentDetailItem.setValue(currentTreeNode);
        detailsController.setDetailGroupNameTextField(newName);
        group.addPreviousName(oldName);
    }
}
