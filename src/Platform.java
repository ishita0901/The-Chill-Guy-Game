import java.awt.*;

public class Platform {
    public int x, y, width, height;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g, int cameraX) {
        g.setColor(new Color(120, 72, 36));
        g.fillRect(x - cameraX, y, width, height);
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
