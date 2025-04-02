import java.awt.*;

public class Coin {
    public int x, y;
    public int size = 20;
    public boolean collected = false;

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, int cameraX) {
        if (!collected) {
            g.setColor(Color.YELLOW);
            g.fillOval(x - cameraX, y, size, size);
        }
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
