package main2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {
    public int x, y, width, height;
    double dy = 0;
    double gravity = 0.6;
    double jumpStrength = -12;

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update() {
        dy += gravity;
        y += (int) dy;
    }

    public void flap() {
        dy = jumpStrength;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.fillOval(x, y, width, height);
        g2.setColor(Color.ORANGE);
        g2.drawOval(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
