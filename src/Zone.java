import java.awt.Rectangle;

public class Zone {
    public String name;
    public Rectangle bounds;

    public Zone(String name, int x, int y, int width, int height) {
        this.name = name;
        this.bounds = new Rectangle(x, y, width, height);
    }
}