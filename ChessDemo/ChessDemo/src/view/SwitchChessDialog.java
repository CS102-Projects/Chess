package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwitchChessDialog  extends JDialog {
    private int clickType = 0;
    public SwitchChessDialog(){
        this.setVisible(true);
        this.setBounds(100, 100, 600, 500);
        Container container = this.getContentPane();
        container.setLayout(null);
        JLabel jLabel = new JLabel("选择要升级的类型");
        jLabel.setBounds(100,100,100,100);
        JButton btn1 = new JButton("车");
        btn1.setBounds(100,200,100,50);
        JButton btn2 = new JButton("马");
        btn2.setBounds(200,200,100,50);
        JButton btn3 = new JButton("相");
        btn3.setBounds(300,200,100,50);
        JButton btn4 = new JButton("王后");
        btn4.setBounds(400,200,100,50);

        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickType = 1;
                setVisible(false);
            }
        });

        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickType = 2;
                setVisible(false);
            }
        });

        btn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickType = 3;
                setVisible(false);
            }
        });

        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickType = 4;
                setVisible(false);
            }
        });

        container.add(jLabel);
        container.add(btn1);
        container.add(btn2);
        container.add(btn3);
        container.add(btn4);
    }

    public int getClickType() {
        return clickType;
    }
}