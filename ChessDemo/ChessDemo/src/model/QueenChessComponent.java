package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示国际象棋里面的王后
 */
public class QueenChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image QUEEN_WHITE;
    private static Image QUEEN_BLACK;


    private Image queenImage;

    /**
     * 读取加载王后棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (QUEEN_WHITE == null) {
            QUEEN_WHITE = ImageIO.read(new File("D:/image_new/5.png"));
        }
        if (QUEEN_BLACK == null) {
            QUEEN_BLACK = ImageIO.read(new File("D:/image_new/11.png"));
        }

    }


    private void initiateQueenImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                queenImage = QUEEN_WHITE;
            } else if (color == ChessColor.BLACK) {
                queenImage = QUEEN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public QueenChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateQueenImage(color);
    }

    @Override
    public ChessComponent clone()
    {
        QueenChessComponent queenChessComponent = new QueenChessComponent(getChessboardPoint(), getLocation(), getChessColor(), clickController, getSize().width);
        queenChessComponent.curStep = this.curStep;
        queenChessComponent.stepCount = this.stepCount;
        return queenChessComponent;
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
        if (source.getX()-destination.getX()==source.getY()-destination.getY()){
            int row = Math.min(source.getX(),destination.getX());int col =  Math.min(source.getY(),destination.getY());
            for (int i=1;i<Math.abs(source.getY()-destination.getY()); i++) {
                if (!(chessComponents[row+i][col+i] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        }
        else if (source.getX()-destination.getX()+source.getY()-destination.getY()==0){
            int row = Math.min(source.getX(),destination.getX());int col =  Math.max(source.getY(),destination.getY());
            for (int i=1;i<Math.abs(source.getY()-destination.getY()); i++) {
                if (!(chessComponents[row+i][col-i] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        }
        else if (source.getX() == destination.getX()) {
            int row = source.getX();
            for (int col = Math.min(source.getY(), destination.getY()) + 1;
                 col < Math.max(source.getY(), destination.getY()); col++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else if (source.getY() == destination.getY()) {
            int col = source.getY();
            for (int row = Math.min(source.getX(), destination.getX()) + 1;
                 row < Math.max(source.getX(), destination.getX()); row++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        }
        else {return false;}
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
        g.drawImage(queenImage, 1, 1, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);

    }
}
