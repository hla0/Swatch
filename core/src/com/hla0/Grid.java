package com.hla0;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.math.*;
import java.util.ArrayList;

public class Grid extends InputAdapter{
    private Square[][] squares;
    Square selected;
    Square selected2;
    int width;
    int height;
    Viewport viewport;
    boolean[] columnChanged;
    //check the state of all squares
    boolean animating;
    int[] colorDestroyed;
    int totalDestroyed;
    int moves;
    //y screen index where new squares would be added
    int top;
    ArrayList<Square> toDelete;
    ArrayList<Square> toSwap;
    //was animating but finished

    Grid (int width, int height, Viewport viewport) {
        this.width = width;
        this.height = height;
        squares = new Square[width][height];
        columnChanged = new boolean[width];
        colorDestroyed = new int[Constants.numColors];
        this.viewport = viewport;
        top = Constants.bottomPadding + (height + 1) * (Constants.boxSize + Constants.margin);
        animating = false;
        moves = 0;
        totalDestroyed = 0;
        toDelete = new ArrayList<Square>();
        toSwap = new ArrayList<Square>();
        init();
    }

    void init() {
        generateWithoutChains();
        for (int i = 0; i < Constants.numColors; i++) {
            colorDestroyed[i] = 0;
        }
    }

    //create a grid without match3
    void generateWithoutChains() {
        int colorLeft = -1;
        int colorUp = -1;
        int randomColor;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                randomColor = (int)(Math.random() * Constants.numColors);
                if (i > 1) {
                    if (squares[i-1][j].getColorNum() == squares[i-2][j].getColorNum()) {
                        colorLeft = squares[i-1][j].getColorNum();
                    }
                }
                if (j > 1) {
                    if (squares[i][j-1].getColorNum() == squares[i][j-2].getColorNum()) {
                        colorUp = squares[i][j-1].getColorNum();
                    }
                }
                while (randomColor == colorLeft || randomColor == colorUp) {
                    randomColor = (int)(Math.random() * Constants.numColors);
                }
                squares[i][j] = new Square(i, j, randomColor);
            }
        }
    }

    Square[][] getSquares() {
        return squares;
    }

    public int getWidth() {return width;}

    public int getHeight() {return height;}

    //deletions (depends on mode) match 3 and explode with white
    //remove the Square from the grid
    public void removeSquare(Square s) {
        animating = true;
        if (s != null) {
            columnChanged[s.x] = true;
            colorDestroyed[s.getColorNum()]++;
            totalDestroyed++;
            //check white
            if (s.getColorNum() == 1) {
                if (squares[s.x][s.y] != null) {
                    toDelete.add(squares[s.x][s.y]);
                }
                squares[s.x][s.y] = null;
                if (s.x + 1 < getWidth()) {
                    removeSquare(squares[s.x + 1][s.y]);
                }
                if (s.x > 0) {
                    removeSquare(squares[s.x - 1][s.y]);
                }
                if (s.y + 1 < getHeight()) {
                    removeSquare(squares[s.x][s.y + 1]);
                }
                if (s.y > 0) {
                    removeSquare(squares[s.x][s.y - 1]);
                }
            } else {
                if (squares[s.x][s.y] != null) {
                    toDelete.add(squares[s.x][s.y]);
                }
                squares[s.x][s.y] = null;
            }
        }
    }

    //rearrange column so that blocks have fallen
    public void updateColumns() {
        for (int i = 0; i < width; i++) {
            if (columnChanged[i]) {
                updateColumn(i);
                columnChanged[i] = false;
            }
        }
    }

    //fix for removed squares in column
    public void updateColumn(int col) {
        animating = true;
        boolean above = true;
        int squareCount = 0;
        for (int i = 0; i < getHeight(); i++) {
            if (squares[col][i] == null) {
                //try finding squares above
                if (above) {
                    Square s = findSquareAbove(col, i);
                    //did not find square above
                    if (s == null) {
                        squares[col][i] = new Square(col,i,(int)(Math.random() * Constants.numColors));
                        squares[col][i].pos.y = top + squareCount * (Constants.boxSize + Constants.margin);
                        squareCount++;
                        above = false;
                    }
                    //found next square above
                    //moving square down and removing old copy
                    else {
                        squares[s.x][s.y] = null;
                        s.moveTo(col,i);
                        squares[col][i] = s;
                    }
                }
                //no squares above
                else {
                    squares[col][i] = new Square(col,i,(int)(Math.random() * Constants.numColors));
                    squares[col][i].pos.y = top + squareCount * (Constants.boxSize + Constants.margin);
                    squareCount++;
                }
                //can add parameters to animate fall or swap
                squares[col][i].animate();
            }
        }
    }

    public Square findSquareAbove(int col, int index) {
        Square s = null;
        for (int i = index + 1; i < getHeight(); i++) {
            if (squares[col][i] != null) {
                return squares[col][i];
            }
        }
        return s;
    }

    //TODO animate swap colors
    //modifying colors
    public void swapLineColors(Square s1, Square s2, boolean onX) {
        animating = true;
        boolean haveRed = s1.haveRed;
        boolean haveBlue = s1.haveBlue;
        boolean haveYellow = s1.haveYellow;
        System.out.println("swapping lines");

        //TODO need to add a way to check animating has finished before uncommenting
        if (onX) {
            System.out.println("x are equal" + s1.x + " " + s2.x);
            int x = s1.x;
            //swap down
            if (s1.y > s2.y) {
                for (int i = s1.y - 1; i >= s2.y; i--) {
                    System.out.println("swapping " + x + " " + i);
                    toSwap.add(new Square(x,i,squares[x][i].getColorNum()));
                    squares[x][i].swapColor(haveRed, haveBlue, haveYellow);
                }
            }
            //swap up
            else {
                for (int i = s1.y + 1; i <= s2.y; i++) {
                    System.out.println("swapping " + x + " " + i);
                    toSwap.add(new Square(x,i,squares[x][i].getColorNum()));
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
                    toSwap.add(new Square(i,y,squares[i][y].getColorNum()));
                    squares[i][y].swapColor(haveRed, haveBlue, haveYellow);
                }
            }
            //swap right
            else {
                System.out.println("x right: " + s1.x + " " + s2.x);
                for (int i = s1.x + 1; i <= s2.x; i++) {
                    System.out.println("swapping " + i + " " + y);
                    toSwap.add(new Square(i,y,squares[i][y].getColorNum()));
                    squares[i][y].swapColor(haveRed, haveBlue, haveYellow);
                }
            }
        }
        //start checking for matches and removing
        System.out.println("finished swapping");
        //TODO fix continuous checking
        //check after swapping and falling e.g. end of animating
        // will overflow currently because no removals occur
    }

    //TODO check if there are any match 3s and remove them
    //should be called after swapLines and updateColumns
    public boolean checkMatches() {
        boolean match = false;
        for (int i = 0; i < getHeight(); i++) {
            if (scanHorizontal(i)) {
                match = true;
                System.out.println("Found horizontal match on row " + i);
            }
        }
        for (int i = 0; i < getWidth(); i++) {
            if (scanVertical(i)) {
                match = true;
                System.out.println("Found vertical match on column " + i);
            }
        }
        removeMatches();
        return match;
    }

    public boolean scanHorizontal(int row) {
        int curColor = -1;
        int count = 0;
        boolean match = false;
        Square left1 = null;
        Square left2 = null;
        Square right1 = null;
        Square right2 = null;

        for (int i = 0; i < getWidth(); i++) {
            Square curSquare = squares[i][row];
            if (curSquare != null) {
                count = 1;
                curColor = curSquare.getColorNum();
                if (i - 2 >= 0) {
                    left2 = squares[i - 2][row];
                }
                if (i - 1 >= 0) {
                    left1 = squares[i - 1][row];
                }
                if (i + 2 < getWidth()) {
                    right2 = squares[i + 2][row];
                }
                if (i + 1 < getWidth()) {
                    right1 = squares[i + 1][row];
                }
                if (left1 != null && left1.getColorNum() == curColor) {
                    count++;
                    if (left2 != null && left2.getColorNum() == curColor) {
                        count++;
                    }
                }
                if (right1 != null && right1.getColorNum() == curColor) {
                    count++;
                    if (right2 != null && right2.getColorNum() == curColor) {
                        count++;
                    }
                }
                if (count >= 3) {
                    squares[i][row].setHorizontalMatch(count);
                    System.out.println(count + "Found horizontal match for " + curSquare.getX() + ", " + curSquare.getY());
                    match = true;
                }
                count = 0;
                left1 = null;
                left2 = null;
                right1 = null;
                right2 = null;
            }
        }
        return match;
    }


    public boolean scanVertical(int col) {
        int curColor = -1;
        int count = 0;
        boolean match = false;
        Square up1 = null;
        Square up2 = null;
        Square down1 = null;
        Square down2 = null;

        for (int i = 0; i < getHeight(); i++) {
            Square curSquare = squares[col][i];
            if (curSquare != null) {
                count = 1;
                curColor = curSquare.getColorNum();
                if (i + 2 < getHeight()) {
                    up2 = squares[col][i + 2];
                }
                if (i + 1 < getHeight()) {
                    up1 = squares[col][i + 1];
                }
                if (i - 2 >= 0) {
                    down2 = squares[col][i - 2];
                }
                if (i - 1 >= 0) {
                    down1 = squares[col][i - 1];
                }
                if (down1 != null && down1.getColorNum() == curColor) {
                    count++;
                    if (down2 != null && down2.getColorNum() == curColor) {
                        count++;
                    }
                }
                if (up1 != null && up1.getColorNum() == curColor) {
                    count++;
                    if (up2 != null && up2.getColorNum() == curColor) {
                        count++;
                    }
                }
                if (count >= 3) {
                    squares[col][i].setVerticalMatch(count);
                    System.out.println(count + "Found vertical match for " + curSquare.getX() + ", " + curSquare.getY());
                    match = true;
                }

                count = 0;
                up1 = null;
                up2 = null;
                down1 = null;
                down1 = null;
            }
        }
        return match;
    }

    //TODO remove all matches from the grid
    //need to have square have data marking it with match
    //if squares have a certain criteria (match horizontal and vertical) match 4 or 5
    public void removeMatches() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (squares[i][j].getHorizontalMatch() >= 3 || squares[i][j].getVerticalMatch() >= 3) {
                    removeSquare(squares[i][j]);
                }
            }
        }
    }


    public ArrayList<Square> getDeleted() {
        return toDelete;
    }

    public ArrayList<Square> getSwapped() {
        return toSwap;
    }

    public boolean isAnimating() {
        if (toDelete.size() > 0) {
            return true;
        }
        if (toSwap.size() > 0) {
            return true;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (squares[i][j] != null && squares[i][j].isAnimating()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //do not process touches when grid is animating
        if (animating) {
            animating = isAnimating();
        }
        if (!animating) {
            Vector2 v = transformToGrid(viewport.unproject(new Vector2(screenX, screenY)));
            boolean withinGrid = false;
            //removes margins from grid with some flexibility for slightly off touches
            if (v.x % 1 >= .07 && v.x % 1 <= .93 && v.y % 1 >= .07 && v.y % 1 <= .93) {
                if (v.x >= getWidth() || v.x < 0 || v.y >= getHeight() || v.y < 0) {
                    System.out.println("Outside grid");
                    withinGrid = false;
                } else {
                    System.out.println("Obtained grid coordinates: (" + (int) v.x + ", " + (int) v.y + ")");
                    processTouch((int) v.x, (int) v.y);
                    withinGrid = true;
                }
            } else {
                System.out.println("Outside grid");
                withinGrid = false;
            }
            return withinGrid;
        }
        return false;
    }

    public Vector2 transformToGrid(Vector2 v1) {
        Vector2 v = new Vector2((v1.x - (Constants.margin / 2))/(Constants.boxSize + Constants.margin),
                (v1.y - Constants.bottomPadding - (Constants.margin / 2)) / (Constants.boxSize + Constants.margin));
        return v;
    }

    //where there is selected = *; change status of the variable within square
    public void processTouch(int x, int y) {
        boolean onX = false;
        if (selected == null) {
            //square is white
            if (squares[x][y].getColorNum() == 1) {
                removeSquare(squares[x][y]);
                updateColumns();
                moves++;
            }
            else {
                selected = squares[x][y];
                squares[x][y].setSelect(true);
            }
        }
        else {
            //same square
            if (selected.getX() == x && selected.getY() == y) {
                selected = null;
                squares[x][y].setSelect(false);
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
                squares[selected.x][selected.y].setSelect(false);
                selected = null;
                //square is white
                if (squares[x][y].getColorNum() == 1) {
                    removeSquare(squares[x][y]);
                    updateColumns();
                    moves++;
                }
                else {
                    selected = squares[x][y];
                    squares[x][y].setSelect(true);
                }
            }
        }
        //second square on same row or column is clicked
        if (selected2 != null) {
            swapLineColors(selected, selected2, onX);
            updateColumns();
            moves++;
            squares[selected.x][selected.y].setSelect(false);
            selected = null;
            selected2 = null;
        }
    }
    void update() {
        if (animating) {
            animating = isAnimating();
            //finished animating
            if (!animating) {
                if (checkMatches()) {
                    updateColumns();
                }
            }
        }

    }

}
