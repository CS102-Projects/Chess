package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;

import java.util.ArrayList;

public class ChessEnablePathController {
    private static Chessboard s_chessboard = null;
    private Chessboard chessboard;

    public ChessEnablePathController(Chessboard chessboard) {

        s_chessboard = chessboard;
        this.chessboard = chessboard;
    }

    public static boolean canMoveTo(ChessComponent srcChess, ChessboardPoint destPoint) {
        if (s_chessboard == null) {
            return false;
        }
        ChessEnablePathController chessEnablePathController = new ChessEnablePathController(s_chessboard);
        ArrayList<ChessComponent> components = chessEnablePathController.getEnablePath(srcChess);
        ChessComponent component = s_chessboard.getChessComponent(destPoint);
        return components.contains(component);
    }

    public ArrayList<ChessComponent> getEnablePath(ChessComponent chessComponent) {
        return getEnablePath(chessComponent, false);
    }
    public ArrayList<ChessComponent> getEnablePath(ChessComponent chessComponent, boolean ignoreHit) {

        if (chessComponent instanceof BishopChessComponent) {
            return getBishopNextPath(chessComponent);
        } else if (chessComponent instanceof KingChessComponent) {
            return getKingNextPath(chessComponent, ignoreHit);
        } else if (chessComponent instanceof KnightChessComponent) {
            return getKnightNextPath(chessComponent);
        } else if (chessComponent instanceof PawnChessComponent) {
            return getPawnNextPath(chessComponent);
        } else if (chessComponent instanceof QueenChessComponent) {
            return getQueenNextPath(chessComponent);
        } else if (chessComponent instanceof RookChessComponent) {
            return getRookNextPath(chessComponent);
        }

        return null;
    }

