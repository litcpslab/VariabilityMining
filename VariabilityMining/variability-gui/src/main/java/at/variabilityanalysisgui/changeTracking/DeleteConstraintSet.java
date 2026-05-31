package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import javafx.scene.control.TreeItem;

import java.util.Set;

public class DeleteConstraintSet implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    TreeItem<Constraint> constraint;
    int index;
    Set<Constraint> newConstraints;

    public DeleteConstraintSet(TreeItem<Constraint> constraint, int index, Set<Constraint> newConstraints) {
        this.constraint = constraint;
        this.index = index;
        this.newConstraints = newConstraints;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getGroupTreeView().getRoot().getChildren().add(index, constraint);
        controller.getConstraints().add(constraint.getValue());
        controller.getConstraints().removeAll(newConstraints);
        controller.getUnfilteredItems().add(constraint);
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getGroupTreeView().getRoot().getChildren().remove(constraint);
        controller.getConstraints().remove(constraint.getValue());
        controller.getConstraints().addAll(newConstraints);
        controller.getUnfilteredItems().remove(constraint);
    }
}
