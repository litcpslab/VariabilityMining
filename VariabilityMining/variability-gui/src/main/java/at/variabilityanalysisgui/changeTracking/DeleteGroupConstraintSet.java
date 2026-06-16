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
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.Comparator;
import java.util.Set;

public class DeleteGroupConstraintSet implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    TreeItem<Constraint> constraint;
    int index;
    Set<Constraint> keepConstraints;

    public DeleteGroupConstraintSet(TreeItem<Constraint> constraint, int index, Set<Constraint> keepConstraints) {
        this.constraint = constraint;
        this.index = index;
        this.keepConstraints = keepConstraints;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getConstraints().add(constraint.getValue());
        controller.getConstraints().removeAll(keepConstraints);
        controller.getUnfilteredItems().add(constraint);

        if (controller.getIsGroupView()) {
            controller.getGroupTreeView().getRoot().getChildren().add(index, constraint);
        } else {
            ObservableList<TreeItem<Constraint>> treeItems = controller.getGroupTreeView().getRoot().getChildren();
            for (Constraint constraint : keepConstraints) {
                treeItems.removeIf(item -> item.getValue().equals(constraint));
            }
        }
        controller.getGroupTreeView().refresh();
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getConstraints().remove(constraint.getValue());
        controller.getConstraints().addAll(keepConstraints);
        controller.getUnfilteredItems().remove(constraint);

        if (controller.getIsGroupView()) {
            controller.getGroupTreeView().getRoot().getChildren().remove(index);
        } else {
            for (Constraint c : keepConstraints) {
                controller.addSimpleConstraintTreeItem(c);
            }
            controller.getGroupTreeView().getRoot().getChildren()
                    .sort(Comparator.comparingInt(item -> controller.getConstraints().stream().toList().indexOf(item.getValue())));
        }
        controller.getGroupTreeView().refresh();
    }
}
