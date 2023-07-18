import java.awt.*;
import java.util.Arrays;

public class Utils {
    public static int random(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static char randomTetromino() {
        final char[] tetrominos = {'I', 'J', 'L', 'O', 'S', 'T', 'Z'};
        return tetrominos[random(0, tetrominos.length - 1)];
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
