package controller;

import model.ChessColor;
import model.ChessComponent;
import model.KingChessComponent;
import view.Chessboard;
import view.ChessboardPoint;

import java.util.ArrayList;

public class StatusController {

    private ChessEnablePathController chessEnablePathController;

    public StatusController(ChessEnablePathController chessEnablePathController) {
        this.chessEnablePathController = chessEnablePathController;
    }

    public boolean isHitKing(ChessColor color, Chessboard chessboard) {

        ChessComponent[][] chessComponents = chessboard.getChessComponents();

        for (ChessComponent[] chessComponent : chessComponents) {
            for (ChessComponent value : chessComponent) {
                if (value.getChessColor() == color) {

                    ArrayList<ChessComponent> arrayList = ChessEnablePathController.getEnablePath(value);
                    if (arrayList == null) {
                        continue;
                    }
                    for (ChessComponent component : arrayList) {
                        if (component instanceof KingChessComponent) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // 判断是否死局
    public boolean isDead(ChessColor color, Chessboard chessboard) {
        ChessComponent king = chessboard.getKing(color);

        // 没有人攻击王，没死
        ArrayList<ChessComponent> attackers = ChessEnablePathController.getAttackers(king);
        if (attackers.isEmpty()) {
            return false;
        }

        // 王还有活路走，没死
        ArrayList<ChessComponent> enablePoint = ChessEnablePathController.getEnablePath(king);
        if (enablePoint != null && !enablePoint.isEmpty()) {
            return false;
        }

        // 同时两个以上敌人攻击王，必死
        if (attackers.size() >= 2) {
            return true;
        }

        // 只有一个敌人攻击王
        if (attackers.size() == 1) {
            // 找到敌方攻击者
            ChessComponent enemy = attackers.get(0);

            // 找到攻击者和king之间的防御点
            ArrayList<ChessboardPoint> defendPath = ChessEnablePathController.getMovePath(king.getChessboardPoint(), enemy.getChessboardPoint());

            // 找不到防御点，只能选择干掉攻击者
            if (defendPath.isEmpty()) {
                defendPath.add(enemy.getChessboardPoint());
            }

            // 遍历本方的棋子，看是否能救王一命
            ChessComponent[][] chessComponents = chessboard.getChessComponents();
            for (ChessComponent[] chessComponent : chessComponents) {
                for (ChessComponent component : chessComponent) {
                    if (component.getChessColor() != color) {
                        continue;
                    }
                    if (component instanceof KingChessComponent) {
                        continue;
                    }
                    // 此人可吃掉攻击者 或者 挡住攻击者杀王的路线
                    for (ChessboardPoint defendPoint : defendPath) {
                        if (component.canMoveTo(chessComponents, defendPoint)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    public boolean isCanNotMoveDraw(ChessColor color, Chessboard chessboard) {
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        for (ChessComponent[] chessComponent : chessComponents) {
            for (ChessComponent component : chessComponent) {
                if (component.getChessColor() != color) {
                    continue;
                }
                if (!ChessEnablePathController.getSafePath(component).isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }



}
