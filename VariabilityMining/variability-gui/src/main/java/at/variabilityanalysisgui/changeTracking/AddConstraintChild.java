package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintInfoController;
import at.variabilityanalysisgui.controller.ConstraintsViewController;
import constraints.Group;
import variabilityMining.Feature;

public class AddConstraintChild implements ChangeModel<ConstraintsViewController, ConstraintInfoController> {

    Feature feature;
    Group group;
    int listViewIndex;
    int comboBoxIndex;

    public AddConstraintChild(Feature feature, Group group) {
        this.feature = feature;
        this.group = group;
    }

    @Override
    public void undo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.removeFeature(feature);
        comboBoxIndex = controller.getFeatureComboBox().getItems().indexOf(feature);
        controller.getFeatureComboBox().getItems().add(comboBoxIndex, feature);

        if (controller.getIsGroupView()) {
            listViewIndex = controller.getGroupFeatureListView().getItems().indexOf(feature);
            controller.getGroupFeatureListView().getItems().remove(listViewIndex);
            controller.getGroupTreeView().refresh();
        }
    }

    @Override
    public void redo(ConstraintsViewController controller, ConstraintInfoController viewController) {
        group.addFeature(feature);
        controller.getFeatureComboBox().getItems().remove(comboBoxIndex);

        if (controller.getIsGroupView()) {
            controller.getGroupFeatureListView().getItems().add(listViewIndex, feature);
            controller.getGroupTreeView().refresh();
        }
    }
}
