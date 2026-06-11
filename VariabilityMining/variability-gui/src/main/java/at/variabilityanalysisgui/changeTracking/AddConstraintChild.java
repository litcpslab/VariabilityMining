package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Group;
import variabilityMining.Feature;

public class AddConstraintChild implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    Feature feature;
    Group group;
    int comboBoxIndex;

    public AddConstraintChild(Feature feature, Group group, int comboBoxIndex) {
        this.feature = feature;
        this.group = group;
        this.comboBoxIndex = comboBoxIndex;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.removeFeature(feature);

        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getFeatureComboBox().getItems().add(comboBoxIndex, feature);
            controller.getGroupFeatureListView().getItems().remove(feature);
            controller.getGroupTreeView().refresh();
        }
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.addFeature(feature);

        if (controller.getIsGroupView() && viewController.getInfoPane().isVisible()) {
            controller.getFeatureComboBox().getItems().remove(comboBoxIndex);
            controller.getGroupFeatureListView().getItems().add(feature);
            controller.getGroupTreeView().refresh();
        }
    }
}
