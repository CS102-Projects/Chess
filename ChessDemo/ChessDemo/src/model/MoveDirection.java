package model;

import view.ChessboardPoint;

public enum MoveDirection {
    NONE(0), TOP(1), BOTTOM(2), LEFT(3), RIGHT(4), LEFT_TOP(5), LEFT_BOTTOM(6), RIGHT_TOP(7), RIGHT_BOTTOM(8);

    MoveDirection(int type) {
    }

    public static MoveDirection getMoveDirection(ChessboardPoint point1, ChessboardPoint point2) {
        int x1 = point1.getX();
        int y1 = point1.getY();
        int x2 = point2.getX();
        int y2 = point2.getY();
        if (x1 == x2 && y1 == y2) {
            return NONE;
        }
        if (x1 == x2 && y1 < y2) {
            return RIGHT;
        }
        if (x1 == x2) {
            return LEFT;
        }
        if (y1 == y2 && x1 < x2) {
            return BOTTOM;
        }
        if (y1 == y2) {
            return TOP;
        }
        if (Math.abs(x1-x2) == Math.abs(y1-y2)) {
            if (x1 < x2 && y1 < y2) {
                return RIGHT_BOTTOM;
            }
            if (x1 < x2) {
                return LEFT_BOTTOM;
            }
            if (y1 < y2) {
                return RIGHT_TOP;
            }
            return LEFT_TOP;
        }
        return NONE;
    }

    public static ChessboardPoint getMovePoint(MoveDirection direction) {
        return switch (direction) {
            case TOP -> new ChessboardPoint(-1, 0);
            case BOTTOM -> new ChessboardPoint(1, 0);
            case LEFT -> new ChessboardPoint(0, -1);
            case RIGHT -> new ChessboardPoint(0, 1);
            case LEFT_TOP -> new ChessboardPoint(-1, -1);
            case LEFT_BOTTOM -> new ChessboardPoint(1, -1);
            case RIGHT_TOP -> new ChessboardPoint(-1, 1);
            case RIGHT_BOTTOM -> new ChessboardPoint(1, 1);
            default -> new ChessboardPoint(0, 0);
        };
    }
}
