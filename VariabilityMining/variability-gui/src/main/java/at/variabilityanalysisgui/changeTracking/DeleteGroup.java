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
import at.variabilityanalysisgui.controller.TreeViewController;
import guiModel.Group;

public class DeleteGroup implements ChangeModel<Controller, TreeViewController> {

    private final Group group;
    private final int groupIndex;

    public DeleteGroup(Group group, int groupIndex) {
        this.group = new Group(group);
        this.groupIndex = groupIndex;
    }

    @Override
    public void undo(Controller controller, TreeViewController treeViewController) {
        controller.getOriginalGroups().add(groupIndex, group);
        treeViewController.populateTreeView(controller.getOriginalGroups(), null);
    }

    @Override
    public void redo(Controller controller, TreeViewController treeViewController) {
        controller.getOriginalGroups().remove(group);
        treeViewController.populateTreeView(controller.getOriginalGroups(), null);
    }
}
