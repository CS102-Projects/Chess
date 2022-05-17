package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;

import java.util.ArrayList;

public class ChessEnablePathController {
    private static Chessboard chessboard = null;

    public ChessEnablePathController(Chessboard c) {
        chessboard = c;
    }

    public static boolean canMoveTo(ChessComponent srcChess, ChessboardPoint destPoint) {
        if (chessboard == null) {
            return false;
        }
        ChessEnablePathController chessEnablePathController = new ChessEnablePathController(chessboard);
        ArrayList<ChessComponent> components = chessEnablePathController.getEnablePath(srcChess);
        ChessComponent component = chessboard.getChessComponent(destPoint);
        return components.contains(component);
    }

    // 获取保卫king的路径
    public static ArrayList<ChessboardPoint> getDefendPath(ChessComponent chessComponent) {
        ArrayList<ChessboardPoint> result = new ArrayList<>();

        ChessComponent king = chessboard.getKing(chessComponent.getChessColor());
        MoveDirection moveDirection = getProtectDirection(chessComponent, king);
        if (moveDirection == MoveDirection.NONE) {
            return result;
        }

        // 判断和king之间是否有棋子
        ArrayList<ChessboardPoint> protectPath = getMovePath(chessComponent, king);
        int cnt = 0;
        for (ChessboardPoint point : protectPath) {
            if (!(chessboard.getChessComponent(point) instanceof EmptySlotComponent)) {
                ++cnt;
            }
        }
        if (cnt > 1) { // cnt数量包含了 king ，所以最少为 1
            return result;
        }

        // 判断路线上是否有敌人可能攻击king
        ChessComponent enemy = getAttacker(chessComponent, moveDirection);
        if (enemy != null) {
            return getMovePath(chessComponent, enemy);
        }
        return result;
    }

    // 获取安全的行走路线
    public static ArrayList<ChessboardPoint> getSafePath(ChessComponent chessComponent) {
        ArrayList<ChessboardPoint> result = new ArrayList<>();

        // 获取所有可走的路线
        ArrayList<ChessComponent> enablePath = getEnablePath(chessComponent);
        if (enablePath == null || enablePath.isEmpty()) {
            return result;
        }

        // 获取王的状态
        ArrayList<ChessComponent> enablePath2 = new ArrayList<>();
        ChessComponent king = chessboard.getKing(chessComponent.getChessColor());
        if (chessComponent == king) { // 如果选择的是王，则不必考虑安全路径
            for (ChessComponent point : enablePath) {
                result.add(point.getChessboardPoint());
            }
            return result;
        }

        // 获取正在攻击王的敌人
        ArrayList<ChessComponent> enemies = getAttackers(king);
        if (enemies.size() >= 2) { // 如果有2个以上的攻击者在攻击king，则只能移动king
            return result;
        }
        else if (enemies.size() == 1) { // 如果只有一个攻击者，则必须阻断敌方攻击
            ArrayList<ChessboardPoint> defensePath = getMovePath(king, enemies.get(0));
            if (defensePath.isEmpty()) { // 没有防御路线，说明攻击者是 马或者兵或者king
                enablePath2.add(enemies.get(0));
            } else {
                for (ChessComponent component : enablePath) {
                    for (ChessboardPoint defensePoint : defensePath) {
                        if (component.getChessboardPoint().equal(defensePoint)) {
                            enablePath2.add(component);
                        }
                    }
                }
            }
        } else {
            enablePath2 = enablePath;
        }


        // 获取正在保护king的路线
        ArrayList<ChessboardPoint> defendPath = getDefendPath(chessComponent);
        if (defendPath.isEmpty()) {
            for (ChessComponent component : enablePath2) {
                result.add(new ChessboardPoint(component.getChessboardPoint().getX(), component.getChessboardPoint().getY()));
            }
        } else {
            for (ChessComponent component : enablePath2) {
                for (ChessboardPoint point : defendPath) {
                    if (component.getChessboardPoint().equal(point)) {
                        result.add(new ChessboardPoint(component.getChessboardPoint().getX(), component.getChessboardPoint().getY()));
                    }
                }
            }
        }



        return result;
    }

