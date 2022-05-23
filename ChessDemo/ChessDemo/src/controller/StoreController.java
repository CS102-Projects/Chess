package controller;

import model.ChessComponent;
import view.Chessboard;
import view.ChessboardPoint;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class StoreController {
    private Chessboard chessboard;

    public StoreController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public boolean load(File file, Chessboard _chessboard) throws IOException {
        chessboard = _chessboard;
        String fileName = file.getName();
        if (!fileName.matches(".*(txt).*")) {
            Frame loadWrongFrame = new LoadWrong(104);
            loadWrongFrame.setVisible(true);
            return false;
        }
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line = in.readLine();
        line = in.readLine();
        if (!(Objects.equals(line, "WHITE") || Objects.equals(line, "BLACK"))) {
            Frame loadWrongFrame = new LoadWrong(103);
            loadWrongFrame.setVisible(true);
            return false;
        }
        String[] CHESSBOARD = new String[8];
        for (int i = 0; i < 8; i++) {
            CHESSBOARD[i] = in.readLine();
            if (CHESSBOARD[i].length() != 8) {
                Frame loadWrongFrame = new LoadWrong(101);
                loadWrongFrame.setVisible(true);
                return false;
            }
        }
        String reg = ".*[^KQRBNP_kqrbnp].*";
        for (String s : CHESSBOARD) {
            for (int i = 0; i < 8; i++) {
                if (s.matches(reg)) {
                    Frame loadWrongFrame = new LoadWrong(102);
                    loadWrongFrame.setVisible(true);
                    return false;
                }
            }
        }
        line = in.readLine();
        if (!line.trim().equals("--chessmove--")) {
            System.out.println("not chessmove data format");
            return false;
        }

        line = in.readLine();
        while (line != null) {
            if (!parseLine(line.trim())) {
                return false;
            }
            line = in.readLine();
        }

        in.close();
        return true;
    }

    public void store(String path) throws IOException {
//        Date date = new Date();//获取当前的日期
//        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");//设置日期格式
//        String str = df.format(date);//获取String类型的时间
//
//        FileWriter writer = new FileWriter("./archive_" + str);
        FileWriter writer = new FileWriter(path + ".txt");
        writer.write("--chessboard--");
        writer.write("\n" + Chessboard.currentColor);
        writer.write("\n" + getChessboardGraph());
        writer.write("--chessmove--");
        writer.write(UndoManagerController.getInstance().toString());
        writer.close();
    }

    public boolean parseLine(String line) {
        String[] vals = line.split(" ");
        if (vals.length == 0) {
            return false;
        }

        if (vals[0].equals("move")) {
            if (vals.length == 3) {
                return parseMoveOperator(vals[1], vals[2]);
            }
        } else if (vals[0].equals("upgrade")) {
            if (vals.length == 4) {
                return parseUpgradeOperator(vals[1], vals[2], vals[3]);
            }
        }

        return false;
    }

    private boolean parseMoveOperator(String first, String second) {
        first = first.substring(1, first.length() - 1);
        second = second.substring(1, second.length() - 1);
        String[] pos1 = first.split(",");
        if (pos1.length != 2) {
            return false;
        }
        String[] pos2 = second.split(",");
        if (pos2.length != 2) {
            return false;
        }


        int row1 = Integer.parseInt(pos1[0]);
        int col1 = Integer.parseInt(pos1[1]);
        int row2 = Integer.parseInt(pos2[0]);
        int col2 = Integer.parseInt(pos2[1]);

        chessboard.moveChess(new ChessboardPoint(row1, col1), new ChessboardPoint(row2, col2), 0);

//        UndoManagerController.getInstance().add(
//                new MoveUndoController(chessboard.getChessComponent(row1, col1),
//                        chessboard.getChessComponent(row2, col2), chessboard));

        return true;
    }

    private boolean parseUpgradeOperator(String first, String second, String third) {
        first = first.substring(1, first.length() - 1);
        second = second.substring(1, second.length() - 1);
        String[] pos1 = first.split(",");
        if (pos1.length != 2) {
            return false;
        }
        String[] pos2 = second.split(",");
        if (pos2.length != 2) {
            return false;
        }


        int row1 = Integer.parseInt(pos1[0]);
        int col1 = Integer.parseInt(pos1[1]);
        int row2 = Integer.parseInt(pos2[0]);
        int col2 = Integer.parseInt(pos2[1]);

        int switchType = Integer.parseInt(third);

        chessboard.moveChess(new ChessboardPoint(row1, col1), new ChessboardPoint(row2, col2), switchType);

        return true;
    }

    public String getChessboardGraph() {
        StringBuilder graph = new StringBuilder();
        for (ChessComponent[] chessComponent : chessboard.getChessComponents()) {
            for (ChessComponent component : chessComponent) {
                graph.append(component.getname());
            }
            graph.append("\n");
        }
        return String.valueOf(graph);
    }

    static class LoadWrong extends JFrame {
        public LoadWrong(int wrongType) {
            setTitle("LoadWrong");
            setSize(500, 300);
            // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            JLabel jLabel = new JLabel();
            if (wrongType == 101)
                jLabel.setText("   WRONG" + wrongType + ":Size of Chessboard Wrong");
            if (wrongType == 102)
                jLabel.setText("   WRONG" + wrongType + ":Chesscomponent Wrong");
            if (wrongType == 103)
                jLabel.setText("   WRONG" + wrongType + ":CurrentPlayer Wrong");
            if (wrongType == 104)
                jLabel.setText("   WRONG" + wrongType + ":FileType Wrong");
            jLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
            Container container = getContentPane();
            container.add(jLabel);
//            setVisible(true);
        }
    }
}