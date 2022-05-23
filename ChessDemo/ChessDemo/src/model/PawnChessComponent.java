package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            PAWN_WHITE = ImageIO.read(new File("D:/image_new/6.png"));
        }
        if (PAWN_BLACK == null) {
            PAWN_BLACK = ImageIO.read(new File("D:/image_new/12.png"));
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
        PawnChessComponent pawnChessComponent = new PawnChessComponent(getChessboardPoint(), getLocation(), getChessColor(), clickController, getSize().width);
        pawnChessComponent.curStep = this.curStep;
        pawnChessComponent.stepCount = this.stepCount;
        return pawnChessComponent;
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
        for (int col = Math.min(curX, destX) + 1; col < Math.max(curX, destX); col++) {
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

        if (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) {
            return false;
        }

        if (y1 == y2) { // 说明是在前进
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

            // 前方是否有其它棋子
            int frontX = getChessColor() == ChessColor.BLACK ? 1 : -1;
            if (hasBarrier(chessComponents, x1, y1, x2+frontX, y2)) {
                return false;
            }
        } else {
            int inv = x2 - x1;
            // 斜着只能前进一步
            if (Math.abs(inv) != 1) {
                return false;
            }
            // 不能吃自己方的棋子
            if (chessComponents[x2][y2].getChessColor() == getChessColor()) {
                return false;
            }
            // 不能后退
            if (getChessColor() == ChessColor.BLACK && inv < 0) {
                return false;
            }
            // 不能后退
            if (getChessColor() == ChessColor.WHITE && inv > 0) {
                return false;
            }

            // 如果落子点是空，进行吃过路兵判断
            if (chessComponents[x2][y2].getChessColor() == ChessColor.NONE) {

                // 找到过路兵的x坐标
                int xx = 0;
                if (getChessColor() == ChessColor.BLACK) {
                    xx = x2 - 1;
                } else {
                    xx = x2 + 1;
                }

                if (xx < 0 || xx > 7) {
                    return false;
                }

                // 判断是否有过路兵
                ChessComponent chess = chessComponents[xx][y2];
                if (chess.getChessColor() == getChessColor() ||
                        !(chess instanceof PawnChessComponent) ||
                        !chess.isNewestStep() ||   // 判断上一步走的是不是该棋子
                        (1 != chess.getStepCount())  // 判断该棋子是否只走过一步
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

    }
}

