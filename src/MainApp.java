import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import processing.core.PVector;

@SuppressWarnings("serial")
public class MainApp extends JFrame {
    // --Static Fields-- //
    public static PVector gridSize = new PVector(33, 17);
    public static PVector tileSize = new PVector(30, 30);
    public static PVector gridGap = new PVector(0, 0);
    public static Dimension panelSize = new Dimension(
            (int) ((tileSize.x * gridSize.x) + (gridGap.x * (gridSize.x + 1) - 1)),
            (int) ((tileSize.y * gridSize.y) + (gridGap.y * (gridSize.y + 1) - 1)));
    public static Dimension windowSize = new Dimension((int) (panelSize.getWidth() + 100),
            (int) (panelSize.getHeight() + 100));

    public static final int realRange = 3;
    public static final int visionBoost = 11;
    public static final int jumpDist = 2;
    public static final int landDist = 2;
    public static int visionRange = realRange;
    public static int antRotation = 90;

    public static int tilesMoved = 0;
    public static int deathIndex = 0;

    // --Constructor-- //
    public MainApp(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gPanel = new GamePanel(this);

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        contentPane.add(gPanel, c);

        this.pack();
        this.setVisible(true);

        this.setPreferredSize(windowSize);
        this.pack();
        this.setVisible(true);
    }

    public void quit() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static void main(String[] args) {
        new Util();
        new MainApp("AntLion v1.0 - Team OOF");
    }

}
