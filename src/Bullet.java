import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet {
    public int x, y; // GamePanel එකෙන් කෙලින්ම access කිරීමට public ලෙස තබා ඇත
    private final int SPEED = 10;
    public static final int WIDTH = 5;
    public static final int HEIGHT = 15;
    private boolean active;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
        this.active = true;
    }

    public void move() {
        y -= SPEED;
        if (y < 0) {
            active = false;
        }
    }

    // මෙය GamePanel එකේ Image drawing නොමැති විට භාවිතා වේ
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}