package model;

import controller.ClickController;
import view.ChessboardPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * 这个类是一个抽象类，主要表示8*8棋盘上每个格子的棋子情况，当前有两个子类继承它，分别是EmptySlotComponent(空棋子)和RookChessComponent(车)。
 */
public abstract class ChessComponent extends JComponent {

    /**
     * CHESSGRID_SIZE: 主要用于确定每个棋子在页面中显示的大小。
     * <br>
     * 在这个设计中，每个棋子的图案是用图片画出来的（由于国际象棋那个棋子手动画太难了）
     * <br>
     * 因此每个棋子占用的形状是一个正方形，大小是50*50
     */

//    private static final Dimension CHESSGRID_SIZE = new Dimension(1080 / 4 * 3 / 8, 1080 / 4 * 3 / 8);
    private static Color[] BACKGROUND_COLORS = {new Color(244, 242, 213), new Color(111, 155, 90)};
    private static Color[] ENTER_COLORS = {new Color(245, 236, 138), new Color(245, 236, 138)};
    private static Color[] SELECT_COLORS = {new Color(197, 191, 116), new Color(197, 191, 116)};
    /**
     * handle click event
     */
    protected ClickController clickController;
    private static int totalStepCount = 0;
    protected int stepCount;
    protected int curStep;

    /**
     * chessboardPoint: 表示8*8棋盘中，当前棋子在棋格对应的位置，如(0, 0), (1, 0), (0, 7),(7, 7)等等
     * <br>
     * chessColor: 表示这个棋子的颜色，有白色，黑色，无色三种
     * <br>
     * selected: 表示这个棋子是否被选中
     */
    private ChessboardPoint chessboardPoint;
    protected final ChessColor chessColor;
    private boolean selected;
    protected char name;
    private int showType;


    private boolean isEnablePath;
    private boolean isMouseEnter;


    protected ChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setLocation(location);
        setSize(size, size);
        this.chessboardPoint = chessboardPoint;
        this.chessColor = chessColor;
        this.selected = false;
        this.clickController = clickController;
        this.curStep = 0;
        this.stepCount = 0;
        this.repaint();
    }

    public char getname() {
        return name;
    }

    public ClickController getClickController() {
        return clickController;
    }

    public ChessboardPoint getChessboardPoint() {
        return chessboardPoint;
    }

    public void setChessboardPoint(ChessboardPoint chessboardPoint) {
        this.chessboardPoint = chessboardPoint;
    }

    public ChessColor getChessColor() {
        return chessColor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setShowType(int type) {
        showType = type;
    }

    public void setEnablePath(boolean enablePath) {
        isEnablePath = enablePath;
    }

    public void incrementStep() {
        curStep = ++totalStepCount;
        ++stepCount;
    }

    public static void decrementTotalCount() {
        --totalStepCount;
    }

    public void setCurStep(int step) {
        curStep = step;
    }

    public int getCurStep() {
        return curStep;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getTotalStepCount() {
        return totalStepCount;
    }

    public boolean isNewestStep() {
        return curStep == totalStepCount;
    }

    /**
     * @param another 主要用于和另外一个棋子交换位置
     *                <br>
     *                调用时机是在移动棋子的时候，将操控的棋子和对应的空位置棋子(EmptySlotComponent)做交换
     */
    public void swapLocation(ChessComponent another) {
        ChessboardPoint chessboardPoint1 = getChessboardPoint(), chessboardPoint2 = another.getChessboardPoint();
        Point point1 = getLocation(), point2 = another.getLocation();
        setChessboardPoint(chessboardPoint2);
        setLocation(point2);
        another.setChessboardPoint(chessboardPoint1);
        another.setLocation(point1);
    }

    /**
     * @param e 响应鼠标监听事件
     *          <br>
     *          当接收到鼠标动作的时候，这个方法就会自动被调用，调用所有监听者的onClick方法，处理棋子的选中，移动等等行为。
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        //mouse event 对象被鼠标点击之后会调用这个方法

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            System.out.printf("Click [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
            clickController.onClick(this);
        }
        //输出一个坐标
        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            isMouseEnter = true;
            repaint();
        }
        if (e.getID() == MouseEvent.MOUSE_EXITED) {
            isMouseEnter = false;
            repaint();
        }
    }

    public abstract ChessComponent clone();

    /**
     * @param chessboard  棋盘
     * @param destination 目标位置，如(0, 0), (0, 7)等等
     * @return this棋子对象的移动规则和当前位置(chessboardPoint)能否到达目标位置
     * <br>
     * 这个方法主要是检查移动的合法性，如果合法就返回true，反之是false
     */
    public abstract boolean canMoveTo(ChessComponent[][] chessboard, ChessboardPoint destination);

    /**
     * 这个方法主要用于加载一些特定资源，如棋子图片等等。
     *
     * @throws IOException 如果一些资源找不到(如棋子图片路径错误)，就会抛出异常
     */
    public abstract void loadResource() throws IOException;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        //System.out.printf("repaint chess [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());

        if (isMouseEnter) {
            Color squareColor = ENTER_COLORS[(chessboardPoint.getX() + chessboardPoint.getY()) % 2];
            g.setColor(squareColor);
        } else {
            Color squareColor = BACKGROUND_COLORS[(chessboardPoint.getX() + chessboardPoint.getY()) % 2];
            g.setColor(squareColor);
        }


        if (isSelected()) {
            Color squareColor = SELECT_COLORS[(chessboardPoint.getX() + chessboardPoint.getY()) % 2];
            g.setColor(squareColor);
        }
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        Font f = new Font("Comic Sans MS", Font.BOLD, 13);
        g.setFont(f);
        g.setColor(Color.green);

//        String ss = "("+getChessboardPoint().getX() + ","+getChessboardPoint().getY()+") ";
//        g.drawString(ss, 20, 20);
        //显示坐标
        if (showType == 1) {
            g.setColor(Color.green);
            drawPathType(g);
        } else if (showType == 2) {
            g.setColor(Color.gray);
            drawPathType(g);
        } else if (showType == 3) {
            g.setColor(Color.red);
            drawPathType(g);
        }

    }
    private void drawPathType(Graphics g) {
        g.fillRect(8, 8, 20, 5);
        g.fillRect(8, 8, 5, 20);

        g.fillRect(getWidth() - 8 - 20, 8, 20, 5);
        g.fillRect(getWidth() - 8 - 5, 8, 5, 20);

        g.fillRect(8, getHeight() - 8 - 5, 20, 5);
        g.fillRect(8, getHeight() - 8 - 20, 5, 20);

        g.fillRect(getWidth() - 8 - 20, getHeight() - 8 - 5, 20, 5);
        g.fillRect(getWidth() - 8 - 5, getHeight() - 8 - 20, 5, 20);
    }

}