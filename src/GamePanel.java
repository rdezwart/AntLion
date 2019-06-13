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
import javax.swing.*;

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

            // R
            if (e.getKeyCode() == e.VK_R) {
                loadLevel(curLevel);
            }

            // Space
            if (e.getKeyCode() == e.VK_SPACE) {
                if (screen == "menu") {
                    screen = "game";
                    loadLevel(1);
                    System.out.println("SCREEN: Game");
                }

                if (screen == "end1") {
                    screen = "menu";
                    System.out.println("SCREEN: Menu");
                }
            }
        }
    }

    // --Fields-- //
    private MainApp app;

    private Tile[][] tileGrid;
    private ArrayList<Tile> tileList;
    private int curLevel;

    private String screen;

    private Timer timer;

    Ant a;

    private Image[] wallImages;
    private Image titleImage, end1Image;

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

        screen = "menu";
        curLevel = 1;
        loadLevel(curLevel);

        initImages();
        setImages();

        // Event listeners
        addMouseListener(new MyMouseAdapter());
        addKeyListener(new MyKeyAdapter());
        setFocusable(true);

        // Timers
        timer = new Timer(33, this);
        timer.start();
    }

    // --Runtime-- //
    @Override
    public void actionPerformed(ActionEvent arg0) {
        switch (screen) {
            case "menu": {

                break;
            }

            case "game": {
                checkAnt();
                checkVision();
                break;
            }

            case "end1": {
                break;
            }
        }

        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform af = g2.getTransform();

        switch (screen) {
            case "menu": {
                setBackground(Color.BLACK);

                g2.translate(MainApp.panelSize.width / 2, MainApp.panelSize.height / 2);
                if (titleImage != null) {
                    g2.drawImage(titleImage, -titleImage.getWidth(null) / 2, -titleImage.getHeight(null) / 2, null);
                }
                break;
            }

            case "game": {
                setBackground(Color.WHITE);

                g2.translate(-1 + MainApp.gridGap.x, -1 + MainApp.gridGap.y);
                for (Tile t : tileList) {
                    t.tick();
                    t.draw(g2);
                }

                break;
            }

            case "end1": {
                setBackground(Color.BLACK);

                g2.translate(MainApp.panelSize.width / 2, MainApp.panelSize.height / 2);
                if (end1Image != null) {
                    g2.drawImage(end1Image, -end1Image.getWidth(null) / 2, -end1Image.getHeight(null) / 2, null);
                }

                break;
            }
        }

        g2.setTransform(af);
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
        } else if (curTile.getType() == "land") {
            tileGrid[a.getCol()][a.getRow()].setType("slid");
            moveAnt(oppDir(dir), MainApp.landDist);
        } else if (curTile.getType() == "end") {
            if (curLevel == 1) {
                screen = "end1";
                System.out.println("SCREEN: End1");
            }
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
                wallImages[i] = ImageIO.read(GamePanel.class.getResourceAsStream("wall" + i + ".png"));
            }
        } catch (IOException e) {
            System.out.println("ERROR: Wall");
            if (Util.DEBUG) {
                System.out.println(e.toString());
            }
        }

        // Title
        try {
            titleImage = ImageIO.read(GamePanel.class.getResourceAsStream("title.png"));
        } catch (IOException e) {
            System.out.println("ERROR: Title");
            if (Util.DEBUG) {
                System.out.println(e.toString());
            }
        }

        // End1
        try {
            end1Image = ImageIO.read(GamePanel.class.getResourceAsStream("end1.png"));
        } catch (IOException e) {
            System.out.println("ERROR: End1");
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

    private String oppDir(String dir) {
        switch (dir) {
            case "up":
                return "down";

            case "right":
                return "left";

            case "down":
                return "up";

            case "left":
                return "right";

            default:
                return "invalid";
        }
    }

    private void loadLevel(int l) {
        if (l == 0) {
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

            a = new Ant(0, 8);
        }
        if (l == 1) {
            for (Tile t : tileList) {
                t.setType("wall");
            }

            tileGrid[0][9].setType("start");

            tileGrid[1][8].setType("floor");
            tileGrid[1][9].setType("vision");
            tileGrid[1][8].setType("floor");

            tileGrid[2][8].setType("floor");
            tileGrid[2][9].setType("jump");
            tileGrid[2][11].setType("floor");
            tileGrid[2][12].setType("floor");

            tileGrid[3][8].setType("floor");
            tileGrid[3][12].setType("floor");
            tileGrid[3][13].setType("floor");

            tileGrid[4][8].setType("floor");
            tileGrid[4][9].setType("floor");
            tileGrid[4][13].setType("floor");

            tileGrid[5][13].setType("floor");

            tileGrid[6][13].setType("vision");

            tileGrid[7][13].setType("floor");

            tileGrid[8][13].setType("jump");

            tileGrid[10][1].setType("floor");
            tileGrid[10][3].setType("jump");
            tileGrid[10][4].setType("floor");
            tileGrid[10][5].setType("jump");
            tileGrid[10][7].setType("vision");
            tileGrid[10][8].setType("jump");
            tileGrid[10][10].setType("floor");
            tileGrid[10][11].setType("floor");
            tileGrid[10][13].setType("floor");
            tileGrid[10][14].setType("floor");
            tileGrid[10][15].setType("vision");

            tileGrid[11][1].setType("jump");
            tileGrid[11][3].setType("floor");
            tileGrid[11][4].setType("floor");
            tileGrid[11][5].setType("floor");
            tileGrid[11][10].setType("jump");
            tileGrid[11][11].setType("floor");
            tileGrid[11][13].setType("jump");
            tileGrid[11][14].setType("floor");
            tileGrid[11][15].setType("floor");

            tileGrid[13][1].setType("floor");
            tileGrid[13][4].setType("jump");
            tileGrid[13][5].setType("floor");
            tileGrid[13][7].setType("jump");
            tileGrid[13][8].setType("floor");
            tileGrid[13][10].setType("floor");
            tileGrid[13][11].setType("floor");
            tileGrid[13][13].setType("floor");
            tileGrid[13][14].setType("jump");

            tileGrid[14][1].setType("jump");
            tileGrid[14][4].setType("floor");
            tileGrid[14][5].setType("floor");
            tileGrid[14][7].setType("floor");
            tileGrid[14][8].setType("floor");
            tileGrid[14][10].setType("jump");
            tileGrid[14][11].setType("floor");
            tileGrid[14][13].setType("floor");
            tileGrid[14][14].setType("floor");

            tileGrid[16][1].setType("vision");
            tileGrid[16][2].setType("vision");
            tileGrid[16][3].setType("one");
            tileGrid[16][4].setType("floor");
            tileGrid[16][5].setType("floor");
            tileGrid[16][6].setType("floor");
            tileGrid[16][10].setType("floor");
            tileGrid[16][11].setType("jump");

            tileGrid[17][6].setType("one");
            tileGrid[17][10].setType("floor");
            tileGrid[17][11].setType("vision");

            tileGrid[18][6].setType("vision");

            tileGrid[19][2].setType("floor");
            tileGrid[19][3].setType("floor");
            tileGrid[19][4].setType("floor");
            tileGrid[19][5].setType("floor");
            tileGrid[19][6].setType("vision");
            tileGrid[19][7].setType("floor");
            tileGrid[19][8].setType("floor");
            tileGrid[19][9].setType("floor");
            tileGrid[19][10].setType("floor");

            tileGrid[20][2].setType("floor");
            tileGrid[20][4].setType("one");
            tileGrid[20][6].setType("one");
            tileGrid[20][8].setType("one");

            tileGrid[21][2].setType("vision");
            tileGrid[21][3].setType("floor");
            tileGrid[21][4].setType("floor");
            tileGrid[21][6].setType("floor");
            tileGrid[21][8].setType("floor");
            tileGrid[21][9].setType("floor");
            tileGrid[21][10].setType("floor");

            tileGrid[22][2].setType("floor");
            tileGrid[22][6].setType("floor");
            tileGrid[22][10].setType("floor");

            tileGrid[23][2].setType("floor");
            tileGrid[23][3].setType("floor");
            tileGrid[23][4].setType("floor");
            tileGrid[23][5].setType("floor");
            tileGrid[23][6].setType("floor");
            tileGrid[23][8].setType("floor");
            tileGrid[23][9].setType("floor");
            tileGrid[23][10].setType("floor");

            tileGrid[24][8].setType("floor");

            tileGrid[25][6].setType("vision");
            tileGrid[25][7].setType("floor");
            tileGrid[25][8].setType("floor");
            tileGrid[25][9].setType("floor");
            tileGrid[25][10].setType("vision");

            tileGrid[26][6].setType("vision");
            tileGrid[26][10].setType("jump");

            tileGrid[27][6].setType("one");

            tileGrid[28][2].setType("vision");
            tileGrid[28][3].setType("floor");
            tileGrid[28][4].setType("floor");
            tileGrid[28][5].setType("floor");
            tileGrid[28][6].setType("floor");
            tileGrid[28][7].setType("floor");
            tileGrid[28][8].setType("floor");
            tileGrid[28][9].setType("floor");
            tileGrid[28][10].setType("floor");
            tileGrid[28][11].setType("floor");
            tileGrid[28][12].setType("vision");

            tileGrid[29][3].setType("floor");
            tileGrid[29][5].setType("floor");
            tileGrid[29][7].setType("floor");
            tileGrid[29][9].setType("floor");
            tileGrid[29][11].setType("floor");

            tileGrid[30][3].setType("land");
            tileGrid[30][5].setType("land");
            tileGrid[30][7].setType("land");
            tileGrid[30][9].setType("floor");
            tileGrid[30][11].setType("land");

            tileGrid[31][3].setType("floor");
            tileGrid[31][4].setType("floor");
            tileGrid[31][5].setType("floor");
            tileGrid[31][6].setType("floor");
            tileGrid[31][7].setType("floor");
            tileGrid[31][8].setType("floor");
            tileGrid[31][9].setType("floor");
            tileGrid[31][10].setType("floor");
            tileGrid[31][11].setType("floor");

            tileGrid[32][7].setType("end");

            a = new Ant(0, 9);
        }
    }
}
