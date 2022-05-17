package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;

public class SwitchUndoController extends UndoController {

    private ChessComponent oldChess;
    private ChessComponent newChess;
    private Chessboard chessboard;

    private int switchType;

    public SwitchUndoController(ChessComponent chess, int _switchType, Chessboard _chessboard) {
        oldChess = chess;
        chessboard = _chessboard;
        switchType = _switchType;

        switch (switchType) {
            case 1:
                newChess = new RookChessComponent(chess.getChessboardPoint(),
                        chess.getLocation(),
                        chess.getChessColor(),
                        chess.getClickController(),
                        chess.getSize().width);
                break;
            case 2:
                newChess = new KnightChessComponent(chess.getChessboardPoint(),
                        chess.getLocation(),
                        chess.getChessColor(),
                        chess.getClickController(),
                        chess.getSize().width);
                break;
            case 3:
                newChess = new BishopChessComponent(chess.getChessboardPoint(),
                        chess.getLocation(),
                        chess.getChessColor(),
                        chess.getClickController(),
                        chess.getSize().width);
                break;
            case 4:
                newChess = new QueenChessComponent(chess.getChessboardPoint(),
                        chess.getLocation(),
                        chess.getChessColor(),
                        chess.getClickController(),
                        chess.getSize().width);
                break;
        }

        chessboard.putChessOnBoard(newChess);
        newChess.repaint();
    }
    public SwitchUndoController(ChessComponent _oldChess, ChessComponent _newChess, Chessboard _chessboard) {
        oldChess = _oldChess;
        newChess = _newChess;
        switchType = 0;
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
    public String toString() {
        ChessboardPoint point = oldChess.getChessboardPoint();
        if (switchType == 0) {
            return "";
        } else {
            return Integer.toString(switchType);
        }
    }
}
