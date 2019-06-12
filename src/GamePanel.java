import com.sun.tools.javac.Main;
import processing.core.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

    // --Inner Classes-- //
    private class MyMouseAdapter extends MouseAdapter {
        public MyMouseAdapter() {

        }

        public void mousePressed(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        public MyKeyAdapter() {

        }

        public void keyPressed(KeyEvent e) {
            // Up
            if (e.getKeyCode() == e.VK_W || e.getKeyCode() == e.VK_UP) {
                moveAnt("up", 1);
            }
            // Right
            if (e.getKeyCode() == e.VK_D || e.getKeyCode() == e.VK_RIGHT) {
                moveAnt("right", 1);
            }
            // Down
            if (e.getKeyCode() == e.VK_S || e.getKeyCode() == e.VK_DOWN) {
                moveAnt("down", 1);
            }
            // Left
            if (e.getKeyCode() == e.VK_A || e.getKeyCode() == e.VK_LEFT) {
                moveAnt("left", 1);
            }

            // Esc
            if (e.getKeyCode() == e.VK_ESCAPE) {
                app.quit();
            }
        }
    }

    // --Fields-- //
    private MainApp app;

    private Tile[][] tileGrid;
    private ArrayList<Tile> tileList;

    private Timer timer;

    Ant a;

    private Image[] wallImages;

    // --Constructor(s)-- //
    public GamePanel(MainApp ma) {
        // App
        app = ma;
        setPreferredSize(MainApp.panelSize);

        // Tiles
        tileList = new ArrayList<Tile>();
        tileGrid = new Tile[(int) MainApp.gridSize.x][(int) MainApp.gridSize.y];
        for (int c = 0; c < tileGrid.length; c++) {
            for (int r = 0; r < tileGrid[c].length; r++) {
                tileGrid[c][r] = new Tile(c, r);
                tileList.add(tileGrid[c][r]);
            }
        }

        for (int c = 4; c < 9; c++) {
            tileGrid[c][5].setType("wall");
            tileGrid[c][7].setType("vision");
            tileGrid[c][9].setType("one");
            tileGrid[c][11].setType("land");
            tileGrid[c][13].setType("jump");
        }

        tileGrid[8][6].setType("jump");
        tileGrid[9][7].setType("jump");
        tileGrid[11][13].setType("jump");
        tileGrid[12][13].setType("wall");
        tileGrid[13][13].setType("wall");
        tileGrid[12][14].setType("jump");
        tileGrid[12][12].setType("jump");
        tileGrid[8][8].setType("vision");
        tileGrid[15][15].setType("jump");
        tileGrid[9][9].setType("jump");

        initImages();
        setImages();

        // rdz.antlion.Ant
        a = new Ant();

        // Event listeners
        addMouseListener(new MyMouseAdapter());
        addKeyListener(new MyKeyAdapter());
        setFocusable(true);

        // Timers
        timer = new Timer(33, this);
        timer.start();
    }

    // --Runtime-- //
    @Override // Calculations
    public void actionPerformed(ActionEvent arg0) {
        checkAnt();
        checkVision();


        repaint();
    }

    // Graphics
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        setBackground(Color.WHITE);

        AffineTransform af = g2.getTransform();
        g2.translate(-1 + MainApp.gridGap.x, -1 + MainApp.gridGap.y);
        for (Tile t : tileList) {
            t.tick();
            t.draw(g2);
        }
    }

    // --Methods-- //
    private void moveAnt(String dir, int dist) {
        switch (dir) {
            case "up": {
                if (checkMoves(dist)[0]) {
                    a.setRow(a.getRow() - dist);
                }
                break;
            }

            case "right": {
                if (checkMoves(dist)[1]) {
                    a.setCol(a.getCol() + dist);
                }
                break;
            }

            case "down": {
                if (checkMoves(dist)[2]) {
                    a.setRow(a.getRow() + dist);
                }
                break;
            }

            case "left": {
                if (checkMoves(dist)[3]) {
                    a.setCol(a.getCol() - dist);
                }
                break;
            }

            default: {
                System.out.println("Not a valid direction.");
            }
        }

        // checking for special tiles
        Tile curTile = tileGrid[a.getCol()][a.getRow()];
        if (curTile.getType() == "jump" && checkMoves(MainApp.jumpDist)[dirToNum(dir)]) {
            moveAnt(dir, MainApp.jumpDist);
        } else if (curTile.getType() == "vision") {
            MainApp.visionRange = MainApp.visionBoost;
        } else if (curTile.getType() == "one") {
            tileGrid[a.getCol()][a.getRow()].setType("used");
        }

        reduceVision();
    }

    private boolean[] checkMoves(int dist) {
        // up, right, down, left
        boolean[] canMove = {false, false, false, false};

        // Up
        if (a.getRow() != dist - 1) {
            String tUp = tileGrid[a.getCol()][a.getRow() - dist].getType();
            if (tUp != "wall" && tUp != "used") {
                canMove[0] = true;
            }
        }
        // Right
        if (a.getCol() != MainApp.gridSize.x - dist) {
            String tRight = tileGrid[a.getCol() + dist][a.getRow()].getType();
            if (tRight != "wall" && tRight != "used") {
                canMove[1] = true;
            }
        }
        // Down
        if (a.getRow() != MainApp.gridSize.y - dist) {
            String tDown = tileGrid[a.getCol()][a.getRow() + dist].getType();
            if (tDown != "wall" && tDown != "used") {
                canMove[2] = true;
            }
        }
        // Left
        if (a.getCol() != dist - 1) {
            String tLeft = tileGrid[a.getCol() - dist][a.getRow()].getType();
            if (tLeft != "wall" && tLeft != "used") {
                canMove[3] = true;
            }
        }

        return canMove;
    }

    private void checkAnt() {
        for (Tile t : tileList) {
            if (t.getRow() == a.getRow() && t.getCol() == a.getCol()) {
                t.setAnt(true);
            } else {
                t.setAnt(false);
            }
        }
    }

    private void checkVision() {
        for (Tile t : tileList) {
            if (Math.abs(t.getCol() - a.getCol()) < MainApp.visionRange - 1 && Math.abs(t.getRow() - a.getRow()) < MainApp.visionRange - 1) {
                t.setInRange(true);
            } else {
                t.setInRange(false);
            }
        }
    }

    private void reduceVision() {
        if (MainApp.visionRange != MainApp.realRange) {
            MainApp.visionRange--;
        }
    }

    // --Helpers-- //
    private void initImages() {
        // Walls
        wallImages = new Image[1];
        try {
            for (int i = 0; i < wallImages.length; i++) {
                wallImages[i] = ImageIO.read(new File("img\\wall" + i + ".png"));
            }
        } catch (IOException e) {
            System.out.println("ERROR: Wall");
            if (Util.DEBUG) {
                System.out.println(e.toString());
            }
        }
    }

    private void setImages() {
        for (Tile t : tileList) {
            if (t.getType() == "wall") {
                t.setImg(wallImages[0]);
            }
        }
    }

    private int dirToNum(String dir) {
        switch (dir) {
            case "up":
                return 0;

            case "right":
                return 1;

            case "down":
                return 2;

            case "left":
                return 3;

            default:
                return -1;
        }
    }
}
