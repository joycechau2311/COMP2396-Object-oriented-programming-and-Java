import javax.swing.*;
import java.awt.*;

class BoardButton extends JButton {
    private final int row;
    private final int col;

    public BoardButton(int row, int col) {
        this.row = row;
        this.col = col;
        setFont(new Font("Arial", Font.BOLD, 60));
        setFocusPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}