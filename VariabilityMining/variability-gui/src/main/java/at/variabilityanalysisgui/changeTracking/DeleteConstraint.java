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

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import javafx.scene.control.TreeItem;

public class DeleteConstraint implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    TreeItem<Constraint> constraint;
    int index;
    boolean isGroupConstraint;

    public DeleteConstraint(TreeItem<Constraint> constraint, int index, boolean isGroupConstraint) {
        this.constraint = constraint;
        this.index = index;
        this.isGroupConstraint = isGroupConstraint;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getUnfilteredItems().add(constraint);
        controller.getConstraints().add(constraint.getValue());

        if((controller.getIsGroupView() && isGroupConstraint) || (!controller.getIsGroupView() &&  !isGroupConstraint)) {
            controller.getGroupTreeView().getRoot().getChildren().add(index, constraint);
        }
        controller.getGroupTreeView().refresh();
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getUnfilteredItems().remove(constraint);
        controller.getConstraints().remove(constraint.getValue());

        if((controller.getIsGroupView() && isGroupConstraint) || (!controller.getIsGroupView() &&  !isGroupConstraint)) {
            controller.getGroupTreeView().getRoot().getChildren().remove(index);
        }
        controller.getGroupTreeView().refresh();
    }
}
