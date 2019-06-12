import com.sun.tools.javac.Main;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Tile {
    // --Field-- //
    private int col, row;
    private boolean hasAnt, visible, inRange;
    private String type;
    private Color floorColor = new Color(0xC69C6D);
    Image img;

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
        this(c, r, "floor");
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

        if (visible || Util.REVEAL) {
            // Floor by default
            g.setColor(floorColor);
            g.fillRect(0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y);

            // If something else, override
            g.drawImage(img, 0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y, null);

            if (hasAnt) {
                g.setColor(Color.RED);
                g.translate((int) MainApp.tileSize.x / 2, (int) MainApp.tileSize.y / 2);
                g.fillRect((int) -MainApp.tileSize.x / 8, (int) -MainApp.tileSize.y / 8, (int) MainApp.tileSize.x / 4, (int) MainApp.tileSize.y / 4);
            }
        } else {
            g.setColor(Color.BLACK);

            g.fillRect(0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y);
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

    public void setImg(Image i) {
        img = i;
    }
}
