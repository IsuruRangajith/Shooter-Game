import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.awt.Rectangle;

public class Enemy {
    private int x, y; // private ලෙසම තබා ඇත
    private int speed;
    private final int SIZE = 40; // private final ලෙසම තබා ඇත
    private boolean active;

    public Enemy(int gameWidth) {
        Random random = new Random();
        this.x = random.nextInt(gameWidth - SIZE);
        this.y = -SIZE;
        this.speed = 2 + random.nextInt(3);
        this.active = true;
    }

    public void move() {
        y += speed;
        if (y > GamePanel.GAME_HEIGHT) {
            active = false;
        }
    }

    // මෙය GamePanel එකේ Image drawing නොමැති විට භාවිතා වේ
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, SIZE, SIZE);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    // *** අලුතින් එකතු කරන Getter Methods ***
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return SIZE;
    }
}