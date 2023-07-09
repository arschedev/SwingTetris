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
    private char currentTetromino = 'I';
    private int xPos = Utils.random(1, 7);
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
                // reached limits of floor or other tetrominos
                if (yPos + 1 >= BOARD_HEIGHT || tetrominoWillCollide(xPos, yPos, currentTetromino, "down")) {
                    // new active tile
                    xPos = Utils.random(1, 7);
                    yPos = -1;
                }

                yPos++; // increment y position
                if (yPos > 0)
                    fillTetromino(xPos, yPos - 1, DEFAULT_TILE_COLOR, currentTetromino); // remove previous tile
                fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino); // fill current tile
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

    private Boolean tetrominoWillCollide(int x, int y, char tetromino, String direction) {
        int down = 0, left = 0, right = 0;
        if (direction.equals("down")) down = 1;
        if (direction.equals("left")) left = 1;
        if (direction.equals("right")) right = 1;

        if (tetromino == 'I') {
            if (x - 1 - left < 0) return true;
            if (x + 2 + right >= BOARD_WIDTH) return true;
            if (getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x + 1, y + down) != DEFAULT_TILE_COLOR) return true;
            return getTile(x + 2 + right, y + down) != DEFAULT_TILE_COLOR;
        }

        /* TODO */

        return false;
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
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino); // remove previous tetromino
            if (xPos > 0 && !tetrominoWillCollide(xPos, yPos, currentTetromino, "left")) // is aside from wall and other tetrominos
                xPos--; // decrement x position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino); // fill current tetromino
        }
        if (e.getKeyChar() == 'd' || e.getKeyChar() == 'в') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino); // remove previous tetromino
            if (xPos < BOARD_WIDTH - 1 && !tetrominoWillCollide(xPos, yPos, currentTetromino, "right")) //  is aside from wall and other tetrominos
                xPos++; // increment x position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino); // fill current tetromino
        }
        if (e.getKeyChar() == 's' || e.getKeyChar() == 'ы' || e.getKeyChar() == 'і') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino); // remove previous tetromino
            if (yPos < BOARD_HEIGHT - 1 && !tetrominoWillCollide(xPos, yPos, currentTetromino, "down")) // is above floor level and other tetrominos
                yPos++; // increment y position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino); // fill current tetromino
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
