package view;

import controller.GameController;
import controller.UndoManagerController;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGHT;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;
    private Image theme1;
    private static JLabel background;

    public ChessGameFrame(int width, int height) {
        setTitle("2022 CS102A Project Demo"); //设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;

        this.repaint();
        this.setVisible(true);


        setSize(WIDTH, HEIGHT);

        background = new JLabel();
        background.setBounds(0, 0, this.getWidth(), this.getHeight());
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\zhang\\Desktop\\cutecute\\flower.jpg");
        Image scaledImage = imageIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
        imageIcon.setImage(scaledImage);
        background.setIcon(imageIcon);
        background.setVisible(true);
        background.setLayout(null);
        this.add(background);

        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        addChessboard(background,HEIGHT);
        addLabel(background,HEIGHT);
        addHelloButton(background,HEIGHT);
        addLoadButton(background,HEIGHT);
        addResignationButton(background,HEIGHT);
        addResetButton(background,HEIGHT);
        addUndoButton(background,HEIGHT);
        addStoreButton(background,HEIGHT);
        addTheme1button(background);
        addTheme2button(background);
        addTheme3button(background);

//        this.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                int width = getWidth();//获取窗口宽度
//                int height = getHeight();//获取窗口高度  你也可以设置高度居中
//                addChessboard(background,getHeight());
//                addLabel(background,getHeight());
//                addHelloButton(background,getHeight());
//                addLoadButton(background,getHeight());
//                addResignationButton(background,getHeight());
//                addResetButton(background,getHeight());
//                addUndoButton(background,getHeight());
//                addStoreButton(background,getHeight());
//                addTheme1button(background);
//                addTheme2button(background);
//                addTheme3button(background);
//            }
//        });


    }

    /**
     * 在游戏面板中添加棋盘
     */

    private void addChessboard(JLabel j, int x) {
        Chessboard chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE);
        gameController = new GameController(chessboard);

        chessboard.setLocation(x / 10, x / 10);
        j.add(chessboard);
    }

    /**
     * 在游戏面板中添加标签
     */
    private void addLabel(JLabel j, int x) {
        JLabel statusLabel = new JLabel("(՞•Ꙫ•՞)ﾉ");
        statusLabel.setLocation(x, x/10);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(statusLabel);
    }
//    private void addCurrentPlayer() {
//        JLabel statusLabel = new JLabel();
//        statusLabel.setLocation(HEIGHT, HEIGHT / 10+480);
//        statusLabel.setSize(200, 60);
//        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
//        add(statusLabel);
//    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addHelloButton(JLabel j, int x) {
        JButton button = new JButton("Show Hello Here");
        button.addActionListener((e) -> JOptionPane.showMessageDialog(this, "Hello, world!"));
        button.setLocation(x,x/10+120);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);
    }

    private void addLoadButton(JLabel j,int x) {
        JButton button = new JButton("Load");
        button.setLocation(x,x/10+180);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);

        button.addActionListener(e -> {
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this, "Input Path here");
            gameController.loadGameFromFile(path);
        });
    }

    private void addResignationButton(JLabel j,int x) {
        JButton button = new JButton("Resignation");
        button.addActionListener((e) -> JOptionPane.showMessageDialog(this, "Never give up!!!"));
        button.setLocation(x, x / 10 + 240);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);
    }

    private void addResetButton(JLabel j,int x) {
        JButton button = new JButton("Reset");
        button.setLocation(x, x / 10 + 300);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);

        button.addActionListener(e -> {
            System.out.println("Click reset");
            gameController.reset();
        });
    }

    private void addUndoButton(JLabel j,int x) {
        JButton button = new JButton("Undo");
        button.setLocation(x, x / 10 + 420);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);

        button.addActionListener(e -> {
            if (gameController.undo()) {
                System.out.println("Click Undo");
            } else {
                System.out.println("can't Undo");
            }
        });
    }

    private void addStoreButton(JLabel j,int x) {
        JButton button = new JButton("Store");
        button.setLocation(x, x / 10 + 480);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);

        button.addActionListener(e -> {
            try {
                gameController.store();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static void playMusic() {// 背景音乐播放
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("C:\\Users\\zhang\\Downloads\\Summer.wav"));    //绝对路径
            AudioFormat aif = ais.getFormat();
            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            FloatControl fc = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
            // value可以用来设置音量，从0-2.0
            double value = 2;
            float dB = (float) (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
            fc.setValue(dB);
            int nByte = 0;
            final int SIZE = 1024 * 64;
            byte[] buffer = new byte[SIZE];
            while (nByte != -1) {
                nByte = ais.read(buffer, 0, SIZE);
                sdl.write(buffer, 0, nByte);
            }
            sdl.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public Dimension getPreferredSize() {
//        Dimension space = getParent().getSize();
//        int length = (Math.min(space.width / this.WIDTH, space.height / this.HEIGHT));
//        return new Dimension(length, length);
//    }

    public void addTheme1button(JLabel j) {
        JButton button = new JButton("S1");
        button.setLocation(HEIGHT, HEIGHT / 10 + 60);
        button.setSize(60, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);

        button.addActionListener(e -> {
            ImageIcon imageIcon = new ImageIcon("C:\\Users\\zhang\\Desktop\\cutecute\\flower.jpg");
            Image scaledImage = imageIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
            imageIcon.setImage(scaledImage);
            j.setIcon(imageIcon);
            j.setVisible(true);
            //j.setLayout(null);
        });
    }

    public void addTheme2button(JLabel j) {
        JButton button = new JButton("S2");
        button.setLocation(HEIGHT + 60, HEIGHT / 10 + 60);
        button.setSize(60, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);

        button.addActionListener(e -> {
            ImageIcon imageIcon = new ImageIcon("C:\\Users\\zhang\\Desktop\\cutecute\\flower2.png");
            Image scaledImage = imageIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
            imageIcon.setImage(scaledImage);
            j.setIcon(imageIcon);
            j.setVisible(true);
            //j.setLayout(null);
        });
    }

    public void addTheme3button(JLabel j) {
        JButton button = new JButton("S3");
        button.setLocation(HEIGHT + 120, HEIGHT / 10 + 60);
        //button.setLocation(this.WIDTH-HEIGHT-228, HEIGHT / 10 + 60);
        button.setSize(60, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        j.add(button);

        button.addActionListener(e -> {
            ImageIcon imageIcon = new ImageIcon("C:\\Users\\zhang\\Desktop\\cutecute\\sun.jpg");
            Image scaledImage = imageIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
            imageIcon.setImage(scaledImage);
            j.setIcon(imageIcon);
            j.setVisible(true);
            //j.setLayout(null);
        });


    }
}
