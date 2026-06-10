package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.Controller;
import at.variabilityanalysisgui.controller.TreeViewController;
import guiModel.Element;
import guiModel.Group;

import java.util.List;
import java.util.Map;

public class MoveElement implements ChangeModel<Controller, TreeViewController> {
    private final Map<Integer, List<Element>> movedElements;
    private final int targetId;

    public MoveElement(int targetId, Map<Integer, List<Element>> movedElements) {
        this.movedElements = movedElements;
        this.targetId = targetId;
    }

    @Override
    public void undo(Controller controller, TreeViewController treeViewController) {
        Group sourceGroup = controller.findGroupById(targetId);
        for (Map.Entry<Integer, List<Element>> entry : movedElements.entrySet()) {
            Group targetGroup = controller.findGroupById(entry.getKey());
            performMove(treeViewController, targetGroup, sourceGroup, entry.getValue());
        }
    }

    @Override
    public void redo(Controller controller, TreeViewController treeViewController) {
        Group targetGroup = controller.findGroupById(targetId);
        for (Map.Entry<Integer, List<Element>> entry : movedElements.entrySet()) {
            Group sourceGroup = controller.findGroupById(entry.getKey());
            performMove(treeViewController, targetGroup, sourceGroup, entry.getValue());
        }
    }

    private void performMove(TreeViewController treeViewController, Group targetGroup, Group sourceGroup, List<Element> elements) {
        for (Element element : elements) {
            targetGroup.addElement(element);
            sourceGroup.getElements().remove(element);
        }
        treeViewController.refreshGroup(sourceGroup);
        treeViewController.refreshGroup(targetGroup);
    }
}
