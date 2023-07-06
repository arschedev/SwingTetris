import javax.swing.*;
import java.awt.*;

public class TetrisGame extends JFrame {

    public TetrisGame() {
        initUI();
    }

    private void initUI() {
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        Board board = new Board();
        add(board);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            TetrisGame tetris = new TetrisGame();
            tetris.setVisible(true);
        });
    }
}

class Board extends JPanel {

    private final int TILE_SIZE = 30;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private Color[][] boardGrid;

    public Board() {
        setPreferredSize(new Dimension(TILE_SIZE * BOARD_WIDTH, TILE_SIZE * BOARD_HEIGHT));
        boardGrid = new Color[BOARD_HEIGHT][BOARD_WIDTH];
        clearBoard();
    }

    private void clearBoard() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                boardGrid[row][col] = Color.BLACK;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                g.setColor(boardGrid[row][col]);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }



}

