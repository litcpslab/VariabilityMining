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
import at.variabilityanalysisgui.controller.TreeViewController;
import guiModel.Group;
import javafx.beans.property.SimpleStringProperty;

public class RenameGroup implements ChangeModel<Controller, TreeViewController> {
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
    public void undo(Controller controller, TreeViewController treeViewController) {
        group.setName(new SimpleStringProperty(oldName));
        treeViewController.populateTreeView(controller.getOriginalGroups(), null);
        detailsController.setDetailGroupNameTextField(oldName);
        group.removePreviousName(oldName);
    }

    @Override
    public void redo(Controller controller, TreeViewController treeViewController) {
        group.setName(new SimpleStringProperty(newName));
        treeViewController.populateTreeView(controller.getOriginalGroups(), null);
        detailsController.setDetailGroupNameTextField(newName);
        group.addPreviousName(oldName);
    }
}
