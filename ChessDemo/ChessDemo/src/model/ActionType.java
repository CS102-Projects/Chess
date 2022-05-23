package model;

import java.awt.*;

public enum ActionType {
    NONE(0), EAT_PASS_PAWN(1), EXCHANGE_KING_AND_ROOK(2), UPGRADE_PAWN(3);

    private final int type;

    ActionType(int type) {
        this.type = type;
    }

}
