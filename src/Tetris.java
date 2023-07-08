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
                if (yPos > 0) fillTile(xPos, yPos - 1, DEFAULT_TILE_COLOR); // remove previous tile
                fillTile(xPos, yPos, ACTIVE_TILE_COLOR); // fill current tile
                repaint(); // Redrawing the board
            }
        }, 0, 1000);
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
            if (xPos < BOARD_WIDTH - 1 && getTile(xPos + 1, yPos) == DEFAULT_TILE_COLOR) //  is aside from wall and other tiles */
                xPos++; // increment x position
            fillTile(xPos, yPos, ACTIVE_TILE_COLOR); // fill current tile
        }
        if (e.getKeyChar() == 's' || e.getKeyChar() == 'ы' || e.getKeyChar() == 'і') {
            fillTile(xPos, yPos, DEFAULT_TILE_COLOR); // remove previous tile
            if (yPos < BOARD_HEIGHT - 1 && getTile(xPos, yPos + 1) == DEFAULT_TILE_COLOR) // is above floor level and other tiles */
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

    class Utils {
        public static int random(int min, int max) {
            return (int) Math.floor(Math.random() * (max - min + 1) + min);
        }



        public class Tetromino {
            private int[][] shape; // Massif, that represents the form of figurine
            private Color color; // The color of figurine

            public Tetromino(int[][] shape, Color color) {
                this.shape = shape;
                this.color = color;
            }

            public int[][] getShape() {
                return shape;
            }

            public Color getColor() {
                return color;
            }
        }

        public class TetrominoGenerator {
            private Tetromino[] tetrominos; // Massif of avaiable figures
            private int currentIndex; // Index of current figurine

            public TetrominoGenerator() {
                tetrominos = new Tetromino[]{
                        new Tetromino(new int[][]{{1, 1, 1, 1}}, Color.CYAN), // I
                        new Tetromino(new int[][]{{1, 1}, {1, 1}}, Color.YELLOW), // O
                        new Tetromino(new int[][]{{1, 1, 1}, {0, 1, 0}}, Color.MAGENTA), // T
                        new Tetromino(new int[][]{{1, 1, 0}, {0, 1, 1}}, Color.GREEN), // S
                        new Tetromino(new int[][]{{0, 1, 1}, {1, 1, 0}}, Color.RED), // Z
                        new Tetromino(new int[][]{{1, 1, 1}, {1, 0, 0}}, Color.ORANGE), // L
                        new Tetromino(new int[][]{{1, 1, 1}, {0, 0, 1}}, Color.BLUE) // J
                };
                currentIndex = -1;
            }

            public Tetromino getNextTetromino() {
                currentIndex = (currentIndex + 1) % tetrominos.length;
                return tetrominos[currentIndex];
            }
        }
        void init() {
            Timer timer = new Timer();
            TetrominoGenerator tetrominoGenerator = new TetrominoGenerator();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (yPos + 1 >= BOARD_HEIGHT || getTile(xPos, yPos + 1) != DEFAULT_TILE_COLOR) {
                        // Creating a new figure
                        Tetromino tetromino = tetrominoGenerator.getNextTetromino();
                        xPos = Utils.random(0, BOARD_WIDTH - tetromino.getShape()[0].length);
                        yPos = -1;
                        // Updating a form of figure on the field
                        updateBoardByTetromino(tetromino);
                    }

                    yPos++;
                    if (yPos > 0) fillTile(xPos, yPos - 1, DEFAULT_TILE_COLOR);
                    fillTile(xPos, yPos, ACTIVE_TILE_COLOR);
                    repaint();
                }
            }, 0, 1000);
        }

        private void updateBoardByTetromino(Tetromino tetromino) {
            int[][] shape = tetromino.getShape();
            Color color = tetromino.getColor();

            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] == 1) {
                        fillTile(xPos + col, yPos + row, color);
                    }
                }
            }
        }
    }
}

