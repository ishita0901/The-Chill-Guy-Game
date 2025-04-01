//while (running) {
//    update();   // move player, apply gravity, check collisions
//    render();   // draw background, tiles, player, enemies
//}

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Game extends Canvas implements Runnable {

    private Image playerSprite;
    private int playerX = 100;
    private int playerY = 500;
    private int playerWidth = 40;
    private int playerHeight = 50;
    private int playerSpeed = 5;

    //gravity related vars
    private int gravity = 1;
    private int maxFallSpeed = 10;
    private int jumpStrength = -15;

    private int velocityY = 0;
    private boolean jumping = false;

    // Ground level (just a simple y-value for now)
    private int groundY = 550; // adjust depending on your sprite height
    //

    private ArrayList<Coin> coins = new ArrayList<>();
    private int score = 0;


    private boolean movingLeft = false;
    private boolean movingRight = false;

    private Thread thread;
    private boolean running = false;

    private ArrayList<Platform> platforms = new ArrayList<>();


    public Game() {
        JFrame frame = new JFrame("The Chill Guy");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.setVisible(true);
        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                    movingLeft = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                    movingRight = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!jumping) {
                        velocityY = jumpStrength;
                        jumping = true;
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                    movingLeft = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                    movingRight = false;
                }
            }
        });

        try {
            playerSprite = ImageIO.read(new File("res/chillguy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        platforms.add(new Platform(200, 450, 100, 20));
        platforms.add(new Platform(400, 350, 120, 20));
        platforms.add(new Platform(600, 300, 80, 20));

        coins.add(new Coin(220, 420)); // on first platform
        coins.add(new Coin(420, 320)); // on second
        coins.add(new Coin(620, 270)); // on third


    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Game loop
        this.createBufferStrategy(3); // triple buffering
        BufferStrategy bs = this.getBufferStrategy();

        while (running) {
            update();
            render(bs);
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    private void update() {
        if (movingLeft) {
            playerX -= playerSpeed;
        }
        if (movingRight) {
            playerX += playerSpeed;
        }
        // Apply gravity
        velocityY += gravity;
        if (velocityY > maxFallSpeed) velocityY = maxFallSpeed;

        playerY += velocityY;

        Rectangle playerBounds = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        for (Platform platform : platforms) {
            Rectangle platBounds = platform.getBounds();

            //polishing platform collisions
            if (playerBounds.intersects(platBounds)) {
                // Landing on top of the platform
                if (velocityY > 0 && playerY + playerHeight - velocityY <= platform.y) {
                    playerY = platform.y - playerHeight;
                    velocityY = 0;
                    jumping = false;
                }

                // Hitting the bottom of the platform while jumping
                else if (velocityY < 0 && playerY >= platform.y + platform.height - 5) {
                    playerY = platform.y + platform.height;
                    velocityY = 0;
                }

                // Hitting the platform from the sides (basic)
                else if (playerX + playerWidth > platform.x && playerX < platform.x + platform.width) {
                    if (playerX < platform.x) {
                        playerX = platform.x - playerWidth;
                    } else {
                        playerX = platform.x + platform.width;
                    }
                }
            }
        }


//        Rectangle playerBounds = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        for (Coin coin : coins) {
            if (!coin.collected && playerBounds.intersects(coin.getBounds())) {
                coin.collected = true;
                score++;
                System.out.println("Score: " + score);
            }
        }


// Check ground collision
        if (playerY + playerHeight >= groundY) {
            playerY = groundY - playerHeight;
            velocityY = 0;
            jumping = false;
        }
    }

    private void render(BufferStrategy bs) {
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, getWidth(), getHeight()); // clear screen
        g.drawImage(playerSprite, playerX, playerY, playerWidth, playerHeight, null);

        // Ground
        g.setColor(Color.GREEN);
        g.fillRect(0, groundY, getWidth(), getHeight() - groundY);

        for (Platform platform : platforms) {
            platform.draw(g);
        }
        for (Coin coin : coins) {
            coin.draw(g);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 40);


        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
