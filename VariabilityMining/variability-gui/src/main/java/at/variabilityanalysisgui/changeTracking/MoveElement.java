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

import at.variabilityanalysisgui.controller.FeatureViewController;
import at.variabilityanalysisgui.controller.TreeViewController;
import guiModel.Element;
import guiModel.Group;

import java.util.List;
import java.util.Map;

public class MoveElement implements ChangeModel<FeatureViewController, TreeViewController> {
    private final Map<Integer, List<Element>> movedElements;
    private final int targetId;

    public MoveElement(int targetId, Map<Integer, List<Element>> movedElements) {
        this.movedElements = movedElements;
        this.targetId = targetId;
    }

    @Override
    public void undo(FeatureViewController controller, TreeViewController treeViewController) {
        Group sourceGroup = controller.findGroupById(targetId);
        for (Map.Entry<Integer, List<Element>> entry : movedElements.entrySet()) {
            Group targetGroup = controller.findGroupById(entry.getKey());
            performMove(treeViewController, targetGroup, sourceGroup, entry.getValue());
        }
    }

    @Override
    public void redo(FeatureViewController controller, TreeViewController treeViewController) {
        Group targetGroup = controller.findGroupById(targetId);
        for (Map.Entry<Integer, List<Element>> entry : movedElements.entrySet()) {
            Group sourceGroup = controller.findGroupById(entry.getKey());
            performMove(treeViewController, targetGroup, sourceGroup, entry.getValue());
        }
    }

    private void performMove(TreeViewController treeViewController, Group targetGroup, Group sourceGroup, List<Element> elements) {
        for (Element element : elements) {
            targetGroup.addElement(element);
            sourceGroup.getElements().remove(element);
        }
        treeViewController.refreshGroup(sourceGroup);
        treeViewController.refreshGroup(targetGroup);
    }
}
