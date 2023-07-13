import javax.swing.*;
import java.awt.*;

public class Tetris extends JFrame {
    public Tetris() {
        init();
    }

    private void init() {
        setTitle("Tetris (p to start) beta 1.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        Board board = new Board(getContentPane());
        add(board);
        pack();
        setLocationRelativeTo(null); // center frame
        addKeyListener(board); // Add KeyListener to the board
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Tetris tetris = new Tetris();
            tetris.setVisible(true);
        });
    }
}
