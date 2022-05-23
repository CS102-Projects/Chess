package controller;

import model.ChessComponent;
import model.KingChessComponent;

import java.util.ArrayList;

public class StatusController {

    private ChessEnablePathController chessEnablePathController;

    public StatusController(ChessEnablePathController chessEnablePathController) {
        this.chessEnablePathController = chessEnablePathController;
    }

    public boolean isHitKing(ChessComponent chessComponent) {
        ArrayList<ChessComponent> arrayList = chessEnablePathController.getEnablePath(chessComponent);
        for (ChessComponent component: arrayList) {
            if (component instanceof KingChessComponent) {
                return true;
            }
        }
        return false;
    }



}
