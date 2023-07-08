import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class Tetris extends JFrame {
    public Tetris() {
        init();
    }

    private void init() {
        setTitle("Tetris (p to start)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        Board board = new Board();
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

class Board extends JPanel implements KeyListener {
    private final int TILE_SIZE = 30;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final Color DEFAULT_BORDER_COLOR = Color.WHITE;
    private final Color DEFAULT_TILE_COLOR = Color.BLACK;
    private final Color ACTIVE_TILE_COLOR = Color.RED;
    private final Color[][] boardGrid; // 2d array of colors
    private char tetromino = Utils.randomTetromino();
    private int xPos = Utils.random(0, 9);
    private int yPos = -1;

    public Board() {
        setPreferredSize(new Dimension(TILE_SIZE * BOARD_WIDTH, TILE_SIZE * BOARD_HEIGHT));
        boardGrid = new Color[BOARD_HEIGHT][BOARD_WIDTH]; // initialize board
        fillBoard();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                g.setColor(boardGrid[row][col]);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(DEFAULT_BORDER_COLOR);
                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void fillBoard() {
        fillBoard(DEFAULT_TILE_COLOR);
    }

    private void fillBoard(Color color) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                boardGrid[row][col] = color;
            }
        }

    }

    private void init() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // reached limits of floor or tiles
                if (yPos + 1 >= BOARD_HEIGHT || getTile(xPos, yPos + 1) != DEFAULT_TILE_COLOR) {
                    // new active tile
                    xPos = Utils.random(0, 9);
                    yPos = -1;
                }

                yPos++; // increment y position
                if (yPos > 0) fillTetromino(xPos, yPos - 1, DEFAULT_TILE_COLOR, tetromino); // remove previous tile
                fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, tetromino); // fill current tile
                repaint(); // Redrawing the board
            }
        }, 0, 1000);
    }

    private void fillTetromino(int x, int y, Color color, char tetromino) {
        if (tetromino == 'I') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            fillTile(x + 1, y, color);
            fillTile(x + 2, y, color);
        }

        if (tetromino == 'J') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            if (y > 0) fillTile(x - 1, y - 1, color);
            fillTile(x + 1, y, color);
        }

        if (tetromino == 'L') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x + 1, y - 1, color);
        }

        if (tetromino == 'O') {
            fillTile(x, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
            if (y > 0) fillTile(x + 1, y - 1, color);
        }

        if (tetromino == 'S') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
            if (y > 0) fillTile(x + 1, y - 1, color);
        }

        if (tetromino == 'T') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
        }

        if (tetromino == 'Z') {
            fillTile(x, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
            if (y > 0) fillTile(x - 1, y - 1, color);
        }
    }

    private void fillTile(int x, int y, Color color) {
        boardGrid[y][x] = color;
    }

    private Color getTile(int x, int y) {
        return boardGrid[y][x];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'p' || e.getKeyChar() == 'з') {
            init();
        }
        if (e.getKeyChar() == 'a' || e.getKeyChar() == 'ф') {
            fillTile(xPos, yPos, DEFAULT_TILE_COLOR); // remove previous tile
            if (xPos > 0 && getTile(xPos - 1, yPos) == DEFAULT_TILE_COLOR) // is aside from wall and other tiles
                xPos--; // decrement x position
            fillTile(xPos, yPos, ACTIVE_TILE_COLOR); // fill current tile
        }
        if (e.getKeyChar() == 'd' || e.getKeyChar() == 'в') {
            fillTile(xPos, yPos, DEFAULT_TILE_COLOR); // remove previous tile
            if (xPos < BOARD_WIDTH - 1 && getTile(xPos + 1, yPos) == DEFAULT_TILE_COLOR) //  is aside from wall and other tiles
                xPos++; // increment x position
            fillTile(xPos, yPos, ACTIVE_TILE_COLOR); // fill current tile
        }
        if (e.getKeyChar() == 's' || e.getKeyChar() == 'ы' || e.getKeyChar() == 'і') {
            fillTile(xPos, yPos, DEFAULT_TILE_COLOR); // remove previous tile
            if (yPos < BOARD_HEIGHT - 1 && getTile(xPos, yPos + 1) == DEFAULT_TILE_COLOR) // is above floor level and other tiles
                yPos++; // increment y position
            fillTile(xPos, yPos, ACTIVE_TILE_COLOR); // fill current tile
        }
        repaint(); // Redrawing the board
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO
    }
}

class Utils {
    public static int random(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static char randomTetromino() {
        final char[] tetrominos = {'I', 'J', 'L', 'O', 'S', 'T', 'Z'};
        return tetrominos[random(0, 6)];
    }
}