    private ArrayList<ChessComponent> getBishopNextPath(ChessComponent chessComponent) {
        ArrayList<ChessComponent> result = new ArrayList<>();

        int[]  moveList = {1, 1, 1, -1, -1, 1, -1, -1};

        for (int i = 0; i < moveList.length; i+=2) {
            int moveX = moveList[i];
            int moveY = moveList[i+1];
            int x = chessComponent.getChessboardPoint().getX();
            int y = chessComponent.getChessboardPoint().getY();
            while (true) {
                x += moveX; y+=moveY;
                if (x < 0 || x > 7 || y < 0 || y > 7) {
                    break;
                }
                if (chessboard.getChessComponent(x, y).getChessColor() != chessComponent.getChessColor()) {
                    result.add(chessboard.getChessComponent(x, y));
                    if (chessboard.getChessComponent(x, y).getChessColor() != ChessColor.NONE) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return result;
    }

    private ArrayList<ChessComponent> getKingNextPath(ChessComponent chessComponent, boolean ignoreHit) {
        ArrayList<ChessComponent> result = new ArrayList<>();

        int[]  moveList = {1, 0, 1, 1, 1, -1, 0, 1, 0, -1, -1, 1, -1, 0, -1, -1};

        for (int i = 0; i < moveList.length; i+=2) {
            int moveX = moveList[i];
            int moveY = moveList[i+1];
            int x = chessComponent.getChessboardPoint().getX();
            int y = chessComponent.getChessboardPoint().getY();

            x += moveX; y+=moveY;
            if (x < 0 || x > 7 || y < 0 || y > 7) {
                continue;
            }
            if (chessboard.getChessComponent(x, y).getChessColor() != chessComponent.getChessColor()) {
                if (ignoreHit || !isHitByEnemy(chessComponent, chessboard.getChessComponent(x, y))) {
                    result.add(chessboard.getChessComponent(x, y));
                }
            }
        }

        // 王车易位
        // 王必须是第一次走
        if (chessComponent.getStepCount() > 0) {
            return result;
        }
        int x = chessComponent.getChessboardPoint().getX();
        int y = chessComponent.getChessboardPoint().getY();

        // 短易位
        ArrayList<ChessComponent> shortEnablePath = new ArrayList<>();
        ChessComponent rook1 = chessboard.getChessComponent(x, y+3);
        // 右车没走过
        if ((rook1 instanceof RookChessComponent) && rook1.getStepCount() == 0) {

            // 判断王和右车之间是否有障碍
            int i = 1;
            for (; i < 3; ++i) {
                if (!(chessboard.getChessComponent(x, y+i) instanceof EmptySlotComponent)) {
                    break;
                }
            }

            if (i == 3) { // 王和右车之间没有障碍
                if (ignoreHit || !isHitByEnemy(chessComponent, chessboard.getChessComponent(x, y+2)))
                {
                    result.add(chessboard.getChessComponent(x, y+2));
                }
            }
        }

        // 长易位
        ArrayList<ChessComponent> longEnablePath = new ArrayList<>();
        ChessComponent rook2 = chessboard.getChessComponent(x, y-4);
        // 左车没走过
        if ((rook2 instanceof RookChessComponent) && rook2.getStepCount() == 0) {

            // 判断王和左车之间是否有障碍
            int i = 1;
            for (; i < 4; ++i) {
                ChessComponent tmpChess = chessboard.getChessComponent(x, y-i);
                if (!(tmpChess instanceof EmptySlotComponent)) {
                    break;
                }
            }

            if (i == 4) { // 王和左车之间没有障碍
                if (ignoreHit || !isHitByEnemy(chessComponent, chessboard.getChessComponent(x, y-2))) {
                    result.add(chessboard.getChessComponent(x, y-2));
                }
            }
        }

        return result;

    }


    private boolean isHitByEnemy(ChessComponent chessComponent, ChessComponent nextStepChess) {
        EmptySlotComponent emptySlotComponent = new EmptySlotComponent(nextStepChess.getChessboardPoint(),
                nextStepChess.getLocation(),
                nextStepChess.getClickController(),
                nextStepChess.getSize().width);

        chessboard.putChessOnBoard(emptySlotComponent);
        chessboard.pureSwapChessComponents(chessComponent, emptySlotComponent);

        ChessComponent[][] components = chessboard.getChessComponents();
        for (int k = 0; k < components.length; ++k) {
            for (int j = 0; j < components[k].length; ++j) {
                if (components[k][j] instanceof EmptySlotComponent) {
                    continue;
                }

                if (components[k][j].getChessColor() == chessComponent.getChessColor()) {
                    continue;
                }

                // 获取敌方棋子
                ChessComponent enemyChess = components[k][j];

                // 判断对方棋子是否将住该位置
                ArrayList<ChessComponent> enemyEnablePath = getEnablePath(enemyChess, true);
                if (enemyEnablePath.contains(chessComponent)) {
                    chessboard.pureSwapChessComponents(chessComponent, emptySlotComponent);
                    chessboard.putChessOnBoard(nextStepChess);
                    return true;
                }
            }
        }

        chessboard.pureSwapChessComponents(chessComponent, emptySlotComponent);
        chessboard.putChessOnBoard(nextStepChess);

        return false;
    }

    private ArrayList<ChessComponent> getKnightNextPath(ChessComponent chessComponent) {
        ArrayList<ChessComponent> result = new ArrayList<>();
        int[]  moveList = {2, 1, 2, -1, -2, 1, -2, -1, 1, 2, 1, -2, -1, 2, -1, -2};

        for (int i = 0; i < moveList.length; i+=2) {
            int moveX = moveList[i];
            int moveY = moveList[i+1];
            int x = chessComponent.getChessboardPoint().getX();
            int y = chessComponent.getChessboardPoint().getY();

            x += moveX; y+=moveY;
            if (x < 0 || x > 7 || y < 0 || y > 7) {
                continue;
            }
            if (chessboard.getChessComponent(x, y).getChessColor() != chessComponent.getChessColor()) {
                result.add(chessboard.getChessComponent(x, y));
            }
        }

        return result;
    }

    private ArrayList<ChessComponent> getPawnNextPath(ChessComponent chessComponent) {
        ArrayList<ChessComponent> result = new ArrayList<>();
        int invX = chessComponent.getChessColor() == ChessColor.BLACK ? 1 : -1;
        int curX = chessComponent.getChessboardPoint().getX();
        int curY = chessComponent.getChessboardPoint().getY();

        ChessboardPoint p1 = new ChessboardPoint(curX+invX, curY);
        if (chessComponent.canMoveTo(chessboard.getChessComponents(), p1)) {
            result.add(chessboard.getChessComponent(p1));
        }

        ChessboardPoint p2 = new ChessboardPoint(curX+invX, curY+1);
        if (chessComponent.canMoveTo(chessboard.getChessComponents(), p2)) {
            result.add(chessboard.getChessComponent(p2));
        }

        ChessboardPoint p3 = new ChessboardPoint(curX+invX, curY-1);
        if (chessComponent.canMoveTo(chessboard.getChessComponents(), p3)) {
            result.add(chessboard.getChessComponent(p3));
        }

        ChessboardPoint p4 = new ChessboardPoint(curX+invX+invX, curY);
        if (chessComponent.canMoveTo(chessboard.getChessComponents(), p4)) {
            result.add(chessboard.getChessComponent(p4));
        }

        return result;
    }

    private ArrayList<ChessComponent> getQueenNextPath(ChessComponent chessComponent) {
        ArrayList<ChessComponent> result = new ArrayList<>();
        result = getBishopNextPath(chessComponent);
        result.addAll(getRookNextPath(chessComponent));
        return result;
    }

    private ArrayList<ChessComponent> getRookNextPath(ChessComponent chessComponent) {
        ArrayList<ChessComponent> result = new ArrayList<>();
        int[]  moveList = {1, 0, -1, 0, 0, 1, 0, -1};

        for (int i = 0; i < moveList.length; i+=2) {
            int moveX = moveList[i];
            int moveY = moveList[i+1];
            int x = chessComponent.getChessboardPoint().getX();
            int y = chessComponent.getChessboardPoint().getY();
            while (true) {
                x += moveX; y+=moveY;
                if (x < 0 || x > 7 || y < 0 || y > 7) {
                    break;
                }
                if (chessboard.getChessComponent(x, y).getChessColor() != chessComponent.getChessColor()) {
                    result.add(chessboard.getChessComponent(x, y));
                    if (chessboard.getChessComponent(x, y).getChessColor() != ChessColor.NONE) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return result;
    }

}
