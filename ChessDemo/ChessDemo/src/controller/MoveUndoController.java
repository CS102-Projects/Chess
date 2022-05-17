package controller;

import model.ChessComponent;
import view.Chessboard;

public class MoveUndoController extends UndoController {
    private ChessComponent firstChess;
    private ChessComponent secondChess;

    private Chessboard chessboard;

    MoveUndoController(ChessComponent _firstChess, ChessComponent _secondChess, Chessboard _chessboard) {
        firstChess = _firstChess.clone();
        secondChess = _secondChess.clone();
        chessboard = _chessboard;

        chessboard.swapChessComponents(_firstChess, _secondChess);
        _firstChess.incrementStep();

        System.out.println("cur step: " + _firstChess.getCurStep() + " count: " + _firstChess.getStepCount() + " total: " + _firstChess.getTotalStepCount());
        System.out.println(this);
    }

    @Override
    public void undo() {
        chessboard.undoChessComponents(firstChess, secondChess);
        ChessComponent.decrementTotalCount();
        System.out.println("cur step: " + firstChess.getCurStep() + " count: " + firstChess.getStepCount() + " total: " + firstChess.getTotalStepCount());
//        chessboard.swapColor();
//        firstChess.decrementStep();
    }

    @Override
    public String toString() {
        return "move (" +
                firstChess.getChessboardPoint().getX() + "," + firstChess.getChessboardPoint().getY()+") (" +
                secondChess.getChessboardPoint().getX() + "," + secondChess.getChessboardPoint().getY() + ")";
    }

}
