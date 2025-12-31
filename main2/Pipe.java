package main2;

import java.awt.Rectangle;

public class Pipe {
    public int x;
    public int width;
    public int gapY;
    public int gapHeight;
    public int speed;
    public boolean passed = false;

    public Pipe(int x, int gapY, int gapHeight, int speed, int width) {
        this.x = x;
        this.gapY = gapY;
        this.gapHeight = gapHeight;
        this.speed = speed;
        this.width = width;
    }

    public void update() {
        x -= speed;
    }

    public Rectangle getTopRect() {
        return new Rectangle(x, 0, width, gapY);
    }

    public Rectangle getBottomRect() {
        return new Rectangle(x, gapY + gapHeight, width, 10000);
    }
}
