package at.variabilityanalysisgui.changeTracking;

import java.util.Stack;

public class ChangeTracker<T, U> {

    private final T controller;
    private final U viewController;
    private final Stack<ChangeModel<T, U>> undoStack = new Stack<>();
    private final Stack<ChangeModel<T, U>> redoStack = new Stack<>();

    public ChangeTracker(T controller, U viewController) {
        this.controller = controller;
        this.viewController = viewController;
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            ChangeModel<T, U> change = undoStack.pop();
            change.undo(controller, viewController);
            redoStack.push(change);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            ChangeModel<T, U> change = redoStack.pop();
            change.redo(controller, viewController);
            undoStack.push(change);
        }
    }

    public void addUndo(ChangeModel<T, U> undo) {
        undoStack.push(undo);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
