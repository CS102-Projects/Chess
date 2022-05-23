package controller;

import model.ChessColor;
import view.ChessGameFrame;
import view.Chessboard;

import java.io.File;
import java.io.IOException;

public class GameController {
    private Chessboard chessboard;
    private StoreController storeController;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
        storeController = new StoreController(chessboard);
    }

    public void loadGameFromFile(File file) {
        try {
            reset();
            storeController.load(file, chessboard);
            chessboard.clearStatu();
            chessboard.clearPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        chessboard.reset();
        UndoManagerController.getInstance().clear();
        ChessGameFrame.record.setText("MoveRecord");
    }

    public boolean canUndo() {
        return UndoManagerController.getInstance().canUndo();
    }

    public boolean undo() {
        chessboard.swapColor();
        if (chessboard.getCurrentColor() == ChessColor.BLACK) {
            ChessGameFrame.record.append("\nBLACK Undo");
        } else
            ChessGameFrame.record.append("\nWHITE Undo");
        ChessGameFrame.currentPlayerLabel.setText(String.valueOf(Chessboard.currentColor));
        chessboard.clearPath();
        chessboard.clearStatu();

        return UndoManagerController.getInstance().undo();
    }

    public void store(String path) throws IOException {
        storeController.store(path);
    }
}