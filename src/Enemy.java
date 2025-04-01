import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class Enemy {
    public int x, y, width, height;
    public int speed = 2;
    public boolean movingLeft = true;
    public boolean alive = true;

    private int minX, maxX; // patrol range
    private Image sprite;

    public Enemy(int x, int y, int minX, int maxX) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.minX = minX;
        this.maxX = maxX;

        try {
            sprite = ImageIO.read(new File("res/enemy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void update() {
        if (!alive) return;

        if (movingLeft) {
            x -= speed;
            if (x <= minX) {
                x = minX;
                movingLeft = false;
            }
        } else {
            x += speed;
            if (x + width >= maxX) {
                x = maxX - width;
                movingLeft = true;
            }
        }
    }

    public void draw(Graphics g) {
        if (!alive) return;

        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            g.setColor(Color.MAGENTA); // fallback if image didn't load
            g.fillRect(x, y, width, height);
        }
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
