package controller;

import model.*;
import view.ChessGameFrame;
import view.Chessboard;
import view.ChessboardPoint;
import view.SwitchChessDialog;

import javax.swing.*;
import java.awt.*;
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

    public void clear() {
        first = null;
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
                moveChess(first.getChessboardPoint(), chessComponent.getChessboardPoint());
                first.setSelected(false);
                first = null;
            }
        }
    }

    public void moveChess(ChessboardPoint from, ChessboardPoint to) {
        ChessComponent chess1 = chessboard.getChessComponent(from);
        ChessComponent chess2 = chessboard.getChessComponent(to);

        ActionType actionType = calcActionType(chess1, chess2);
        handleAction(chess1, chess2, actionType);

        move(chess1);
    }

    private void move(ChessComponent chess1) {
        ChessColor enemyColor = chess1.getChessColor().getColor()== Color.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
        if (chessboard.getStatusController().isHitKing(chess1.getChessColor(), chessboard)) {
            ChessComponent enemyKing = chessboard.getKing(enemyColor);
            enemyKing.setShowType(3); // 被攻击
            System.out.println("--attack king--");

            if (chessboard.getStatusController().isDead(enemyColor, chessboard)) {
                System.out.println( chess1.getChessColor().toString() + " is win");
                int res= JOptionPane.showConfirmDialog(null, chess1.getChessColor().toString() + " is win \n 是否重新开始", "WIN"   , JOptionPane.YES_NO_OPTION);
                if(res==JOptionPane.YES_OPTION){
                    chessboard.reset();
                    UndoManagerController.getInstance().clear();
                }
            }
        }
        if (chessboard.getStatusController().isCanNotMoveDraw(enemyColor, chessboard)) {
            System.out.println( "Draw");
            int res= JOptionPane.showConfirmDialog(null,  " is Draw \n 是否重新开始", "DRAW"   , JOptionPane.YES_NO_OPTION);
            if(res==JOptionPane.YES_OPTION){
                chessboard.reset();
                UndoManagerController.getInstance().clear();
            }
        }
        chessboard.swapColor();
        ChessGameFrame.currentPlayerLabel.setText(String.valueOf(Chessboard.currentColor));
    }

    public void moveChessByFile(ChessboardPoint from, ChessboardPoint to, int switchType) {
        ChessComponent chess1 = chessboard.getChessComponent(from);
        ChessComponent chess2 = chessboard.getChessComponent(to);

        ActionType actionType = calcActionType(chess1, chess2);
        handleActionByFile(chess1, chess2, actionType, switchType);

        move(chess1);
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
        if (chessComponent.getChessColor() == chessboard.getCurrentColor() ||
                !first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint())) {
            return false;
        }
        // 如果这步走出去后 king 被将军，则不允许走这步
        ArrayList<ChessboardPoint> safePath = ChessEnablePathController.getSafePath(first);
        for (ChessboardPoint point : safePath) {
            if (chessComponent.getChessboardPoint().equal(point)) {
                return true;
            }
        }
        return false;
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

    private void handleAction(ChessComponent firstChess, ChessComponent chessComponent, ActionType actionType) {
        handleActionByFile(firstChess, chessComponent, actionType, 0);
    }

    private void handleActionByFile(ChessComponent firstChess, ChessComponent chessComponent, ActionType actionType, int switchType) {
        if (actionType == ActionType.EAT_PASS_PAWN) {
            int inv = firstChess.getChessColor() == ChessColor.BLACK ? -1 : 1;
            ChessComponent pawnChess = chessboard.getChessComponent(
                    chessComponent.getChessboardPoint().getX() + inv,
                    chessComponent.getChessboardPoint().getY());
            GroupUndoController groupUndoController = new GroupUndoController();
            SwitchUndoController switchUndoController = new SwitchUndoController(pawnChess, new EmptySlotComponent(pawnChess.getChessboardPoint(), pawnChess.getLocation(), pawnChess.getClickController(), pawnChess.getSize().width), chessboard);

            groupUndoController.addUndoController(switchUndoController);
            groupUndoController.addUndoController(new MoveUndoController(firstChess, chessComponent, chessboard));
            undoManagerController.add(groupUndoController);
//            ChessGameFrame.record.append("\n"+ groupUndoController);
        } else if (actionType == ActionType.UPGRADE_PAWN) {
            if (switchType == 0) {
                switchChessDlg.setVisible(true);
                switchType = switchChessDlg.getClickType();
            }

            System.out.println("click type is " + switchType);

            GroupUndoController groupUndoController = new GroupUndoController();
            int newX = chessComponent.getChessboardPoint().getX();
            int newY = chessComponent.getChessboardPoint().getY();
            groupUndoController.addUndoController(new MoveUndoController(firstChess, chessComponent, chessboard));
            ChessComponent newFirst = chessboard.getChessComponent(newX, newY);
            SwitchUndoController switchUndoController = new SwitchUndoController(newFirst, switchType, chessboard);
            groupUndoController.addUndoController(switchUndoController);
            undoManagerController.add(groupUndoController);
            ChessGameFrame.record.append("\n" + groupUndoController);
        } else if (actionType == ActionType.EXCHANGE_KING_AND_ROOK) {
            GroupUndoController groupUndoController = new GroupUndoController();
            int secondY = chessComponent.getChessboardPoint().getY();
            int rookY = 0;
            int emptyY = 0;
            if (secondY > firstChess.getChessboardPoint().getY()) {
                rookY = firstChess.getChessboardPoint().getY() + 3;
                emptyY = firstChess.getChessboardPoint().getY() + 1;
            } else {
                rookY = firstChess.getChessboardPoint().getY() - 4;
                emptyY = firstChess.getChessboardPoint().getY() - 1;
            }
            ChessComponent rook = chessboard.getChessComponent(firstChess.getChessboardPoint().getX(), rookY);
            if (!(rook instanceof RookChessComponent)) {
                System.out.println("王车易位 未找到车");
            } else {
                ChessComponent emptyChekk = chessboard.getChessComponent(firstChess.getChessboardPoint().getX(), emptyY);
                groupUndoController.addUndoController(new MoveUndoController(rook, emptyChekk, chessboard));
            }
            groupUndoController.addUndoController(new MoveUndoController(firstChess, chessComponent, chessboard));
            undoManagerController.add(groupUndoController);
            ChessGameFrame.record.append("\n" + groupUndoController);

        } else {
            MoveUndoController move = new MoveUndoController(firstChess, chessComponent, chessboard);
            undoManagerController.add(move);
            ChessGameFrame.record.append("\n" + move);
        }
    }
}
