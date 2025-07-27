import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Dimension;
import java.util.Random;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private Thread gameThread;
    private volatile boolean running;

    private int playerX, playerY;
    private final int PLAYER_SPEED = 5;
    private final int PLAYER_SIZE = 50;

    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;

    private long lastEnemySpawnTime;
    private final long ENEMY_SPAWN_DELAY = 1500;

    private Random random;
    private boolean gameOver;
    private int score;

    private Rectangle replayButtonBounds;
    private final String REPLAY_TEXT = "Replay";
    private final int BUTTON_WIDTH = 150;
    private final int BUTTON_HEIGHT = 50;

    private BufferedImage playerImage;
    private BufferedImage enemyImage;
    private BufferedImage bulletImage;

    public GamePanel() {
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new GameKeyListener());
        this.addMouseListener(new GameMouseListener());

        playerX = (GAME_WIDTH - PLAYER_SIZE) / 2;
        playerY = GAME_HEIGHT - PLAYER_SIZE - 20;

        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        random = new Random();

        lastEnemySpawnTime = System.currentTimeMillis();
        gameOver = false;
        score = 0;

        int buttonX = (GAME_WIDTH - BUTTON_WIDTH) / 2;
        int buttonY = (GAME_HEIGHT / 2) + 50;
        replayButtonBounds = new Rectangle(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Images load කරනවා
        try {
            playerImage = ImageIO.read(getClass().getClassLoader().getResource("images/rocket.png"));
            enemyImage = ImageIO.read(getClass().getClassLoader().getResource("images/rocket.png"));
            bulletImage = ImageIO.read(getClass().getClassLoader().getResource("images/star.png"));
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        startGame();
    }

    public void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                if (!gameOver) {
                    updateGame();
                }
                repaint();
                delta--;
            }
        }
    }

    private void updateGame() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.move();
            if (!bullet.isActive()) {
                bulletIterator.remove();
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemySpawnTime > ENEMY_SPAWN_DELAY) {
            enemies.add(new Enemy(GAME_WIDTH));
            lastEnemySpawnTime = currentTime;
        }

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.move();
            if (!enemy.isActive()) {
                enemyIterator.remove();
            }
        }

        // Collision Detection
        Iterator<Bullet> bulletCollisionIterator = bullets.iterator();
        while (bulletCollisionIterator.hasNext()) {
            Bullet bullet = bulletCollisionIterator.next();

            Iterator<Enemy> enemyCollisionIterator = enemies.iterator();
            while (enemyCollisionIterator.hasNext()) {
                Enemy enemy = enemyCollisionIterator.next();

                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    bulletCollisionIterator.remove();
                    enemyCollisionIterator.remove();
                    score += 10;
                    break;
                }
            }
        }

        Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

        for (Enemy enemy : enemies) {
            Rectangle enemyRect = enemy.getBounds();

            if (playerRect.intersects(enemyRect)) {
                gameOver = true;
                System.out.println("Game Over! Final Score: " + score);
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);

        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String gameOverText = "GAME OVER!";

            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int x = (GAME_WIDTH - metrics.stringWidth(gameOverText)) / 2;
            int y = (GAME_HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(gameOverText, x, y);

            g.setColor(Color.GREEN);
            g.fillRect(replayButtonBounds.x, replayButtonBounds.y, replayButtonBounds.width, replayButtonBounds.height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics buttonMetrics = g.getFontMetrics(g.getFont());
            int textX = replayButtonBounds.x + (replayButtonBounds.width - buttonMetrics.stringWidth(REPLAY_TEXT)) / 2;
            int textY = replayButtonBounds.y + ((replayButtonBounds.height - buttonMetrics.getHeight()) / 2) + buttonMetrics.getAscent();
            g.drawString(REPLAY_TEXT, textX, textY);
        }
    }

    private void draw(Graphics g) {
        // Player අඳිමු
        if (playerImage != null) {
            g.drawImage(playerImage, playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, this);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
        }

        // Bullets අඳිමු
        for (Bullet bullet : bullets) {
            if (bulletImage != null && bullet.isActive()) {
                g.drawImage(bulletImage, bullet.x, bullet.y, Bullet.WIDTH, Bullet.HEIGHT, this);
            } else if (bullet.isActive()) {
                g.setColor(Color.YELLOW);
                g.fillRect(bullet.x, bullet.y, Bullet.WIDTH, Bullet.HEIGHT);
            }
        }

        // Enemies අඳිමු
        for (Enemy enemy : enemies) {
            if (enemyImage != null && enemy.isActive()) {
                // Enemy class එකේ Getter methods භාවිතා කරනවා
                g.drawImage(enemyImage, enemy.getX(), enemy.getY(), enemy.getSize(), enemy.getSize(), this);
            } else if (enemy.isActive()) {
                g.setColor(Color.RED);
                // Enemy class එකේ Getter methods භාවිතා කරනවා
                g.fillRect(enemy.getX(), enemy.getY(), enemy.getSize(), enemy.getSize());
            }
        }
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameOver) return;

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                playerX -= PLAYER_SPEED;
                if (playerX < 0) playerX = 0;
            }
            if (key == KeyEvent.VK_RIGHT) {
                playerX += PLAYER_SPEED;
                if (playerX > GAME_WIDTH - PLAYER_SIZE) playerX = GAME_WIDTH - PLAYER_SIZE;
            }
            if (key == KeyEvent.VK_SPACE) {
                bullets.add(new Bullet(playerX + PLAYER_SIZE / 2 - Bullet.WIDTH / 2, playerY));
            }
        }
    }

    private void resetGame() {
        playerX = (GAME_WIDTH - PLAYER_SIZE) / 2;
        playerY = GAME_HEIGHT - PLAYER_SIZE - 20;

        bullets.clear();
        enemies.clear();

        score = 0;
        gameOver = false;
        lastEnemySpawnTime = System.currentTimeMillis();
    }

    private class GameMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (gameOver) {
                if (replayButtonBounds.contains(e.getPoint())) {
                    resetGame();
                }
            }
        }
    }
}