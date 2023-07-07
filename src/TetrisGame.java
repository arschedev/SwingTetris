import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class TetrisGame extends JFrame {
    public TetrisGame() {
        init();
    }

    private void init() {
        setTitle("Tetris");
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
            TetrisGame tetris = new TetrisGame();
            tetris.setVisible(true);
        });
    }
}

class Board extends JPanel implements KeyListener {

    private final int TILE_SIZE = 40;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final Color DEFAULT_TILE_COLOR = Color.BLACK;
    private final Color DEFAULT_BORDER_COLOR = Color.WHITE;
    private final Color[][] boardGrid; // 2d array of colors

    public Board() {
        setPreferredSize(new Dimension(TILE_SIZE * BOARD_WIDTH, TILE_SIZE * BOARD_HEIGHT));
        boardGrid = new Color[BOARD_HEIGHT][BOARD_WIDTH]; // initialize board
        fillBoard();

         fillTile(0, 0, Color.RED);
         fillTile(BOARD_WIDTH - 1, 0, Color.RED);
         fillTile(BOARD_WIDTH - 1, BOARD_HEIGHT - 1, Color.RED);
         fillTile(0, BOARD_HEIGHT - 1, Color.RED);
    }

    private void fillTile(int x, int y, Color color) {
        boardGrid[y][x] = color;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void render(Graphics g) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                g.setColor(boardGrid[row][col]);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(DEFAULT_BORDER_COLOR);
                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void movement() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            }
        }, 0, 1000);
    }

    private int random(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'a') {
            fillTile(4, 4, Color.WHITE);
            fillTile(3,4,Color.white);
            fillTile(5,4,Color.white);
            fillTile(4,3,Color.white);
            fillTile(4,2,Color.white);
            repaint(); // Redrawing the board
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'a') {
            fillTile(4,4,Color.BLACK);
            fillTile(3,4,Color.black);
            fillTile(5,4,Color.black);
            fillTile(4,3,Color.black);
            fillTile(4,2,Color.black);
            repaint();
        }
    }
}