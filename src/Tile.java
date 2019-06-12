import com.sun.tools.javac.Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Tile {
    // --Field-- //
    private int col, row;
    private boolean hasAnt, visible, inRange;
    private String type;

    // --Constructor(s)-- //
    // Row, column, type
    public Tile(int c, int r, String t) {
        col = c;
        row = r;
        type = t;

        hasAnt = false;
        visible = true;
        inRange = false;
    }

    // Row, column
    public Tile(int c, int r) {
        this(c, r, "empty");
    }

    // --Runtime-- //
    public void tick() {
        if (hasAnt || inRange) {
            visible = true;
        } else {
            visible = false;
        }

    }

    public void draw(Graphics2D g) {
        AffineTransform af = g.getTransform();

        g.translate(col * (int) (MainApp.tileSize.x + MainApp.gridGap.x), row * (int) (MainApp.tileSize.y + MainApp.gridGap.y));

        if (visible) {
            // Inside
            if (type == "wall") {
                g.setColor(Color.GRAY);
            } else {
                g.setColor(Color.WHITE);
            }

            g.fillRect(0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y);

            // Outline
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y);

            if (hasAnt) {
                g.setColor(Color.RED);
                g.translate((int) MainApp.tileSize.x / 2, (int) MainApp.tileSize.y / 2);
                g.fillRect((int) -MainApp.tileSize.x / 8, (int) -MainApp.tileSize.y / 8, (int) MainApp.tileSize.x / 4, (int) MainApp.tileSize.y / 4);
            }
        } else {
            g.setColor(Color.BLACK);

            g.fillRect((int) -MainApp.gridGap.x, (int) -MainApp.gridGap.y, (int) MainApp.tileSize.x + (int) MainApp.gridGap.x, (int) MainApp.tileSize.y + (int) MainApp.gridGap.y);
        }

        g.setTransform(af);
    }

    // --Methods-- //

    // --Helpers-- //
    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setAnt(boolean a) {
        hasAnt = a;
    }

    public String getType() {
        return type;
    }

    public void setType(String t) {
        type = t;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean v) {
        visible = v;
    }

    public boolean isInRange() {
        return inRange;
    }

    public void setInRange(boolean r) {
        inRange = r;
    }
}
