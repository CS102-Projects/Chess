package controller;

import view.Chessboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameController {
    private Chessboard chessboard;
    private StoreController storeController;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
        storeController = new StoreController();
    }

    public List<String> loadGameFromFile(String path) {
        try {
            reset();
            storeController.load(path, chessboard);
//            List<String> chessData = Files.readAllLines(Path.of(path));
//            chessboard.loadGame(chessData);
//            return chessData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reset() {
        chessboard.reset();
    }

    public Boolean undo()
    {
        return UndoManagerController.getInstance().undo();
    }

    public void store() throws IOException {
        storeController.store();
    }
}
