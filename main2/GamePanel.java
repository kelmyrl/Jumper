package main2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;


import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

     // Screen settings
    final int originalTileSize = 16; // 16 by 16 title
    final int scale = 3; // 3 times the original size

    public final int tileSize = originalTileSize * scale; // 48 by 48 tile
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 960 pixels  
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    //World Settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol; // 800 pixels
    public final int worldHeight = tileSize * maxWorldRow; // 800 pixels

    //FOR FULL SCREEN
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean FullScreenOn = false;

    //FPS
    int FPS = 60;

    Thread gameThread;
    Player player;
    ArrayList<Pipe> pipes = new ArrayList<>();
    int pipeSpawnTimer = 0;
    int pipeSpawnInterval = 90; // frames
    int pipeSpeed = 4;
    int score = 0;
    boolean gameOver = false;

    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);


    }

    @Override
    public void addNotify() {
        super.addNotify();
        setupGame();
        startGameThread();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (gameOver) restart();
                    else player.flap();
                }
            }
        });
    }

     public void setupGame() {

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) tempScreen.getGraphics();

        player = new Player(100, screenHeight/2 - 12, 34, 24);
        pipes.clear();
        pipeSpawnTimer = 0;
        score = 0;
        gameOver = false;

        if(FullScreenOn == true) {
            setFullScreen();
        }
    }

    public void setFullScreen() {
        // GET LOCAL SCREEN DEVICE
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        // GET FULL SCREEN WIDTH AND HEIGHT
        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();

    }

    public void startGameThread(){

        gameThread = new Thread(this);
        gameThread.start();
    }



     @Override
    public void run() {

        double drawInterval = 1000000000 / FPS; //0.01666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;

        
        while(gameThread != null){
            
            // 1 UPDATE: update information such as character positions
            update();

            // 2 DRAW: draw the updated information on the screen
            drawToTempScreen();
            drawToScreen();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // convert to milliseconds

                if(remainingTime < 0){
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }

    }

    void restart() {
        setupGame();
    }

    public void update() {
        if (!gameOver) {
            player.update();

            pipeSpawnTimer++;
            if (pipeSpawnTimer >= pipeSpawnInterval) {
                pipeSpawnTimer = 0;
                int gap = 140;
                int minY = 60;
                int maxY = screenHeight - 60 - gap;
                int gapY = minY + (int)(Math.random() * (maxY - minY + 1));
                pipes.add(new Pipe(screenWidth, gapY, gap, pipeSpeed, 80));
            }

            ArrayList<Pipe> removeList = new ArrayList<>();
            for (Pipe p : pipes) {
                p.update();
                if (!p.passed && p.x + p.width < player.x) {
                    p.passed = true;
                    score++;
                }
                if (p.x + p.width < 0) removeList.add(p);
                if (p.getTopRect().intersects(player.getBounds()) || p.getBottomRect().intersects(player.getBounds())) {
                    gameOver = true;
                }
            }
            pipes.removeAll(removeList);

            if (player.y + player.height >= screenHeight - 80 || player.y <= 0) {
                gameOver = true;
            }
        }
    }

    public void drawToTempScreen() {
        // sky
        g2.setColor(new Color(135, 206, 235));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // ground
        g2.setColor(new Color(80, 200, 80));
        g2.fillRect(0, screenHeight - 80, screenWidth, 80);

        // draw player
        player.draw(g2);

        // draw pipes
        g2.setColor(new Color(34, 139, 34));
        for (Pipe p : pipes) {
            Rectangle top = p.getTopRect();
            Rectangle bot = p.getBottomRect();
            g2.fillRect(top.x, top.y, top.width, top.height);
            g2.fillRect(bot.x, bot.y, bot.width, bot.height);
        }

        // score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.drawString("Score: " + score, 20, 40);

        if (gameOver) {
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(Color.RED);
            g2.drawString("Game Over", screenWidth/2 - 150, screenHeight/2 - 20);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.WHITE);
            g2.drawString("Press SPACE to restart", screenWidth/2 - 120, screenHeight/2 + 20);
        }
    }

    public void drawToScreen() {
        Graphics g = this.getGraphics();
        if (g != null) {
            g.drawImage(tempScreen, 0, 0, null);
            g.dispose();
        }
    }
}
