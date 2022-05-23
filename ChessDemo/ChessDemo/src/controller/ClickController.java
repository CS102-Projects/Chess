package controller;


import model.*;
import view.Chessboard;
import view.ChessboardPoint;
import view.SwitchChessDialog;

import java.util.ArrayList;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first;

    private UndoManagerController undoManagerController;
    private SwitchChessDialog switchChessDlg;


    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
        undoManagerController = UndoManagerController.getInstance();
        switchChessDlg = new SwitchChessDialog();
        switchChessDlg.setModal(true);
        switchChessDlg.setVisible(false);
    }

    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessboard.showEnablePath(chessComponent);
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
                System.out.println("cur step: " + chessComponent.getCurStep() + " count: " + chessComponent.getStepCount() + " total: " + chessComponent.getTotalStepCount());
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessboard.clearPath();
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (chessComponent.getChessColor() == first.getChessColor()) {
                first.setSelected(false);
                first = chessComponent;
                first.setSelected(true);
                chessboard.showEnablePath(chessComponent);
            } else if (handleSecond(chessComponent)) {
                chessboard.clearPath();
                ChessboardPoint movePoint = new ChessboardPoint(chessComponent.getChessboardPoint().getX(), chessComponent.getChessboardPoint().getY());
                ActionType actionType = calcActionType(first, chessComponent);
                handleAction(chessComponent, actionType);

                ChessComponent curChess = chessboard.getChessComponent(movePoint);
                if (chessboard.getStatusController().isHitKing(curChess)) {
                    System.out.println("--attack king--");
                }

                chessboard.swapColor();
                first.setSelected(false);
                first = null;
            }
        }
    }

    /**
     * @param chessComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(ChessComponent chessComponent) {
        return chessComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param chessComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(ChessComponent chessComponent) {
        return chessComponent.getChessColor() != chessboard.getCurrentColor() &&
                first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint());
    }

    private ActionType calcActionType(ChessComponent first, ChessComponent second)
    {
        if (first instanceof PawnChessComponent) {
            if (second instanceof EmptySlotComponent && first.getChessboardPoint().getY() != second.getChessboardPoint().getY()) {
                return ActionType.EAT_PASS_PAWN;
            } else if (second.getChessboardPoint().getX() == 0 || second.getChessboardPoint().getX() == 7) {
                return ActionType.UPGRADE_PAWN;
            }
        } else if (first instanceof KingChessComponent) {
            if (Math.abs(first.getChessboardPoint().getY() - second.getChessboardPoint().getY()) > 1) {
                return ActionType.EXCHANGE_KING_AND_ROOK;
            }
        }

        return ActionType.NONE;
    }

    private void handleAction(ChessComponent chessComponent, ActionType actionType) {
        if (actionType == ActionType.EAT_PASS_PAWN) {
            int inv = first.getChessColor() == ChessColor.BLACK ? -1 : 1;
            ChessComponent pawnChess = chessboard.getChessComponent(
                    chessComponent.getChessboardPoint().getX() + inv,
                    chessComponent.getChessboardPoint().getY());
            GroupUndoController groupUndoController = new GroupUndoController();
            SwitchUndoController switchUndoController = new SwitchUndoController(pawnChess,
                    new EmptySlotComponent(
                            pawnChess.getChessboardPoint(),
                            pawnChess.getLocation(),
                            pawnChess.getClickController(),
                            pawnChess.getSize().width),
                    chessboard);

            groupUndoController.addUndoController(switchUndoController);
            groupUndoController.addUndoController(new MoveUndoController(first, chessComponent, chessboard));
            undoManagerController.add(groupUndoController);
        } else if (actionType == ActionType.UPGRADE_PAWN) {
            switchChessDlg.setVisible(true);
            int switchType = switchChessDlg.getClickType();
            System.out.println("click type is " + switchType);
            ChessComponent newChess = null;
            switch (switchType) {
                case 1:
                    newChess = new RookChessComponent(chessComponent.getChessboardPoint(),
                            chessComponent.getLocation(),
                            first.getChessColor(),
                            chessComponent.getClickController(),
                            chessComponent.getSize().width);
                    break;
                case 2:
                    newChess = new KnightChessComponent(chessComponent.getChessboardPoint(),
                            chessComponent.getLocation(),
                            first.getChessColor(),
                            chessComponent.getClickController(),
                            chessComponent.getSize().width);
                    break;
                case 3:
                    newChess = new BishopChessComponent(chessComponent.getChessboardPoint(),
                            chessComponent.getLocation(),
                            first.getChessColor(),
                            chessComponent.getClickController(),
                            chessComponent.getSize().width);
                    break;
                case 4:
                    newChess = new QueenChessComponent(chessComponent.getChessboardPoint(),
                            chessComponent.getLocation(),
                            first.getChessColor(),
                            chessComponent.getClickController(),
                            chessComponent.getSize().width);
                    break;
            }

            GroupUndoController groupUndoController = new GroupUndoController();
            groupUndoController.addUndoController(new MoveUndoController(first, chessComponent, chessboard));
            SwitchUndoController switchUndoController = new SwitchUndoController(chessComponent, newChess, chessboard);
            groupUndoController.addUndoController(switchUndoController);
            undoManagerController.add(groupUndoController);
        } else if (actionType == ActionType.EXCHANGE_KING_AND_ROOK) {
            GroupUndoController groupUndoController = new GroupUndoController();
            int secondY = chessComponent.getChessboardPoint().getY();
            int rookY = 0;
            int emptyY = 0;
            if (secondY > first.getChessboardPoint().getY()) {
                rookY = first.getChessboardPoint().getY() + 3;
                emptyY = first.getChessboardPoint().getY() + 1;
            } else {
                rookY = first.getChessboardPoint().getY() - 4;
                emptyY = first.getChessboardPoint().getY() - 1;
            }
            ChessComponent rook = chessboard.getChessComponent(first.getChessboardPoint().getX(), rookY);
            if (!(rook instanceof RookChessComponent)) {
                System.out.println("王车易位 未找到车");
            } else {
                ChessComponent emptyChekk = chessboard.getChessComponent(first.getChessboardPoint().getX(), emptyY);
                groupUndoController.addUndoController(new MoveUndoController(rook, emptyChekk, chessboard));
            }
            groupUndoController.addUndoController(new MoveUndoController(first, chessComponent, chessboard));
            undoManagerController.add(groupUndoController);

        } else {
            undoManagerController.add(new MoveUndoController(first, chessComponent, chessboard));
        }
    }
}
