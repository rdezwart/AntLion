import java.awt.*;
import java.awt.geom.AffineTransform;

public class Tile {
    // --Field-- //
    private int col, row;
    private boolean hasAnt, visible, inRange, death;
    private String type;

    Image img;

    private Color floorColor = new Color(0xC69C6D);
    private Color jumpColor = new Color(0, 189, 10);
    private Color oneColor = new Color(218, 238, 33);
    private Color usedColor = new Color(176, 195, 32);
    private Color landColor = new Color(202, 57, 57);
    private Color slidColor = new Color(159, 50, 50);
    private Color visionColor = new Color(71, 76, 169);
    private Color endColor = new Color(255, 158, 20);
    private Color startColor = new Color(175, 72, 255);
    private Color deathColor = new Color(255, 35, 0);

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
            g.setColor(getTileColor());
            g.fillRect(0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y);

            // If something else, override
            if (!hasAnt) {
                g.drawImage(img, 0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y, null);
            }

            if (hasAnt) {
                g.translate((int) MainApp.tileSize.x / 2, (int) MainApp.tileSize.y / 2);
                g.rotate(Math.toRadians(MainApp.antRotation));
                g.drawImage(img, (int) -MainApp.tileSize.x / 2, (int) -MainApp.tileSize.y / 2, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y, null);
            }
        } else {
            g.setColor(Color.BLACK);

            g.fillRect(0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y);

            if (death) {
                g.setColor(deathColor);
                g.fillRect(0, 0, (int) MainApp.tileSize.x, (int) MainApp.tileSize.y);
            }
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

    public boolean isHasAnt() {
        return hasAnt;
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

    public Color getTileColor() {
        switch (type) {
            case "jump":
                return jumpColor;

            case "one":
                return oneColor;

            case "used":
                return usedColor;

            case "land":
                return landColor;

            case "slid":
                return slidColor;

            case "vision":
                return visionColor;

            case "end":
                return endColor;

            case "start":
                return startColor;

            case "death":
                return deathColor;

            default:
                return floorColor;
        }
    }
}
