package rdz.antlion;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

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
                moveAnt("up");
            }
            // Right
            if (e.getKeyCode() == e.VK_D || e.getKeyCode() == e.VK_RIGHT) {
                moveAnt("right");
            }
            // Down
            if (e.getKeyCode() == e.VK_S || e.getKeyCode() == e.VK_DOWN) {
                moveAnt("down");
            }
            // Left
            if (e.getKeyCode() == e.VK_A || e.getKeyCode() == e.VK_LEFT) {
                moveAnt("left");
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

        // rdz.antlion.rdz.antlion.Ant
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
    private void moveAnt(String dir) {
        if (dir == "up") {
            a.setRow(a.getRow() - 1);
        } else if (dir == "down") {
            a.setRow(a.getRow() + 1);
        } else if (dir == "right") {
            a.setCol(a.getCol() + 1);
        } else if (dir == "left") {
            a.setCol(a.getCol() - 1);
        } else {
            System.out.println("Not a valid direction.");
        }
    }

    public void checkAnt() {
        for (Tile t : tileList) {
            if (t.getRow() == a.getRow() && t.getCol() == a.getCol()) {
                t.setAnt(true);
            } else {
                t.setAnt(false);
            }
        }
    }

    // --Helpers-- //
}
