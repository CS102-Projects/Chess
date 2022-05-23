package controller;

import java.util.ArrayList;

public class GroupUndoController extends UndoController {

    private ArrayList<UndoController> undoList;

    public GroupUndoController() {
        undoList = new ArrayList<>();
    }

    public void addUndoController(UndoController undoController) {
        undoList.add(undoController);
    }

    @Override
    public void undo() {
        for (int i = undoList.size() - 1; i >= 0; i--) {
            undoList.get(i).undo();
        }
//        for (UndoController controller: undoList) {
//            controller.undo();
//        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (UndoController undoController : undoList) {
            String s = undoController.toString();

            if (s.isEmpty()) {
                continue;
            }

            if (undoController instanceof SwitchUndoController) {
                result = new StringBuilder(result.toString().replace("move", "upgrade"));
                result.append(" ").append(s);
            } else {
                result.append("\n").append(s);
            }
        }
        result = new StringBuilder(result.toString().trim());
        return result.toString();
    }
}