    public static ArrayList<ChessComponent> getEnablePath(ChessComponent chessComponent) {
        return getEnablePath(chessComponent, false);
    }
    public static ArrayList<ChessComponent> getEnablePath(ChessComponent chessComponent, boolean ignoreHit) {

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

    // 获取保护方向
    public static MoveDirection getProtectDirection(ChessComponent guardian, ChessComponent protege) {
        return MoveDirection.getMoveDirection(protege.getChessboardPoint(), guardian.getChessboardPoint());
    }

    // 获取指定方向的攻击者
    public static ChessComponent getAttacker(ChessComponent chessComponent, MoveDirection direction) {
        int x = chessComponent.getChessboardPoint().getX();
        int y = chessComponent.getChessboardPoint().getY();
        ChessboardPoint movePoint = MoveDirection.getMovePoint(direction);
        while (true) {
            x += movePoint.getX();
            y += movePoint.getY();
            if (x < 0 || x > 7 || y < 0 || y > 7) {
                break;
            }

            ChessComponent destChess = chessboard.getChessComponent(x, y);

            if (destChess instanceof EmptySlotComponent) {
                continue;
            }

            if (destChess.getChessColor() == chessComponent.getChessColor()) {
                return null;
            }

            switch (direction)
            {
                case LEFT:
                case RIGHT:
                case TOP:
                case BOTTOM:
                    // 女王和车才能水平和垂直攻击
                    if (destChess instanceof QueenChessComponent || destChess instanceof RookChessComponent) {
                        return destChess;
                    }
                    break;
                case LEFT_BOTTOM:
                case LEFT_TOP:
                case RIGHT_BOTTOM:
                case RIGHT_TOP:
                    // 女王和相才能对角攻击
                    if (destChess instanceof QueenChessComponent || destChess instanceof BishopChessComponent) {
                        return destChess;
                    }
                    break;
            }
            return null;
        }

        return null;
    }

    public static ArrayList<ChessboardPoint> getMovePath(ChessComponent from, ChessComponent to) {
        return getMovePath(from.getChessboardPoint(), to.getChessboardPoint());
    }

    public static ArrayList<ChessboardPoint> getMovePath(ChessboardPoint from, ChessboardPoint to) {
        ArrayList<ChessboardPoint> result = new ArrayList<>();

        MoveDirection moveDirection = MoveDirection.getMoveDirection(from, to);
        if (moveDirection == MoveDirection.NONE) {
            return result;
        }
        ChessboardPoint movePoint = MoveDirection.getMovePoint(moveDirection);

        int x1 = from.getX();
        int y1 = from.getY();
        int x2 = to.getX();
        int y2 = to.getY();

        while (x1 != x2 || y1 != y2) {
            x1 += movePoint.getX();
            y1 += movePoint.getY();
            result.add(new ChessboardPoint(x1, y1));
        }

        return result;
    }

    // 获取攻击chessComponent的敌人
    public static ArrayList<ChessComponent> getAttackers(ChessComponent victim) {
        ArrayList<ChessComponent> arrayList = new ArrayList<>();

        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        for (int i = 0; i < chessComponents.length; ++i) {
            for (int j = 0; j < chessComponents[i].length; ++j) {
                ChessComponent component = chessComponents[i][j];
                if (component instanceof EmptySlotComponent || component.getChessColor() == victim.getChessColor()) {
                    continue;
                }
                if (component.canMoveTo(chessComponents, victim.getChessboardPoint())) {
                    arrayList.add(component);
                }
            }
        }

        return arrayList;
    }

    private static ArrayList<ChessComponent> getBishopNextPath(ChessComponent chessComponent) {
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

    private static ArrayList<ChessComponent> getKingNextPath(ChessComponent chessComponent, boolean ignoreHit) {
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


    private static boolean isHitByEnemy(ChessComponent chessComponent, ChessComponent nextStepChess) {
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
                if (enemyEnablePath != null && enemyEnablePath.contains(chessComponent)) {
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

    private static ArrayList<ChessComponent> getKnightNextPath(ChessComponent chessComponent) {
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

    private static ArrayList<ChessComponent> getPawnNextPath(ChessComponent chessComponent) {
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

    private static ArrayList<ChessComponent> getQueenNextPath(ChessComponent chessComponent) {
        ArrayList<ChessComponent> result = new ArrayList<>();
        result = getBishopNextPath(chessComponent);
        result.addAll(getRookNextPath(chessComponent));
        return result;
    }

    private static ArrayList<ChessComponent> getRookNextPath(ChessComponent chessComponent) {
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
