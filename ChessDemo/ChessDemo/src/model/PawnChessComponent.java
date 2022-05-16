package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示国际象棋里面的车
 */
public class PawnChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image PAWN_WHITE;
    private static Image PAWN_BLACK;

    /**
     * 车棋子对象自身的图片，是上面两种中的一种
     */
    private Image pawnImage;

    /**
     * 读取加载车棋子的图片
     *
     */
    public void loadResource() throws IOException {
        if (PAWN_WHITE == null) {
            PAWN_WHITE = ImageIO.read(new File("./ChessDemo/ChessDemo/images/pawn-white.png"));
        }
        if (PAWN_BLACK == null) {
            PAWN_BLACK = ImageIO.read(new File("./ChessDemo/ChessDemo/images/pawn-black.png"));
        }

    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiatePawnImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                pawnImage = PAWN_WHITE;
            } else if (color == ChessColor.BLACK) {
                pawnImage = PAWN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PawnChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiatePawnImage(color);
    }

    @Override
    public ChessComponent clone()
    {
        return new PawnChessComponent(getChessboardPoint(), getLocation(), getChessColor(), clickController, getSize().width);
    }

    private boolean isFirstStep() {
        if (getChessColor() == ChessColor.BLACK) {
            return getChessboardPoint().getX() == 1;
        } else {
            return getChessboardPoint().getX() == 6;
        }
    }

    public boolean hasBarrier(ChessComponent[][] chessComponents, int curX, int curY, int destX, int destY)
    {
        for (int col = Math.min(curX, destX) + 1; col < Math.max(curX, curX); col++) {
            if (!(chessComponents[col][curY] instanceof EmptySlotComponent)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 车棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 车棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        int x1 = source.getX();
        int y1 = source.getY();
        int x2 = destination.getX();
        int y2 = destination.getY();
        if (y1 == y2) {
            int inv = x2 - x1;
            if (Math.abs(inv) > 2) {
                return false;
            }
            if (Math.abs(inv) > 1) {
                if (!isFirstStep()) {
                    return false;
                }
            }

            if (getChessColor() == ChessColor.BLACK) {
                if (x2 < x1) {
                    return false;
                }
            } else {
                if (x2 > x1) {
                    return false;
                }
            }

            if (hasBarrier(chessComponents, x1, y1, x2, y2)) {
                return false;
            }
        } else {
            int inv = x2 - x1;
            if (Math.abs(inv) != 1) {
                return false;
            }
            if (chessComponents[x2][y2].getChessColor() == getChessColor()) {
                return false;
            }

            if (chessComponents[x2][y2].getChessColor() == ChessColor.NONE) {
                int yy = 0;
                if (getChessColor() == ChessColor.BLACK) {
                    yy = y2 - 1;
                } else {
                    yy = y2 + 1;
                }

                if (yy < 0 || yy > 7) {
                    return false;
                }

                ChessComponent chess = chessComponents[x2][yy];
                if (chess.getChessColor() == getChessColor() ||
                        !(chess instanceof PawnChessComponent) ||
                        (chess.getCurStep() != chess.getStepCount() - 1)
                ) {
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(pawnImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }
}

