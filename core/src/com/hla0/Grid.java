package com.hla0;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.math.*;

public class Grid extends InputAdapter{
    private Square[][] squares;
    Square selected;
    Square selected2;
    int width;
    int height;
    Viewport viewport;

    Grid (int width, int height, Viewport viewport) {
        this.width = width;
        this.height = height;
        squares = new Square[width][height];
        this.viewport = viewport;
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

    //deletions (depends on mode) match 3 and explode with black

    //rearrange column so that blocks have fallen
    //private void fixColumn(int col) {;}

    //modifying colors
    public void swapLineColors(Square s1, Square s2, boolean onX) {
        boolean haveRed = s1.haveRed;
        boolean haveBlue = s1.haveBlue;
        boolean haveYellow = s1.haveYellow;
        System.out.println("swapping lines");
        if (onX) {
            System.out.println("x are equal" + s1.x + " " + s2.x);
            int x = s1.x;
            //swap down
            if (s1.y > s2.y) {
                for (int i = s1.y - 1; i >= s2.y; i--) {
                    System.out.println("swapping " + x + " " + i);
                    squares[x][i].swapColor(haveRed, haveBlue, haveYellow);
                }
            }
            //swap up
            else {
                for (int i = s1.y + 1; i <= s2.y; i++) {
                    System.out.println("swapping " + x + " " + i);
                    squares[x][i].swapColor(haveRed, haveBlue, haveYellow);
                }
            }
        }
        else {
            int y = s1.y;
            System.out.println("y are equal" + s1.y + " " + s2.y);
            //swap left
            if (s1.x > s2.x) {
                System.out.println("x left: " + s1.x + " " + s2.x);
                for (int i = s1.x - 1; i >= s2.x; i--) {
                    System.out.println("swapping " + i + " " + y);
                    squares[i][y].swapColor(haveRed, haveBlue, haveYellow);
                }
            }
            //swap right
            else {
                System.out.println("x right: " + s1.x + " " + s2.x);
                for (int i = s1.x + 1; i <= s2.x; i++) {
                    System.out.println("swapping " + i + " " + y);
                    squares[i][y].swapColor(haveRed, haveBlue, haveYellow);
                }
            }
        }
        System.out.println("finished swapping");
    }



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println(screenX + ", " + screenY);
        Vector2 v = transformToGrid(viewport.unproject(new Vector2(screenX,screenY)));
        boolean withinGrid = false;
        //removes margins from grid with some flexibility for slightly off touches
        //need to cover out of grid
        if (v.x >= getWidth() && v.x >= 0 && v.y >= getHeight() && v.y >= 0) {
            System.out.println("Outside grid");
            withinGrid = false;
        }
        else if (v.x % 1 >= .07 && v.x % 1 <= .93 && v.y % 1 >= .07 && v.y % 1 <= .93) {
            System.out.println("Obtained grid coordinates: (" + (int)v.x + ", " + (int)v.y + ")");
            processTouch((int)v.x, (int)v.y);
            withinGrid = true;
        }
        else {
            System.out.println("Outside grid");
            withinGrid = false;
        }
        return withinGrid;
    }

    public Vector2 transformToGrid(Vector2 v1) {

        Vector2 v = new Vector2((v1.x - (Constants.margin / 2))/(Constants.boxSize + Constants.margin),
                (v1.y - Constants.bottomPadding - (Constants.margin / 2)) / (Constants.boxSize + Constants.margin));
        return v;
    }

    public void processTouch(int x, int y) {
        boolean onX = false;
        if (selected == null) {
            selected = squares[x][y];
        }
        else {
            //same square
            if (selected.getX() == x && selected.getY() == y) {
                selected = null;
            }
            //on same column
            else if (selected.getX() == x) {
                selected2 = squares[x][y];
                onX = true;
            }
            //on same row
            else if (selected.getY() == y) {
                selected2 = squares[x][y];
                onX = false;
            }
            //not in the same row or column
            else {
                selected = squares[x][y];
            }
        }
        if (selected2 != null) {
            swapLineColors(selected, selected2, onX);
            System.out.println("got second square");
            selected = null;
            selected2 = null;
        }
    }

}
