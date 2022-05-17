package controller;

import view.ChessGameFrame;
import view.Chessboard;

import java.io.IOException;

public class GameController {
    private Chessboard chessboard;
    private StoreController storeController;


    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
        storeController = new StoreController();
    }

    public void loadGameFromFile(String path) {
        try {
            reset();
            storeController.load(path, chessboard);
            chessboard.clearStatu();
            chessboard.clearPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        chessboard.reset();
        UndoManagerController.getInstance().clear();
    }

    public boolean canUndo() {
        return UndoManagerController.getInstance().canUndo();
    }

    public Boolean undo()
    {
        chessboard.swapColor();
        ChessGameFrame.currentPlayerLabel.setText(String.valueOf(Chessboard.currentColor));
        chessboard.clearPath();
        chessboard.clearStatu();
        return UndoManagerController.getInstance().undo();
    }

    public void store(String path) throws IOException {
        storeController.store(path);
    }
}
