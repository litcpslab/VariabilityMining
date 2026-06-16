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
import guiModel.Element;
import guiModel.Group;

public class DeleteElement implements ChangeModel<Controller, TreeViewController> {
    private final Element element;
    private final int elemIndex;
    private final int groupId;

    public DeleteElement(Element element, int elemIndex, int groupId) {
        this.element = element;
        this.groupId = groupId;
        this.elemIndex = elemIndex;
    }

    @Override
    public void undo(Controller controller, TreeViewController treeViewController) {
        Group group = controller.findGroupById(groupId);
        group.addElementAt(element, elemIndex);
        treeViewController.refreshGroup(group);
    }

    @Override
    public void redo(Controller controller, TreeViewController treeViewController) {
        Group group = controller.findGroupById(groupId);
        group.getElements().remove(element);
        treeViewController.refreshGroup(group);
    }
}
