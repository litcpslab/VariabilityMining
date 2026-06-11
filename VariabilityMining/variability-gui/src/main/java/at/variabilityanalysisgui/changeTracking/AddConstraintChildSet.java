package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import constraints.Group;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import variabilityMining.Feature;

import java.util.Comparator;
import java.util.Set;

public class AddConstraintChildSet implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {
    Feature feature;
    Group oldGroup;
    Group newGroup;
    Set<Constraint> keepConstraints;
    int comboBoxIndex;


    public AddConstraintChildSet(Feature feature, Group oldGroup, Group newGroup, Set<Constraint> keepConstraints, int comboBoxIndex) {
        this.feature = feature;
        this.oldGroup = oldGroup;
        this.newGroup = newGroup;
        this.keepConstraints = keepConstraints;
        this.comboBoxIndex = comboBoxIndex;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        oldGroup.addFeature(feature);
        newGroup.removeFeature(feature);
        controller.getConstraints().removeAll(keepConstraints);

        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getFeatureComboBox().getItems().add(comboBoxIndex, feature);
            controller.getGroupFeatureListView().getItems().remove(feature);
            controller.getGroupTreeView().refresh();
        } else if (!controller.getIsGroupView()) {
            ObservableList<TreeItem<Constraint>> treeItems = controller.getGroupTreeView().getRoot().getChildren();
            for (Constraint constraint : keepConstraints) {
                treeItems.removeIf(item -> item.getValue().equals(constraint));
            }
            controller.getGroupTreeView().refresh();
        }
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        oldGroup.removeFeature(feature);
        newGroup.addFeature(feature);
        controller.getConstraints().addAll(keepConstraints);

        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getFeatureComboBox().getItems().remove(feature);
            controller.getGroupFeatureListView().getItems().add(feature);
            controller.getGroupTreeView().refresh();
        } else if (!controller.getIsGroupView()) {
            for (Constraint c : keepConstraints) {
                controller.addSimpleConstraintTreeItem(c);
            }
            controller.getGroupTreeView().getRoot().getChildren()
                    .sort(Comparator.comparingInt(item -> controller.getConstraints().stream().toList().indexOf(item.getValue())));
            controller.getGroupTreeView().refresh();
        }
    }
}
