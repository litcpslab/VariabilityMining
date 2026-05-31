package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import javafx.scene.control.TreeItem;

public class AddSimpleConstraint implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    TreeItem<Constraint> constraint;

    public AddSimpleConstraint(TreeItem<Constraint> constraint) {
        this.constraint = constraint;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getGroupTreeView().getRoot().getChildren().remove(constraint);
        controller.getUnfilteredItems().remove(constraint);
        controller.getGroupTreeView().refresh();
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        controller.getGroupTreeView().getRoot().getChildren().add(constraint);
        controller.getUnfilteredItems().add(constraint);
        controller.getGroupTreeView().refresh();
    }
}
