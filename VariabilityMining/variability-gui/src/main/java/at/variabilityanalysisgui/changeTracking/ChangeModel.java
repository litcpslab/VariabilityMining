package at.variabilityanalysisgui.changeTracking;

public interface ChangeModel<T, U> {

    void undo(T controller, U viewController);

    void redo(T controller, U viewController);
}
