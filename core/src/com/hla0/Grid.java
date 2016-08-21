package com.hla0;

import java.math.*;

public class Grid {
    private Square[][] squares;
    int width;
    int height;

    Grid (int width, int height) {
        this.width = width;
        this.height = height;
        squares = new Square[width][height];
        init();
    }

    void init() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                squares[i][j] = new Square(i,j,(int)(Math.random() * 8));
            }
        }
    }

    Square[][] getSquares() {
        return squares;
    }

    public int getWidth() {return width;}

    public int getHeight() {return height;}
}
