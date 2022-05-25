package model;

import controller.ChessEnablePathController;
import controller.ClickController;
import view.ChessboardPoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * 这个类表示国际象棋里面的车
 */
public class KingChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image KING_WHITE;
    private static Image KING_BLACK;

    /**
     * 车棋子对象自身的图片，是上面两种中的一种
     */
    private Image kingImage;

    public KingChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        if (color == ChessColor.BLACK) {
            this.name = 'K';
        } else if (color == ChessColor.WHITE) {
            this.name = 'k';
        }
        initiateKingImage(color);
    }

    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateKingImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                kingImage = KING_WHITE;
            } else if (color == ChessColor.BLACK) {
                kingImage = KING_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取加载车棋子的图片
     */
    public void loadResource() throws IOException {
            if (KING_WHITE == null) {
                KING_WHITE = ImageIO.read(new File("./ChessDemo/ChessDemo/images/4.png"));
            }
            if (KING_BLACK == null) {
                KING_BLACK = ImageIO.read(new File("./ChessDemo/ChessDemo/images/7.png"));
            }
    }

    @Override
    public ChessComponent clone() {
        KingChessComponent kingChessComponent = new KingChessComponent(getChessboardPoint(), getLocation(), getChessColor(), clickController, getSize().width);
        kingChessComponent.curStep = this.curStep;
        kingChessComponent.stepCount = this.stepCount;
        return kingChessComponent;
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
        int x2 = destination.getX();
        int y2 = destination.getY();

        if (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) {
            return false;
        }

        ChessboardPoint source = getChessboardPoint();
        if (getStepCount() == 0) {
            return ChessEnablePathController.canMoveTo(this, destination);
        }
        return Math.abs(destination.getX() - source.getX()) <= 1 && Math.abs(destination.getY() - source.getY()) <= 1;
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
        g.drawImage(kingImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);

    }
}