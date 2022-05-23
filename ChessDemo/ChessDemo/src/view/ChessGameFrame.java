package view;

import controller.GameController;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGHT;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;
    public static JLabel currentPlayerLabel = new JLabel();
    public static JTextArea record = new JTextArea("MoveRecord", 20, 20);

    public ChessGameFrame(int width, int height) {

        setTitle("2022 CS102A Project Demo"); //设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);
        addChessboard();
//        addHelloButton();
        addLoadButton();
        addResignationButton();
        addResetButton();
        addUndoButton();
        addStoreButton();
        addCurrentPlayerLabel();
        addMoveRecordPanel();
    }


    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        Chessboard chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGHT / 10, HEIGHT / 10);
        add(chessboard);
    }

    /**
     * 在游戏面板中添加标签
     */
    private void addLabel() {
        JLabel statusLabel = new JLabel("(՞•Ꙫ•՞)ﾉ");
        statusLabel.setLocation(HEIGHT, HEIGHT / 10);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(statusLabel);
    }

    private void addMoveRecordPanel() {
        JPanel moveRecord = new JPanel();
        moveRecord.setLocation(HEIGHT + 100, HEIGHT / 10);
        moveRecord.setSize(500, 600);
        moveRecord.setFont(new Font("Rockwell", Font.BOLD, 20));
        record.setEditable(false);
        record.setLineWrap(true);    //设置文本域中的文本为自动换行
        record.setForeground(Color.BLACK);    //设置组件的背景色
        record.setFont(new Font("楷体", Font.BOLD, 16));    //修改字体样式
        record.setBackground(Color.WHITE);//设置按钮背景色
//        record.setLocation(HEIGHT+600,HEIGHT/10);
        record.setSize(300, 300);
        JScrollPane jsp = new JScrollPane(record);    //将文本域放入滚动窗口
        Dimension size = record.getPreferredSize();    //获得文本域的首选大小
        jsp.setBounds(HEIGHT + 300, HEIGHT / 10, size.width, size.height);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        DefaultCaret caret = (DefaultCaret) record.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        moveRecord.add(jsp);//将JScrollPane添加到JPanel容器中
        add(moveRecord);
    }

    private void addCurrentPlayerLabel() {

        currentPlayerLabel.setText(String.valueOf(Chessboard.currentColor));
        currentPlayerLabel.setLocation(HEIGHT, HEIGHT / 10);
        currentPlayerLabel.setSize(200, 60);
        currentPlayerLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(currentPlayerLabel);
    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addHelloButton() {
        JButton button = new JButton("Show Hello Here");
        button.addActionListener((e) -> JOptionPane.showMessageDialog(this, "Hello, world!"));
        button.setLocation(HEIGHT, HEIGHT / 10 + 120);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");
        button.setLocation(HEIGHT, HEIGHT / 10 + 180);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click load");
            JFileChooser fileChooser = new JFileChooser();
            int flag = fileChooser.showOpenDialog(this);
            if (flag == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                gameController.loadGameFromFile(file);
            }
//            String path = JOptionPane.showInputDialog(this,"Input Path here");
//            gameController.loadGameFromFile(path);
        });
    }

    private void addResignationButton() {
        JButton button = new JButton("Resignation");
        button.addActionListener((e) -> JOptionPane.showMessageDialog(this, "Never give up!!!"));
        button.setLocation(HEIGHT, HEIGHT / 10 + 240);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addResetButton() {
        JButton button = new JButton("Reset");
        button.setLocation(HEIGHT, HEIGHT / 10 + 300);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click reset");
            gameController.reset();
        });
    }

    private void addUndoButton() {
        JButton button = new JButton("Undo");
        button.setLocation(HEIGHT, HEIGHT / 10 + 420);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            if (!gameController.canUndo()) {
                return;
            }

            if (gameController.undo()) {
                System.out.println("Click Undo");
            } else {
                System.out.println("can't Undo");
            }
        });
    }

    private void addStoreButton() {
        JButton button = new JButton("Store");
        button.setLocation(HEIGHT, HEIGHT / 10 + 480);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int flag = fileChooser.showSaveDialog(this);
                if (flag == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    gameController.store(path);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}