package controller;

import view.Chessboard;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoreController {

    private Chessboard chessboard;

    public Boolean load(String filepath, Chessboard _chessboard) throws IOException {
        chessboard = _chessboard;

        BufferedReader in=new BufferedReader(new FileReader(filepath));
        String line = in.readLine();
        if (!line.trim().equals("--chessdemo--")) {
            System.out.println("not chessdemo data format");
            return false;
        }

        line = in.readLine();
        while (line!=null)
        {
            if (!parseLine(line.trim())) {
                return false;
            }
            line=in.readLine();
        }

        in.close();
        return true;
    }

    public Boolean parseLine(String line) {
        String[] vals = line.split(" ");
        if (vals.length == 0) {
            return false;
        }

        if (vals[0].equals("move")) {
            if (vals.length == 3) {
                return parseMoveOperator(vals[1], vals[2]);
            }
        }

        return false;
    }

    private Boolean parseMoveOperator(String first, String second) {
        first = first.substring(1, first.length()-1);
        second = second.substring(1, second.length()-1);
        String []pos1 = first.split(",");
        if (pos1.length != 2) {
            return false;
        }
        String []pos2 = second.split(",");
        if (pos2.length != 2) {
            return false;
        }

        int row1 = Integer.parseInt(pos1[0]);
        int col1 = Integer.parseInt(pos1[1]);
        int row2 = Integer.parseInt(pos2[0]);
        int col2 = Integer.parseInt(pos2[1]);
        UndoManagerController.getInstance().add(
                new MoveUndoController(chessboard.getChessComponent(row1, col1),
                        chessboard.getChessComponent(row2, col2), chessboard));

        return true;
    }

    public void store() throws IOException {
        Date date = new Date();//获取当前的日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");//设置日期格式
        String str = df.format(date);//获取String类型的时间

        FileWriter writer = new FileWriter("./archive_" + str);
        writer.write("--chessdemo--");
        writer.write(UndoManagerController.getInstance().toString());
        writer.close();
    }
}
