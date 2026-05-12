package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Constraint;
import constraints.Group;
import variabilityMining.Feature;

import java.util.Set;

public class AddConstraintChildSet implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {
    Feature feature;
    Group oldGroup;
    Group newGroup;
    Set<Constraint> constraints;

    public AddConstraintChildSet(Feature feature, Group oldGroup, Group newGroup, Set<Constraint> constraints) {
        this.feature = feature;
        this.oldGroup = oldGroup;
        this.newGroup = newGroup;
        this.constraints = constraints;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        oldGroup.addFeature(feature);
        newGroup.removeFeature(feature);

        controller.getConstraints().removeAll(constraints);
        controller.getFeatureComboBox().getItems().add(feature);
        controller.getGroupFeatureListView().getItems().remove(feature);
        controller.getGroupTreeView().refresh();
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        oldGroup.removeFeature(feature);
        newGroup.addFeature(feature);

        controller.getConstraints().addAll(constraints);
        controller.getFeatureComboBox().getItems().remove(feature);
        controller.getGroupFeatureListView().getItems().add(feature);
        controller.getGroupTreeView().refresh();
    }
}
