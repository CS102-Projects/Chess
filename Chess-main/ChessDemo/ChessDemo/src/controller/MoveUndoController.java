package controller;

import view.Chessboard;


import java.awt.*;

public class MoveUndoController extends UndoController {
    private ChessComponent firstChess;
    private ChessComponent secondChess;

    private Chessboard chessboard;

    MoveUndoController(ChessComponent _firstChess, ChessComponent _secondChess, Chessboard _chessboard) {
        firstChess = _firstChess.clone();
        secondChess = _secondChess.clone();
        chessboard = _chessboard;

        chessboard.swapChessComponents(_firstChess, _secondChess);
        chessboard.swapColor();
    }

    @Override
    public void undo() {
        chessboard.undoChessComponents(firstChess, secondChess);
        chessboard.swapColor();
    }

    @Override
    public String toString() {
        return "move (" +
                firstChess.getChessboardPoint().getX() + "," + firstChess.getChessboardPoint().getY()+") (" +
                secondChess.getChessboardPoint().getX() + "," + secondChess.getChessboardPoint().getY() + ")";
    }

}
