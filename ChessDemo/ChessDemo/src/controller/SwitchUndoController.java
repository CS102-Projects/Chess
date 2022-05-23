package controller;

import model.ChessComponent;
import view.Chessboard;

public class SwitchUndoController extends UndoController {

    private ChessComponent oldChess;
    private ChessComponent newChess;
    private Chessboard chessboard;

    public SwitchUndoController(ChessComponent _oldChess, ChessComponent _newChess, Chessboard _chessboard) {
        oldChess = _oldChess;
        newChess = _newChess;
        chessboard = _chessboard;
        chessboard.putChessOnBoard(newChess);
        newChess.repaint();
    }

    @Override
    public void undo()
    {
        chessboard.putChessOnBoard(oldChess);
        oldChess.repaint();
    }

    @Override
    public String toString() { return ""; }
}
