package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Group;
import variabilityMining.Feature;

public class AddConstraintChild implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    Feature feature;
    Group group;

    public AddConstraintChild(Feature feature, Group group) {
        this.feature = feature;
        this.group = group;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.removeFeature(feature);
        controller.getFeatureComboBox().getItems().add(feature);
        controller.getGroupFeatureListView().getItems().remove(feature);
        controller.getGroupTreeView().refresh();
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.addFeature(feature);
        controller.getFeatureComboBox().getItems().remove(feature);
        controller.getGroupFeatureListView().getItems().add(feature);
        controller.getGroupTreeView().refresh();
    }
}
