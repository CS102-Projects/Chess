import view.ChessGameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuFrame extends JFrame implements ActionListener {

    private static int x;
    private static int y;
    private static int height;
    private static int width;
    private static JPanel panel;
    private static JButton StartButton;
    private static ImageIcon image;
    private static JLabel background;
    private static ImageIcon imageIcon;
    private static JButton loadButton;

    public MenuFrame() {
        x = 20;
        y = 20;
        width = 500;
        height = 500;
        panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(x, y, width, height);
        this.setLayout(null);
        this.add(panel);
        panel.setVisible(true);
        this.setBounds(400, 200, 700, 500);
        this.setVisible(true);
//        this.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                int width = getWidth();//获取窗口宽度
//                int height = getHeight();//获取窗口高度  你也可以设置高度居中
//                //将lable放在 窗口左边的1/3处
//                panel.setBounds(width / 5, 100, 700, 500);//(起始点x，起始点y，宽地w，高h)  标签设置宽高不明显
//                //将lable放在 窗口左边的1/2处
//                StartButton.setBounds(width / 2, 63, 61, 16);//(起始点x，起始点y，宽地w，高h)
//                //宽度始终是窗口的1/2
//
//            }
//        });


        StartButton = new JButton();
        StartButton.setBounds(200,200,200,50);
        //StartButton.setOpaque(false);
        StartButton.setFocusPainted(false);
        StartButton.setContentAreaFilled(false);
       // StartButton.setBorder(null);
        StartButton.setLayout(null);
        //StartButton.setMargin(new Insets(0, 0, 0, 0));
        StartButton.setText("Game Start");
        panel.add(StartButton);
        StartButton.setVisible(true);
        StartButton.addActionListener(this);



        background = new JLabel();
        background.setBounds(0, 0, this.getWidth(), this.getHeight());
        background.setLayout(null);

        imageIcon = new ImageIcon("./ChessDemo/ChessDemo/images/coverS.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
        imageIcon.setImage(scaledImage);

        background.setIcon(imageIcon);
        background.setVisible(true);
        this.add(background);
        this.repaint();


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            ChessGameFrame mainFrame = new ChessGameFrame(1300, 760);
            mainFrame.setVisible(true);
            Main.myFrame.setVisible(false);
            new Thread(() -> {
                while (true) {
                    ChessGameFrame.playMusic();
                }//while中的true可换成参数来控制音乐的停止播放
            }).start();
        });
    }


}
