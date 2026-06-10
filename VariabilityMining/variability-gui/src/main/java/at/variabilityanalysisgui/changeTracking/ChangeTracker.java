package at.variabilityanalysisgui.changeTracking;

import at.variabilityanalysisgui.controller.ConstraintsViewController;import at.variabilityanalysisgui.controller.Controller;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Stack;

public class ChangeTracker<T, U> {

    private final T controller;
    private final U viewController;
    private final Stack<ChangeModel<T, U>> undoStack = new Stack<>();
    private final Stack<ChangeModel<T, U>> redoStack = new Stack<>();

    private final SimpleBooleanProperty canUndo = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canRedo = new SimpleBooleanProperty(false);

    public ChangeTracker(T controller, U viewController) {
        this.controller = controller;
        this.viewController = viewController;
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            ChangeModel<T, U> change = undoStack.pop();
            change.undo(controller, viewController);
            redrawVisualization(controller);
            redoStack.push(change);
            updateProperties();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            ChangeModel<T, U> change = redoStack.pop();
            change.redo(controller, viewController);
            redrawVisualization(controller);
            undoStack.push(change);
            updateProperties();
        }
    }

    public void addUndo(ChangeModel<T, U> undo) {
        undoStack.push(undo);
        redoStack.clear();
        updateProperties();
        System.out.println("added undo: " + undo);
    }

    private void updateProperties() {
        canUndo.set(!undoStack.isEmpty());
        canRedo.set(!redoStack.isEmpty());
    }

    public SimpleBooleanProperty canUndoProperty() {
        return canUndo;
    }

    public SimpleBooleanProperty canRedoProperty() {
        return canRedo;
    }

    public void redrawVisualization(T controller) {
        if (controller instanceof Controller) {
            ((Controller) controller).redrawVisualization();
        } else {
            ((ConstraintsViewController) controller).updateConstraintModel();
        }
    }
}
