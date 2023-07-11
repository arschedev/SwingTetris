import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
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
    private Color[][] boardGrid; // 2d array of colors
    private final Color DEFAULT_BORDER_COLOR = Color.WHITE;
    private final Color DEFAULT_TILE_COLOR = Color.BLACK;
    private Color ACTIVE_TILE_COLOR = Utils.randomColor();
    private char currentTetromino = 'J';
    private int currentRotation = 0;
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
                if (yPos + 1 >= BOARD_HEIGHT || tetrominoWillCollide(xPos, yPos, currentTetromino, "down", currentRotation)) {
                    // clear filled lines
                    clearLines();

                    // new active tetromino
                    ACTIVE_TILE_COLOR = Utils.randomColor();
                    currentTetromino = 'J';
                    currentRotation = 0;
                    xPos = Utils.random(1, 7);
                    yPos = -1;
                }

                yPos++; // increment y position
                if (yPos > 0)
                    fillTetromino(xPos, yPos - 1, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino
                fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
                repaint(); // Redrawing the board
            }
        }, 0, 500);
    }

    private void clearLines() {
        for (int row = BOARD_HEIGHT - 1; row > 0; row--) {
            boolean isLineFilled = true;

            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (boardGrid[row][col] == DEFAULT_TILE_COLOR) {
                    isLineFilled = false;
                    break;
                }
            }

            if (isLineFilled) {
                boardGrid = Utils.removeColorArrayFromColor2D(boardGrid, row);
                boardGrid = Utils.prependColorArrayToColor2D(boardGrid, new Color[BOARD_WIDTH]);
                fillLine(0, DEFAULT_TILE_COLOR);
                row++;
            }
        }
    }

    private void fillLine(int row, Color color) {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            fillTile(col, row, color);
        }
    }

    private void fillTetromino(int x, int y, Color color, char tetromino, int rotation) {
        /* I */

        if (tetromino == 'I' && (rotation == 0 || rotation == 2)) {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            fillTile(x + 1, y, color);
            fillTile(x + 2, y, color);
        }

        if (tetromino == 'I' && (rotation == 1 || rotation == 3)) {
            fillTile(x, y, color);
            fillTile(x, y - 1, color);
            fillTile(x, y + 1, color);
            fillTile(x, y + 2, color);
        }

        /* J */

        if (tetromino == 'J' && (rotation == 0)) {
            /**
             *      2
             *      1 @ 3
             *
             *        ^
             *  rotation center
             */
            fillTile(x, y, color); // @
            fillTile(x - 1, y, color); // 1
            if (y > 0) fillTile(x - 1, y - 1, color); // 2
            fillTile(x + 1, y, color); // 3
        }

        if (tetromino == 'J' && rotation == 1) {
            /**
             *      1 2
             *      @       - rotation center
             *      3
             */
            fillTile(x, y, color); // @
            fillTile(x, y - 1, color); // 1
            fillTile(x + 1, y - 1, color); // 2
            fillTile(x, y + 1, color); // 3
        }

        if (tetromino == 'J' && rotation == 2) {
            /**
             *  rotation center
             *        v
             *
             *      3 @ 1
             *          2
             */
            fillTile(x, y, color); // @
            fillTile(x + 1, y, color); // 1
            fillTile(x + 1, y + 1, color); // 2
            fillTile(x - 1, y, color); // 3
        }

        // TODO
        /**
         *        1
         *        @
         *      3 2
         */
        if (tetromino == 'J' && rotation == 3) {
            fillTile(x, y, color); // @
            fillTile(x, y - 1, color); // 1
            fillTile(x, y + 1, color); // 2
            fillTile(x - 1, y + 1, color); // 3
        }

        /* L */
        /**
         *            2
         *        1 @ 3
         */

        if (tetromino == 'L') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color); // 1
            if (y > 0) fillTile(x + 1, y - 1, color); // 2
            fillTile(x + 1, y, color); // 3
        }

        /* O */

        if (tetromino == 'O' && (rotation == 0 || rotation == 1 || rotation == 2 || rotation == 3)) {
            /**
             *      2 3
             *      @ 1
             */
            fillTile(x, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
            if (y > 0) fillTile(x + 1, y - 1, color);
        }

        /* S */

        if (tetromino == 'S') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
            if (y > 0) fillTile(x + 1, y - 1, color);
        }

        /* T */

        if (tetromino == 'T') {
            fillTile(x, y, color);
            fillTile(x - 1, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
        }

        /* Z */

        if (tetromino == 'Z') {
            fillTile(x, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
            if (y > 0) fillTile(x - 1, y - 1, color);
        }
    }

    private Boolean tetrominoWillCollide(int x, int y, char tetromino, String direction, int rotation) {
        int down = 0, left = 0, right = 0;
        if (direction.equals("down")) down = 1;
        if (direction.equals("left")) left = 1;
        if (direction.equals("right")) right = 1;

        /* I */

        if (tetromino == 'I' && (rotation == 0 || rotation == 2)) {
            if (x - 1 - left < 0) return true;
            if (x + 2 + right >= BOARD_WIDTH) return true;
            if (getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x + 1, y + down) != DEFAULT_TILE_COLOR) return true;
            return getTile(x + 2 + right, y + down) != DEFAULT_TILE_COLOR;
        }

        if (tetromino == 'I' && (rotation == 1 || rotation == 3)) {
            if (x - left < 0) return true;
            if (x + right >= BOARD_WIDTH) return true;
            if (y < 1) return true;
            if (y >= BOARD_HEIGHT - 3) return true;
            if ((left == 1 || right == 1) && getTile(x - left + right, y) != DEFAULT_TILE_COLOR) return true;
            if ((left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR) return true;
            if ((left == 1 || right == 1) && getTile(x - left + right, y + 1) != DEFAULT_TILE_COLOR) return true;
            return getTile(x - left + right, y + 2 + down) != DEFAULT_TILE_COLOR;
        }

        /* J */

        if (tetromino == 'J' && (rotation == 0)) {
            if (x - 1 - left < 0) return true;
            if (x + 1 + right >= BOARD_WIDTH) return true;
            if (getTile(x, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR) return true;
            return y > 0 && (left == 1 || right == 1) && getTile(x - 1 - left + right, y - 1) != DEFAULT_TILE_COLOR;
        }

        if (tetromino == 'J' && (rotation == 1)) {
            /**
             *      1 2
             *      @       - rotation center
             *      3
             */
            if (x - left < 0) return true; // hits left wall?
            if (x + 1 + right >= BOARD_WIDTH)
                return true; // hits right wall? `1` means there is 1 block on the right (marked as 2)
            if (y < 1) return true; // `1` means 1 block above center
            if (y >= BOARD_HEIGHT - 2)
                return true; // hits floor? `2` means 1 block below center and -1 to get real index
            if ((left == 1 || right == 1) && getTile(x - left + right, y) != DEFAULT_TILE_COLOR)
                return true; /* block @ hits other blocks on the left / right?
                `left == 1 || right == 1` is for not letting it check block below,
                because block below is block 3, which is a part of tetromino,
                otherwise it will stop, thus breaking the game,
                because it thinks that block 3 is a part of other tetromino */
            if (left == 1 && getTile(x - left, y - 1) != DEFAULT_TILE_COLOR)
                return true; /* block 1 hits other block on the left?
                `left == 1` -> same thing as before, except here we also can't go right because of block 2 */
            if (getTile(x + 1 + right, y - 1 + down) != DEFAULT_TILE_COLOR)
                return true; // block 2 hits other blocks on the right / bottom?
            return getTile(x - left + right, y + 1 + down) != DEFAULT_TILE_COLOR;
            /* block 3 hits other blocks on the left / right / bottom? */
        }

        if (tetromino == 'J' && (rotation == 2 )) {
            /**
             *  rotation center
             *        v
             *
             *      3 @ 1
             *          2
             */
            if (x - 1 - left < 0) return true; // hits left wall?
            if (x + 1 + right >= BOARD_WIDTH) return true; // hits right wall?
            if (y >= BOARD_HEIGHT - 2) return true;
            if (getTile(x, y + down) != DEFAULT_TILE_COLOR) return true; // block @ hits other block on the bottom?
            if (right == 1 && getTile(x + 1 + right, y) != DEFAULT_TILE_COLOR)
                return true; // block 1 hits other block on the right?
            if (getTile(x + 1 - left + right, y + 1 + down) != DEFAULT_TILE_COLOR)
                return true; // block 2 hits other blocks on the left / right / bottom?
        }

        if (tetromino == 'J' && (rotation == 3)) {
            /**
             *  rotation center
             *        v
             *
             *      3
             *      @
             *   2  1
             */
            if (x + right >= BOARD_WIDTH) return true; // hits left wall?
            if (y >= BOARD_HEIGHT - 2) return true; // hits right wall?
            if (y >= BOARD_HEIGHT - 3) return true;
            if (((left == 1 || right == 1) && getTile(x - left + right) != DEFAULT_TILE_COLOR)) return true; // block @ hits other block on the bottom?
            if (getTile(x - 1, y) != DEFAULT_TILE_COLOR) return true; // block 1 hits other block on the left?
            if (getTile(x, y - 1) != DEFAULT_TILE_COLOR) return true; // block 2 hits other block on the top?
        }


        /* L */

        if (tetromino == 'L') {
            if (x - 1 - left < 0) return true;
            if (x + 1 + right >= BOARD_WIDTH) return true;
            if (getTile(x, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR) return true;
            return y > 0 && (left == 1 || right == 1) && getTile(x + 1 - left + right, y - 1) != DEFAULT_TILE_COLOR;
        }

        /* O */

        if (tetromino == 'O') {
            if (x - left < 0) return true;
            if (x + 1 + right >= BOARD_WIDTH) return true;
            if (getTile(x - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR) return true;
            if (y > 0 && (left == 1) && getTile(x - left, y - 1) != DEFAULT_TILE_COLOR) return true;
            return y > 0 && (right == 1) && getTile(x + 1 + right, y - 1) != DEFAULT_TILE_COLOR;
        }

        /* S */

        if (tetromino == 'S') {
            if (x - 1 - left < 0) return true;
            if (x + 1 + right >= BOARD_WIDTH) return true;
            if (getTile(x + right, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (y > 0 && (left == 1) && getTile(x - left, y - 1) != DEFAULT_TILE_COLOR) return true;
            return y > 0 && (right == 1) && getTile(x + 1 + right, y - 1) != DEFAULT_TILE_COLOR;
        }

        /* T */

        if (tetromino == 'T') {
            if (x - 1 - left < 0) return true;
            if (x + 1 + right >= BOARD_WIDTH) return true;
            if (getTile(x, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR) return true;
            return y > 0 && (left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR;
        }

        /* Z */

        if (tetromino == 'Z') {
            if (x - 1 - left < 0) return true;
            if (x + 1 + right >= BOARD_WIDTH) return true;
            if (getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR) return true;
            if (getTile(x - left, y + down) != DEFAULT_TILE_COLOR) return true;
            if (y > 0 && (left == 1) && getTile(x - 1 - left, y - 1) != DEFAULT_TILE_COLOR) return true;
            return y > 0 && (right == 1) && getTile(x + right, y - 1) != DEFAULT_TILE_COLOR;
        }

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
        // play
        if (e.getKeyChar() == 'p' || e.getKeyChar() == 'з') {
            init();
        }

        // left
        if (e.getKeyChar() == 'a' || e.getKeyChar() == 'ф') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino
            if (!tetrominoWillCollide(xPos, yPos, currentTetromino, "left", currentRotation)) // is aside from wall and other tetrominos
                xPos--; // decrement x position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
        }

        // right
        if (e.getKeyChar() == 'd' || e.getKeyChar() == 'в') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino
            if (!tetrominoWillCollide(xPos, yPos, currentTetromino, "right", currentRotation)) //  is aside from wall and other tetrominos
                xPos++; // increment x position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
        }

        // down
        if (e.getKeyChar() == 's' || e.getKeyChar() == 'ы' || e.getKeyChar() == 'і') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino
            if (yPos < BOARD_HEIGHT - 1 && !tetrominoWillCollide(xPos, yPos, currentTetromino, "down", currentRotation)) // is above floor level and other tetrominos
                yPos++; // increment y position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
        }

        // rotate
        if (e.getKeyChar() == ' ') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino

            int testRotation = currentRotation + 1;
            if (testRotation > 3) testRotation = 0;

            if (!tetrominoWillCollide(xPos, yPos, currentTetromino, "down", testRotation)) {
                currentRotation = testRotation;
            }

            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
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
        return tetrominos[random(0, tetrominos.length - 1)];
    }

    public static char randomTetromino(int max) {
        final char[] tetrominos = {'I', 'J', 'L', 'O', 'S', 'T', 'Z'};
        return tetrominos[random(0, max - 1)];
    }

    public static Color randomColor() {
        final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};
        return colors[random(0, colors.length - 1)];
    }

    public static Color[][] removeColorArrayFromColor2D(Color[][] originalColor2D, int index) {
        int rowsNumber = originalColor2D.length;
        int colsNumber = originalColor2D[0].length;

        Color[][] color2D = Arrays.copyOf(originalColor2D, originalColor2D.length);
        color2D[index] = null;

        Color[][] result = new Color[rowsNumber - 1][colsNumber];

        int n = 0;
        for (int row = 0; row < color2D.length; row++) {
            if (color2D[row] != null) {
                result[row - n] = color2D[row];
            } else {
                n = 1;
            }
        }

        return result;
    }

    public static Color[][] prependColorArrayToColor2D(Color[][] originalColor2D, Color[] colorArray) {
        int rowsNumber = originalColor2D.length;
        int colsNumber = originalColor2D[0].length;
        int newRowsNumber = originalColor2D.length + 1;

        Color[][] result = new Color[newRowsNumber][colsNumber];
        result[0] = Arrays.copyOf(colorArray, colsNumber);
        System.arraycopy(originalColor2D, 0, result, 1, rowsNumber);

        return result;
    }
}