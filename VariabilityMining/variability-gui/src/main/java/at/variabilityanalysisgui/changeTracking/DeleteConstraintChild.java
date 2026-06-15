package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Group;
import variabilityMining.Feature;

public class DeleteConstraintChild implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    Feature feature;
    Group group;

    public DeleteConstraintChild(Feature feature, Group group) {
        this.feature = feature;
        this.group = group;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.addFeature(feature);
        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getGroupFeatureListView().getItems().add(feature);
        }
        controller.getGroupTreeView().refresh();
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.removeFeature(feature);
        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getGroupFeatureListView().getItems().remove(feature);
        }
        controller.getGroupTreeView().refresh();
    }
}
