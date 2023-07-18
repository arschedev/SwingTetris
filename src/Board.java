import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class Board extends JPanel implements KeyListener {
    private final Container frame;
    private Boolean hasStarted = false;
    private final int TILE_SIZE = 30;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private Color[][] boardGrid; // 2d array of colors
    private final Color DEFAULT_BORDER_COLOR = Color.WHITE;
    private final Color DEFAULT_TILE_COLOR = Color.BLACK;
    private Color ACTIVE_TILE_COLOR = Utils.randomColor();
    private char currentTetromino = Utils.randomTetromino();
    private int currentRotation = 0;
    private int xPos = Utils.random(1, 7);
    private int yPos = -1;

    public Board(Container container) {
        frame = container;
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
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // reached limits of floor or other tetrominos
                if (yPos + 1 >= BOARD_HEIGHT || tetrominoWillCollide(xPos, yPos, currentTetromino, "down", currentRotation)) {
                    // clear filled lines
                    clearLines();

                    // You won!
                    if (isBoardCleared()) {
                        repaint();
                        JOptionPane.showMessageDialog(frame, "You won!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }

                    // You lost!
                    if (yPos < 2) {
                        JOptionPane.showMessageDialog(frame, "You lost!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }

                    // new active tetromino
                    ACTIVE_TILE_COLOR = Utils.randomColor();
                    currentTetromino = Utils.randomTetromino();
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

    private Boolean isBoardCleared() {
        int filledTiles = 0;

        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (boardGrid[row][col] != DEFAULT_TILE_COLOR) filledTiles++;
            }
        }

        return filledTiles == 0;
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

        if (tetromino == 'J' && rotation == 3) {
            /**
             *        1
             *        @
             *      3 2
             */
            fillTile(x, y, color); // @
            fillTile(x, y - 1, color); // 1
            fillTile(x, y + 1, color); // 2
            fillTile(x - 1, y + 1, color); // 3
        }

        /* L */

        if (tetromino == 'L' && rotation == 0) {
            /**
             *          2
             *      1 @ 3
             */
            fillTile(x, y, color); // @
            fillTile(x - 1, y, color); // 1
            if (y > 0) fillTile(x + 1, y - 1, color); // 2
            fillTile(x + 1, y, color); // 3
        }

        if (tetromino == 'L' && rotation == 1) {
            /**
             *      1
             *      @
             *      3 2
             */
            fillTile(x, y, color); // @
            fillTile(x, y - 1, color); // 1
            fillTile(x + 1, y + 1, color); // 2
            fillTile(x, y + 1, color); // 3
        }

        if (tetromino == 'L' && rotation == 2) {
            /**
             *      3 @ 1
             *      2
             */
            fillTile(x, y, color); // @
            fillTile(x + 1, y, color); // 1
            fillTile(x - 1, y + 1, color); // 2
            fillTile(x - 1, y, color); // 3
        }

        if (tetromino == 'L' && rotation == 3) {
            /**
             *      2 3
             *        @
             *        1
             */
            fillTile(x, y, color); // @
            fillTile(x, y + 1, color); // 1
            fillTile(x - 1, y - 1, color); // 2
            fillTile(x, y - 1, color); // 3
        }

        /* O */

        if (tetromino == 'O' && (rotation == 0 || rotation == 1 || rotation == 2 || rotation == 3)) {
            /**
             *      2 3
             *      @ 1
             */
            fillTile(x, y, color); // @
            fillTile(x + 1, y, color); // 1
            if (y > 0) fillTile(x, y - 1, color); // 2
            if (y > 0) fillTile(x + 1, y - 1, color); // 3
        }

        /* S */

        if (tetromino == 'S' && (rotation == 0 || rotation == 2)) {
            /**
             *        2 3
             *      1 @
             */
            fillTile(x, y, color); // @
            fillTile(x - 1, y, color); // 1
            if (y > 0) fillTile(x, y - 1, color); // 2
            if (y > 0) fillTile(x + 1, y - 1, color); // 3
        }

        if (tetromino == 'S' && (rotation == 1 || rotation == 3)) {
            /**
             *      1
             *      @ 2
             *        3
             */
            fillTile(x, y, color); // @
            fillTile(x, y - 1, color); // 1
            fillTile(x + 1, y, color); // 2
            fillTile(x + 1, y + 1, color); // 3
        }

        /* T */

        if (tetromino == 'T' && (rotation == 0)) {
            /**
             *        3
             *      1 @ 2
             *
             */
            fillTile(x, y, color); // @
            fillTile(x - 1, y, color); // 1
            fillTile(x + 1, y, color); // 2
            if (y > 0) fillTile(x, y - 1, color); // 3
        }

        if (tetromino == 'T' && (rotation == 1)) {
            /**
             *      1
             *      @ 3
             *      2
             */
            fillTile(x, y, color); // @
            fillTile(x, y - 1, color); // 1
            fillTile(x, y + 1, color); // 2
            fillTile(x + 1, y, color); // 3
        }

        if (tetromino == 'T' && (rotation == 2)) {
            /**
             *      2 @ 1
             *        3
             */
            fillTile(x, y, color); // @
            fillTile(x + 1, y, color); // 1
            fillTile(x - 1, y, color); // 2
            fillTile(x, y + 1, color); // 3
        }

        if (tetromino == 'T' && (rotation == 3)) {
            /**
             *        2
             *      3 @
             *        1
             */
            fillTile(x, y, color); // @
            fillTile(x, y + 1, color); // 1
            fillTile(x, y - 1, color); // 2
            fillTile(x - 1, y, color); // 3
        }

        /* Z */

        if (tetromino == 'Z' && (rotation == 0 || rotation == 2)) {
            /**
             *      3 2
             *        @ 1
             */
            fillTile(x, y, color);
            fillTile(x + 1, y, color);
            if (y > 0) fillTile(x, y - 1, color);
            if (y > 0) fillTile(x - 1, y - 1, color);
        }

        if (tetromino == 'Z' && (rotation == 1 || rotation == 3)) {
            /**
             *        3
             *      @ 2
             *      1
             */
            fillTile(x, y, color);
            fillTile(x, y + 1, color);
            fillTile(x + 1, y, color);
            fillTile(x + 1, y - 1, color);
        }
    }

    private Boolean tetrominoWillCollide(int x, int y, char tetromino, String direction, int rotation) {
        int down = 0, left = 0, right = 0;
        if (direction.equals("down")) down = 1;
        if (direction.equals("left")) left = 1;
        if (direction.equals("right")) right = 1;

        /* I */

        if (tetromino == 'I' && (rotation == 0 || rotation == 2)) {
            return (x - 1 - left < 0 || x + 2 + right >= BOARD_WIDTH ||
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR ||
                    getTile(x, y + down) != DEFAULT_TILE_COLOR ||
                    getTile(x + 1, y + down) != DEFAULT_TILE_COLOR ||
                    getTile(x + 2 + right, y + down) != DEFAULT_TILE_COLOR);
        }

        if (tetromino == 'I' && (rotation == 1 || rotation == 3)) {
            return (x - left < 0 || x + right >= BOARD_WIDTH ||
                    y < 1 || y >= BOARD_HEIGHT - 3 ||
                    (left == 1 || right == 1) && getTile(x - left + right, y) != DEFAULT_TILE_COLOR ||
                    (left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR ||
                    (left == 1 || right == 1) && getTile(x - left + right, y + 1) != DEFAULT_TILE_COLOR ||
                    getTile(x - left + right, y + 2 + down) != DEFAULT_TILE_COLOR);
        }

        /* J */

        if (tetromino == 'J' && rotation == 0) {
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    getTile(x, y + down) != DEFAULT_TILE_COLOR || // @
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR || // 1
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR || // 2
                    (y > 0 && (left == 1 || right == 1) && getTile(x - 1 - left + right, y - 1) != DEFAULT_TILE_COLOR)); // 3
        }

        if (tetromino == 'J' && rotation == 1) {
            /**
             *      1 2
             *      @       - rotation center
             *      3
             */
            return (x - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y < 1 || // hits ceiling?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    ((left == 1 || right == 1) && getTile(x - left + right, y) != DEFAULT_TILE_COLOR) || // block @ hits other blocks on the left / right?
                    (left == 1 && getTile(x - left, y - 1) != DEFAULT_TILE_COLOR) || // block 1 hits other block on the left?
                    getTile(x + 1 + right, y - 1 + down) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the right / bottom?
                    getTile(x - left + right, y + 1 + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the left / right / bottom?
        }

        if (tetromino == 'J' && rotation == 2) {
            /**
             *  rotation center
             *        v
             *
             *      3 @ 1
             *          2
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    getTile(x, y + down) != DEFAULT_TILE_COLOR || // block @ hits other block on the bottom?
                    (right == 1 && getTile(x + 1 + right, y) != DEFAULT_TILE_COLOR) || // block 1 hits other block on the right?
                    getTile(x + 1 - left + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the left / right / bottom?
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the left / bottom?
        }

        if (tetromino == 'J' && rotation == 3) {
            /**
             *        3
             *        @
             *      2 1
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    y < 1 || // hits ceiling?
                    ((left == 1 || right == 1) && getTile(x - left + right, y) != DEFAULT_TILE_COLOR) || // block @ hits other blocks on the left / right?
                    getTile(x + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 1 hits other block on the left / bottom?
                    getTile(x - 1 - left, y + 1 + down) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the left / bottom?
                    ((left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR)); // block 3 hits other blocks on the left / right?
        }

        /* L */

        if (tetromino == 'L' && rotation == 0) {
            /**
             *          2
             *      1 @ 3
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    getTile(x, y + down) != DEFAULT_TILE_COLOR || // block @ hits other block on the bottom?
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR || // block 1 hits other blocks on the left / bottom?
                    (y > 0 && (left == 1 || right == 1) && getTile(x + 1 - left + right, y - 1) != DEFAULT_TILE_COLOR) || // block 2 hits other block on the left / right?
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the right / bottom?
        }

        if (tetromino == 'L' && rotation == 1) {
            /**
             *      1
             *      @
             *      3 2
             */
            return (x - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    y < 1 || // hits ceiling?
                    ((left == 1 || right == 1) && getTile(x - left + right, y) != DEFAULT_TILE_COLOR) || // block @ hits other blocks on the left / right?
                    ((left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR) || // block 1 hits other blocks on the left / right?
                    getTile(x + 1 + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the right / bottom?
                    getTile(x - left, y + 1 + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the left / bottom?
        }

        if (tetromino == 'L' && rotation == 2) {
            /**
             *      3 @ 1
             *      2
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    getTile(x, y + down) != DEFAULT_TILE_COLOR || // block @ hits other block on the bottom?
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR || // block 1 hits other blocks on the right / bottom?
                    getTile(x - 1 - left + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the left / right / bottom?
                    (left == 1 && getTile(x - 1 - left, y) != DEFAULT_TILE_COLOR)); // block 3 hits other block on the left?
        }

        if (tetromino == 'L' && rotation == 3) {
            /**
             *      2 3
             *        @
             *        1
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    y < 1 || // hits ceiling?
                    ((left == 1 || right == 1) && getTile(x - left + right, y) != DEFAULT_TILE_COLOR) || // block @ hits other blocks on the left / right?
                    getTile(x - left + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 1 hits other blocks on the left / right / bottom?
                    getTile(x - 1 - left, y - 1 + down) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the left / bottom?
                    (right == 1 && getTile(x + right, y - 1) != DEFAULT_TILE_COLOR)); // block 3 hits other block on the right?
        }

        /* O */

        if (tetromino == 'O') {
            return (x - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    getTile(x - left, y + down) != DEFAULT_TILE_COLOR || // @
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR || // 1
                    (y > 0 && left == 1 && getTile(x - left, y - 1) != DEFAULT_TILE_COLOR) || // 2
                    (y > 0 && right == 1 && getTile(x + 1 + right, y - 1) != DEFAULT_TILE_COLOR)); // 3
        }

        /* S */

        if (tetromino == 'S' && (rotation == 0 || rotation == 2)) {
            /**
             *        2 3
             *      1 @
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    getTile(x + right, y + down) != DEFAULT_TILE_COLOR || // @
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR || // 1
                    (y > 0 && left == 1 && getTile(x - left, y - 1) != DEFAULT_TILE_COLOR) || // 2
                    (y > 0 && right == 1 && getTile(x + 1 + right, y - 1) != DEFAULT_TILE_COLOR)); // 3
        }

        if (tetromino == 'S' && (rotation == 1 || rotation == 3)) {
            /**
             *        1
             *      2 @
             *      3
             */
            return (x - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    y < 1 || // hits ceiling?
                    getTile(x - left, y + down) != DEFAULT_TILE_COLOR || // block @ hits other block on the left / bottom?
                    (left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR || // block 1 hits other blocks on the left / right?
                    (left == 1 && getTile(x + 1 + right, y) != DEFAULT_TILE_COLOR) || // block 2 hits other block on the right?
                    getTile(x + 1 + right - left, y + 1 + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the left / right / bottom?
        }

        /* T */

        if (tetromino == 'T' && rotation == 0) {
            /**
             *        3
             *      1 @ 2
             *
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    getTile(x, y + down) != DEFAULT_TILE_COLOR || // @
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR || // 1
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR || // 2
                    (y > 0 && (left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR)); // 3
        }

        if (tetromino == 'T' && rotation == 1) {
            /**
             *      1
             *      @ 3
             *      2
             */
            return (x - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    y < 1 || // hits ceiling?
                    (left == 1 && getTile(x - left, y) != DEFAULT_TILE_COLOR) || // block @ hits other block on the left?
                    (left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR || // block 1 hits other blocks on the left / right?
                    getTile(x - left + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the left / right / bottom?
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the right / bottom?
        }

        if (tetromino == 'T' && rotation == 2) {
            /**
             *      2 @ 1
             *        3
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR || // block 1 hits other block on the right / bottom?
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR || // block 2 hits other block on the left / bottom?
                    getTile(x - left + right, y + 1 + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the left / right / bottom?
        }

        if (tetromino == 'T' && rotation == 3) {
            /**
             *        2
             *      3 @
             *        1
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    y < 1 || // hits ceiling?
                    (right == 1 && getTile(x + right, y) != DEFAULT_TILE_COLOR) || // block @ hits other block on the right?
                    getTile(x - left + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 1 hits other blocks on the left / right / bottom?
                    (left == 1 || right == 1) && getTile(x - left + right, y - 1) != DEFAULT_TILE_COLOR || // block 2 hits other blocks on the left / right?
                    getTile(x - 1 - left, y + down) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the left / bottom?
        }

        /* Z */

        if (tetromino == 'Z' && (rotation == 0 || rotation == 2)) {
            /**
             *      3 2
             *        @ 1
             */
            return (x - 1 - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    getTile(x - left, y + down) != DEFAULT_TILE_COLOR || // @
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR || // 1
                    (y > 0 && right == 1 && getTile(x + right, y - 1) != DEFAULT_TILE_COLOR) || // 2
                    (y > 0 && getTile(x - 1 - left, y - 1 + down) != DEFAULT_TILE_COLOR)); // 3
        }

        if (tetromino == 'Z' && (rotation == 1 || rotation == 3)) {
            /**
             *        3
             *      @ 2
             *      1
             */
            return (x - left < 0 || // hits left wall?
                    x + 1 + right >= BOARD_WIDTH || // hits right wall?
                    y >= BOARD_HEIGHT - 2 || // hits floor?
                    y < 1 || // hits ceiling?
                    (left == 1 && getTile(x - left, y) != DEFAULT_TILE_COLOR) || // block @ hits other block on the left?
                    getTile(x - left + right, y + 1 + down) != DEFAULT_TILE_COLOR || // block 1 hits other blocks on the left / right / bottom?
                    getTile(x + 1 + right, y + down) != DEFAULT_TILE_COLOR || // block 2 hits other block on the right / bottom?
                    (left == 1 || right == 1) && getTile(x + 1 - left + right, y - 1) != DEFAULT_TILE_COLOR); // block 3 hits other blocks on the left / right?
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
        if (!hasStarted && (e.getKeyChar() == 'p' || e.getKeyChar() == 'з')) {
            hasStarted = true;
            init();
        }

        // left
        if (hasStarted && e.getKeyChar() == 'a' || e.getKeyChar() == 'ф') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino
            if (!tetrominoWillCollide(xPos, yPos, currentTetromino, "left", currentRotation)) // is aside from wall and other tetrominos
                xPos--; // decrement x position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
        }

        // right
        if (hasStarted && e.getKeyChar() == 'd' || e.getKeyChar() == 'в') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino
            if (!tetrominoWillCollide(xPos, yPos, currentTetromino, "right", currentRotation)) //  is aside from wall and other tetrominos
                xPos++; // increment x position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
        }

        // down
        if (hasStarted && e.getKeyChar() == 's' || e.getKeyChar() == 'ы' || e.getKeyChar() == 'і') {
            fillTetromino(xPos, yPos, DEFAULT_TILE_COLOR, currentTetromino, currentRotation); // remove previous tetromino
            if (yPos < BOARD_HEIGHT - 1 && !tetrominoWillCollide(xPos, yPos, currentTetromino, "down", currentRotation)) // is above floor level and other tetrominos
                yPos++; // increment y position
            fillTetromino(xPos, yPos, ACTIVE_TILE_COLOR, currentTetromino, currentRotation); // fill current tetromino
        }

        // rotate
        if (hasStarted && e.getKeyChar() == ' ') {
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
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
