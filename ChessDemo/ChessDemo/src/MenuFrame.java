import view.ChessGameFrame;
import view.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

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

        StartButton = new JButton("Game Start");
        Font f =new Font("Rockwell", Font.BOLD, 20);
        StartButton.setFont(f);

        StartButton.setBounds(216, 280, 200, 50);
        StartButton.setOpaque(false);
        StartButton.setFocusPainted(false);
        StartButton.setContentAreaFilled(false);
        // StartButton.setBorder(null);
        StartButton.setLayout(null);
        //StartButton.setMargin(new Insets(0, 0, 0, 0));
        panel.add(StartButton);
        StartButton.setVisible(true);
        StartButton.addActionListener(this);


        background = new JLabel();
        background.setBounds(0, 0, this.getWidth(), this.getHeight());
        background.setLayout(null);

        imageIcon = new ImageIcon("./ChessDemo/ChessDemo/images/rainbow.gif");
        //./ChessDemo/ChessDemo/images/coverSS.png
        Image scaledImage = imageIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
        imageIcon.setImage(scaledImage);

        background.setIcon(imageIcon);
        addTitle(background);
        background.setVisible(true);
        this.add(background);
        this.repaint();


    }

    private void addTitle(JLabel j) {
        JLabel title = new JLabel();
        title.setText("CHESS");
        title.setLocation(150, 10);
        title.setSize(400, 300);
        title.setFont(new Font("Rockwell", Font.BOLD, 100));
        j.add(title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            ChessGameFrame mainFrame = new ChessGameFrame(1300, 760);
            mainFrame.setVisible(true);
            Main.myFrame.setVisible(false);
            String filepath = "./ChessDemo/ChessDemo/music/Summer.wav";
            ChessGameFrame.musicStuff musicObject = new ChessGameFrame.musicStuff();
            musicObject.playMusic(filepath);

        });
    }


}
