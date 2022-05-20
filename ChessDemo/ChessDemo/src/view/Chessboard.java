package view;


import controller.ChessEnablePathController;
import controller.ClickController;
import controller.StatusController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类表示面板上的棋盘组件对象
 */
public class Chessboard extends JComponent {
    /**
     * CHESSBOARD_SIZE： 棋盘是8 * 8的
     * <br>
     * BACKGROUND_COLORS: 棋盘的两种背景颜色
     * <br>
     * chessListener：棋盘监听棋子的行动
     * <br>
     * chessboard: 表示8 * 8的棋盘
     * <br>
     * currentColor: 当前行棋方
     */
    private static final int CHESSBOARD_SIZE = 8;

    private final ChessComponent[][] chessComponents = new ChessComponent[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    public static ChessColor currentColor = ChessColor.WHITE;
    //all chessComponents in this chessboard are shared only one model controller
    private final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;

    private ChessEnablePathController chessEnablePathController;
    private StatusController statusController;

    public Chessboard(int width, int height) {
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        CHESS_SIZE = width / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);

        chessEnablePathController = new ChessEnablePathController(this);
        statusController = new StatusController(chessEnablePathController);

        reset();
    }

    public void reset()
    {
        initiateEmptyChessboard();
        // FIXME: Initialize chessboard for testing only.
        initRookOnBoard(0, 0, ChessColor.BLACK);
        initRookOnBoard(CHESSBOARD_SIZE - 1, 0, ChessColor.WHITE);
        initRookOnBoard(CHESSBOARD_SIZE - 1, CHESSBOARD_SIZE - 1, ChessColor.WHITE);
        initRookOnBoard(0, CHESSBOARD_SIZE - 1, ChessColor.BLACK);
        initKingOnBoard(0, 4, ChessColor.BLACK);
        initKingOnBoard(7, 4, ChessColor.WHITE);
        initBishopOnBoard(0, 2, ChessColor.BLACK);
        initBishopOnBoard(0, 5, ChessColor.BLACK);
        initBishopOnBoard(7, 2, ChessColor.WHITE);
        initBishopOnBoard(7, 5, ChessColor.WHITE);
        initKnightOnBoard(0, 1, ChessColor.BLACK);
        initKnightOnBoard(0, 6, ChessColor.BLACK);
        initKnightOnBoard(7, 1, ChessColor.WHITE);
        initKnightOnBoard(7, 6, ChessColor.WHITE);
        initQueenOnBoard(0, 3, ChessColor.BLACK);
        initQueenOnBoard(CHESSBOARD_SIZE-1, 3, ChessColor.WHITE);
        for (int i = 0; i < CHESSBOARD_SIZE; i++) {
            initPawnOnBoard(1,i, ChessColor.BLACK);
            initPawnOnBoard(CHESSBOARD_SIZE-2,i, ChessColor.WHITE);
        }

        for (ChessComponent[] chessComponent : chessComponents) {
            for (ChessComponent component : chessComponent) {
                component.repaint();
            }
        }

        currentColor = ChessColor.WHITE;
        clickController.clear();
    }

    public void clearStatu() {
        clickController.clear();
    }

    public void moveChess(ChessboardPoint from, ChessboardPoint to, int switchType) {
        clickController.moveChessByFile(from, to, switchType);
    }

    public void clearPath() {
        ChessComponent[][] components = getChessComponents();
        for (ChessComponent[] component : components) {
            for (ChessComponent chessComponent : component) {
                chessComponent.setShowType(0);
                chessComponent.repaint();
            }
        }
    }

    public void showEnablePath(ChessComponent chessComponent) {
        clearPath();

        ArrayList<ChessComponent> path = chessEnablePathController.getEnablePath(chessComponent);
        ArrayList<ChessboardPoint> safePath = chessEnablePathController.getSafePath(chessComponent);
//        ArrayList<ChessboardPoint> defendPath = chessEnablePathController.getDefendPath(chessComponent);

        for (ChessComponent chess: path) {
            if (chess != null) {
                boolean isSafePoint = false;
                for (ChessboardPoint point : safePath) {
                    if (chess.getChessboardPoint().equal(point)) {
                        isSafePoint = true;
                        break;
                    }
                }
                if (isSafePoint) {
                    chess.setShowType(1);
                } else {
                    chess.setShowType(2);
                }

                chess.repaint();
            }
        }



    }
    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public ChessComponent getChessComponent(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null;
        }
        return chessComponents[row][col];
    }

    public ChessComponent getChessComponent(ChessboardPoint point) {
        return getChessComponent(point.getX(), point.getY());
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public ChessComponent getKing(ChessColor color) {
        for (int i = 0; i < chessComponents.length; ++i) {
            for (int j = 0; j < chessComponents[i].length; ++j) {
                if (chessComponents[i][j].getChessColor() == color &&
                chessComponents[i][j] instanceof KingChessComponent) {
                    return chessComponents[i][j];
                }
            }
        }

        return null;
    }

    public void putChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();

        if (chessComponents[row][col] != null) {
            remove(chessComponents[row][col]);
        }
        add(chessComponents[row][col] = chessComponent);
    }

    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, CHESS_SIZE));
        }
        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;

        chess1.repaint();
        chess2.repaint();
    }

    public void pureSwapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;
    }

    public void undoChessComponents(ChessComponent chess1, ChessComponent chess2) {
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        remove(chessComponents[row1][col1]);
        add(chess1);
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        remove(chessComponents[row2][col2]);
        add(chess2);
        chessComponents[row2][col2] = chess2;

        chess1.repaint();
        chess2.repaint();
    }

    public ArrayList<ChessboardPoint> getEnablePoints(ChessComponent chessComponent) {
        ArrayList<ChessboardPoint> result = new ArrayList<>();



        return result;
    }

    public boolean checkChess(ChessComponent chess1) {
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        if (chessComponents[row1][col1] != chess1) {
            return false;
        }
        return true;
    }

    public void initiateEmptyChessboard() {
        for (int i = 0; i < chessComponents.length; i++) {
            for (int j = 0; j < chessComponents[i].length; j++) {
                putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController, CHESS_SIZE));
            }
        }
    }

    public void swapColor() {
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    private void initRookOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new RookChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }
    private void initKingOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KingChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }
    private void initBishopOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new BishopChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }
    private void initKnightOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KnightChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }
    private void initQueenOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new QueenChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initPawnOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new PawnChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    public void loadGame(List<String> chessData) {
        chessData.forEach(System.out::println);
    }

    public StatusController getStatusController() {
        return statusController;
    }
}
