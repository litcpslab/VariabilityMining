package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import constraints.Group;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import variabilityMining.Feature;

import java.util.Set;

public class AddConstraintChildSet implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {
    Feature feature;
    Group oldGroup;
    Group newGroup;
    Set<Constraint> constraints;
    int comboBoxIndex;


    public AddConstraintChildSet(Feature feature, Group oldGroup, Group newGroup, Set<Constraint> constraints, int comboBoxIndex) {
        this.feature = feature;
        this.oldGroup = oldGroup;
        this.newGroup = newGroup;
        this.constraints = constraints;
        this.comboBoxIndex = comboBoxIndex;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        oldGroup.addFeature(feature);
        newGroup.removeFeature(feature);
        controller.getConstraints().removeAll(constraints);

        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getFeatureComboBox().getItems().add(comboBoxIndex, feature);
            controller.getGroupFeatureListView().getItems().remove(feature);
            controller.getGroupTreeView().refresh();
        } else if (!controller.getIsGroupView()) {
            ObservableList<TreeItem<Constraint>> treeItems = controller.getGroupTreeView().getRoot().getChildren();
            for (Constraint constraint : constraints) {
                treeItems.removeIf(item -> item.getValue().equals(constraint));
            }
            controller.getGroupTreeView().refresh();
        }
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        oldGroup.removeFeature(feature);
        newGroup.addFeature(feature);
        controller.getConstraints().addAll(constraints);

        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getFeatureComboBox().getItems().remove(feature);
            controller.getGroupFeatureListView().getItems().add(feature);
            controller.getGroupTreeView().refresh();
        } else if (!controller.getIsGroupView()) {
            for (Constraint c : constraints) {
                controller.addSimpleConstraintTreeItem(c);
                controller.getGroupTreeView().refresh();
            }
        }
    }
}
