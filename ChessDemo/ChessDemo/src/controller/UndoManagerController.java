package controller;

import java.util.Stack;


public class UndoManagerController {
    private Stack<UndoController> undostack;

    public UndoManagerController() {
        undostack = new Stack<>();
    }

    private static UndoManagerController instance = new UndoManagerController();

    //4.提供公共的静态的方法，返回类的对象
    public static UndoManagerController getInstance() {
        return instance;
    }

    public void add(UndoController undoController) {
        undostack.push(undoController);
    }

    public boolean canUndo() {
        return !undostack.empty();
    }

    public boolean undo() {
        if (!undostack.empty()) {
            UndoController object = undostack.pop();
            object.undo();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (UndoController controller: undostack) {
            if (!controller.toString().isEmpty()) {
                result.append("\n").append(controller);
            }
        }
        return result.toString();
    }

    public void clear() {
        undostack.clear();
    }
}
