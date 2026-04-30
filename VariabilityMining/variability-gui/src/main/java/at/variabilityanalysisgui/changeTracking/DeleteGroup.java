package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.Controller;
import at.variabilityanalysisgui.controller.TreeViewController;
import guiModel.Group;

public class DeleteGroup implements ChangeModel<Controller, TreeViewController> {

    private final Group group;
    private final int groupIndex;

    public DeleteGroup(Group group, int groupIndex) {
        this.group = new Group(group);
        this.groupIndex = groupIndex;
    }

    @Override
    public void undo(Controller controller, TreeViewController treeViewController) {
        controller.getOriginalGroups().add(groupIndex, group);
        treeViewController.populateTreeView(controller.getOriginalGroups(), null);
    }

    @Override
    public void redo(Controller controller, TreeViewController treeViewController) {
        controller.getOriginalGroups().remove(group);
        treeViewController.populateTreeView(controller.getOriginalGroups(), null);
    }
}
