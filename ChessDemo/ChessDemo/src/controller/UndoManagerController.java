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
    public Boolean undo() {
        if (!undostack.empty()) {
            UndoController object = undostack.pop();
            object.undo();
            return true;
        }
        return false;
    }

    public String toString() {
        String result = new String();
        for (UndoController controller: undostack) {
            if (!controller.toString().isEmpty()) {
                result = result + "\n" + controller.toString();
            }
        }
        return result;
    }

    public void clear() {
        undostack.clear();
    }
}
