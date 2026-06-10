package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.Controller;
import at.variabilityanalysisgui.controller.TreeViewController;
import guiModel.Element;
import guiModel.Group;

public class DeleteElement implements ChangeModel<Controller, TreeViewController> {
    private final Element element;
    private final int elemIndex;
    private final int groupId;

    public DeleteElement(Element element, int elemIndex, int groupId) {
        this.element = element;
        this.groupId = groupId;
        this.elemIndex = elemIndex;
    }

    @Override
    public void undo(Controller controller, TreeViewController treeViewController) {
        Group group = controller.findGroupById(groupId);
        group.addElementAt(element, elemIndex);
        treeViewController.refreshGroup(group);
    }

    @Override
    public void redo(Controller controller, TreeViewController treeViewController) {
        Group group = controller.findGroupById(groupId);
        group.getElements().remove(element);
        treeViewController.refreshGroup(group);
    }
}
