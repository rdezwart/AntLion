package rdz.antlion;

public class Ant {
    // --Fields-- //
    private int col, row;

    // --Constructor(s)-- //
    // Starting position
    public Ant(int c, int r) {
        col = c;
        row = r;
    }

    // Default
    public Ant() {
        this(0, 0);
    }

    // --Methods-- //

    // --Helpers-- //
    public int getCol() {
        return col;
    }

    public void setCol(int c) {
        if (c >= 0 && c < MainApp.gridSize.x) {
            col = c;
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int r) {
        if (r >= 0 && r < MainApp.gridSize.y) {
            row = r;
        }
    }
}
