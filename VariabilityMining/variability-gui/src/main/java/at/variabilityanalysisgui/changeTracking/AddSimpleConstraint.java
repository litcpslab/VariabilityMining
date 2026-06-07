package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import javafx.scene.control.TreeItem;

public class AddSimpleConstraint implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    TreeItem<Constraint> constraint;
    int index;

    public AddSimpleConstraint(TreeItem<Constraint> constraint, int index) {
        this.constraint = constraint;
        this.index = index;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getUnfilteredItems().remove(constraint);
        controller.getConstraints().remove(constraint.getValue());

        if (!controller.getIsGroupView()) {
            controller.getGroupTreeView().getRoot().getChildren().remove(index);
            controller.getGroupTreeView().refresh();
        }
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getUnfilteredItems().add(constraint);
        controller.getConstraints().add(constraint.getValue());

        if (!controller.getIsGroupView()) {
            controller.getGroupTreeView().getRoot().getChildren().add(index, constraint);
            controller.getGroupTreeView().refresh();
        }
    }
}
