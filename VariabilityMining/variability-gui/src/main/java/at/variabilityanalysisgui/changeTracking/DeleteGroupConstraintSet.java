package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import javafx.scene.control.TreeItem;

import java.util.Set;

public class DeleteGroupConstraintSet implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    TreeItem<Constraint> constraint;
    int index;
    Set<Constraint> newConstraints;

    public DeleteGroupConstraintSet(TreeItem<Constraint> constraint, int index, Set<Constraint> newConstraints) {
        this.constraint = constraint;
        this.index = index;
        this.newConstraints = newConstraints;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getConstraints().add(constraint.getValue());
        controller.getConstraints().removeAll(newConstraints);
        controller.getUnfilteredItems().add(constraint);

        if (controller.getIsGroupView()) {
            controller.getGroupTreeView().getRoot().getChildren().add(index, constraint);
            controller.getGroupTreeView().refresh();
        }
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getConstraints().remove(constraint.getValue());
        controller.getConstraints().addAll(newConstraints);
        controller.getUnfilteredItems().remove(constraint);

        if (controller.getIsGroupView()) {
            controller.getGroupTreeView().getRoot().getChildren().remove(index);
            controller.getGroupTreeView().refresh();
        }
    }
}
