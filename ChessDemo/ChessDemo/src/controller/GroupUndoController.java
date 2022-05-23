package controller;

import javax.swing.undo.UndoManager;
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
        for (UndoController controller: undoList) {
            controller.undo();
        }
    }

    @Override
    public String toString() {
        String result = new String();
        for (UndoController undoController : undoList) {
            String s = undoController.toString();
            if (!s.isEmpty()) {
                result = result + "\n" + s;
            }
        }
        result = result.trim();
        return result;
    }
}
