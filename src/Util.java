import java.awt.Color;

public class Util {
    // --Fields-- //
    public final static boolean DEBUG = true;
    public final static boolean REVEAL = false;

    // --Constructor(s)-- //
    // Default
    public Util() {

    }

    // --Methods-- //
    public static float random(double min, double max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static float random(double max) {
        return (float) (Math.random() * (max));
    }

    public static Color randomColor() {
        int r = (int) random(255);
        int g = (int) random(255);
        int b = (int) random(255);

        return new Color(r, g, b);
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
