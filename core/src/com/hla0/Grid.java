package com.hla0;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.util.ArrayList;

public class Grid {
    private Square[][] squares;
    Square selected;
    Square selected2;
    Viewport viewport;
    boolean[] columnChanged;
    //check the state of all squares
    boolean animating;
    int[] colorDestroyed;
    int[] colorObjectives;
    int totalDestroyed;
    int moves;
    int direction;
    int animatedScore;
    int score;
    //y screen index where new squares would be added
    int top;
    ArrayList<Square> toDelete;
    ArrayList<Square> toSwap;
    //was animating but finished
    int level;
    int[][] levelMap;

    public Grid(Viewport viewport, int level) {
        squares = new Square[Constants.GRID_SIZE][Constants.GRID_SIZE];
        levelMap = new int[Constants.GRID_SIZE][Constants.GRID_SIZE];
        columnChanged = new boolean[Constants.GRID_SIZE];
        colorDestroyed = new int[Constants.NUMBER_COLORS];
        this.viewport = viewport;
        top = Constants.BOTTOM_PADDING + (Constants.GRID_SIZE + 1) * (Constants.BOX_SIZE + Constants.MARGIN);
        animating = false;
        moves = 0;
        totalDestroyed = 0;
        toDelete = new ArrayList<Square>();
        toSwap = new ArrayList<Square>();
        loadLevel(level);
        score = 0;
        animatedScore = 0;
        direction = -1;
    }

    public void loadLevel(int level) {
        reset();
        this.level = level;
        //different amount of moves per level
        moves = Levels.getNumberMoves(level);
        colorObjectives = Levels.getColorObjectives(level);
        for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
            colorDestroyed[i] = 0;
        }
        levelMap = Levels.getLevelMap(level);
        //currently set to random
        generateWithoutChains();
    }

    void reset() {
        toDelete = new ArrayList<Square>();
        toSwap = new ArrayList<Square>();
        score = 0;
        selected = null;
        selected2 = null;
    }

    //create a grid without match3
    void generateWithoutChains() {
        int colorLeft = -1;
        int colorUp = -1;
        int randomColor;
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                if (levelMap[i][j] == 0) {
                    randomColor = Levels.availableColor(level);
                    if (i > 1) {
                        if (squares[i - 1][j].getColorNum() == squares[i - 2][j].getColorNum()) {
                            colorLeft = squares[i - 1][j].getColorNum();
                        }
                    }
                    if (j > 1) {
                        if (squares[i][j - 1].getColorNum() == squares[i][j - 2].getColorNum()) {
                            colorUp = squares[i][j - 1].getColorNum();
                        }
                    }
                    while (randomColor == colorLeft || randomColor == colorUp) {
                        randomColor = (int) (Math.random() * Constants.NUMBER_COLORS);
                    }
                    generateSquare(i,j,top + j * (Constants.BOX_SIZE + Constants.MARGIN),randomColor);
                } else {
                    //TODO check level map for other types besides blank
                    //create specific square
                    generateSquare(i,j,top + j * (Constants.BOX_SIZE + Constants.MARGIN),-1);
                }
            }
        }
    }

    public void removeSquares() {
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                if (squares[i][j] != null && squares[i][j].getColorNum() >= 0) {
                    removeSquare(squares[i][j]);
                }
            }
        }
    }


    //deletions (depends on mode) match 3 and explode with white
    //remove the Square from the grid
    public void removeSquare(Square s) {

        if (s != null) {
            animating = true;
            if (!checkFail()) {
                s.addScore(100);
                score += s.getScore();
            }
            if (s.getColorNum() >= 0) {
                columnChanged[s.x] = true;
                colorDestroyed[s.getColorNum()]++;
                totalDestroyed++;
                if (score > Constants.MAX_SCORE) {
                    score = Constants.MAX_SCORE;
                }
                //check white
                if (s.getColorNum() == 1) {
                    if (squares[s.x][s.y] != null) {
                        toDelete.add(squares[s.x][s.y]);
                    }
                    squares[s.x][s.y] = null;
                    if (s.x + 1 < Constants.GRID_SIZE) {
                        removeSquare(squares[s.x + 1][s.y]);
                    }
                    if (s.x > 0) {
                        removeSquare(squares[s.x - 1][s.y]);
                    }
                    if (s.y + 1 < Constants.GRID_SIZE) {
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
    }

    //rearrange column so that blocks have fallen
    public void updateColumns() {
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            if (columnChanged[i]) {
                updateColumn(i);
                columnChanged[i] = false;
            }
        }
    }

    public void generateSquare(int x, int y, float yPos) {
        squares[x][y] = new Square(x, y, Levels.availableColor(level));
        squares[x][y].pos.y = yPos;
    }

    public void generateSquare(int x, int y, float yPos, int color) {
        squares[x][y] = new Square(x, y, color);
        squares[x][y].pos.y = yPos;
    }


    //fix for removed squares in column
    public void updateColumn(int col) {
        animating = true;
        boolean above = true;
        int squareCount = 0;
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            if (squares[col][i] == null) {
                //try finding squares above
                if (above) {
                    Square s = findSquareAbove(col, i);
                    //did not find square above
                    if (s == null) {
                        generateSquare(col, i, top + squareCount * (Constants.BOX_SIZE + Constants.MARGIN));
                        squareCount++;
                        above = false;
                    }
                    //found next square above
                    //moving square down and removing old copy
                    else {
                        squares[s.x][s.y] = null;
                        s.moveTo(col, i);
                        squares[col][i] = s;
                    }
                }
                //no squares above
                else {
                    generateSquare(col, i, top + squareCount * (Constants.BOX_SIZE + Constants.MARGIN));
                    squareCount++;
                }
                //can add parameters to animate fall or swap
                squares[col][i].animate();
            }
        }
    }

    public Square findSquareAbove(int col, int index) {
        for (int i = index + 1; i < Constants.GRID_SIZE; i++) {
            if (squares[col][i] != null && squares[col][i].getColorNum() >= 0) {
                return squares[col][i];
            }
        }
        return null;
    }

    //modifying colors
    public void swapLineColors(Square s1, Square s2, boolean onX) {
        animating = true;
        boolean haveRed = s1.haveRed;
        boolean haveBlue = s1.haveBlue;
        boolean haveYellow = s1.haveYellow;
        System.out.println("swapping lines");

        if (onX) {
            System.out.println("x are equal" + s1.x + " " + s2.x);
            int x = s1.x;
            //swap up
            if (s1.y < s2.y) {
                for (int i = s1.y + 1; i <= s2.y; i++) {
                    System.out.println("swapping " + x + " " + i);
                    if (squares[x][i].getColorNum() < 0) {
                        System.out.println("swapping empty square");
                    } else {
                        toSwap.add(new Square(x, i, squares[x][i].getColorNum()));
                        squares[x][i].swapColor(haveRed, haveBlue, haveYellow);
                    }
                }
                direction = 0;
            }
            //swap down
            else {
                for (int i = s1.y - 1; i >= s2.y; i--) {
                    System.out.println("swapping " + x + " " + i);
                    if (squares[x][i].getColorNum() < 0) {
                        System.out.println("swapping empty square");
                    } else {
                        toSwap.add(new Square(x, i, squares[x][i].getColorNum()));
                        squares[x][i].swapColor(haveRed, haveBlue, haveYellow);
                    }
                }
                direction = 1;
            }
        } else {
            int y = s1.y;
            System.out.println("y are equal" + s1.y + " " + s2.y);
            //swap left
            if (s1.x > s2.x) {
                System.out.println("x left: " + s1.x + " " + s2.x);
                for (int i = s1.x - 1; i >= s2.x; i--) {
                    System.out.println("swapping " + i + " " + y);
                    if (squares[i][y].getColorNum() < 0) {
                        System.out.println("swapping empty square");
                    } else {
                        toSwap.add(new Square(i, y, squares[i][y].getColorNum()));
                        squares[i][y].swapColor(haveRed, haveBlue, haveYellow);
                    }
                }
                direction = 2;
            }
            //swap right
            else {
                System.out.println("x right: " + s1.x + " " + s2.x);
                for (int i = s1.x + 1; i <= s2.x; i++) {
                    System.out.println("swapping " + i + " " + y);
                    if (squares[i][y].getColorNum() < 0) {
                        System.out.println("swapping empty square");
                    } else {
                        toSwap.add(new Square(i, y, squares[i][y].getColorNum()));
                        squares[i][y].swapColor(haveRed, haveBlue, haveYellow);
                    }
                }
                direction = 3;
            }
        }
        //start checking for matches and removing
        System.out.println("finished swapping");
    }

    public int getDirection() {return direction;}

    public int getLevel() {return level;}

    //should be called after swapLines and updateColumns
    public boolean checkMatches() {
        boolean match = false;
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            if (scanHorizontal(i)) {
                match = true;
                System.out.println("Found horizontal match on row " + i);
            }
        }
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            if (scanVertical(i)) {
                match = true;
                System.out.println("Found vertical match on column " + i);
            }
        }
        removeMatches();
        return match;
    }

    public boolean scanHorizontal(int row) {
        boolean match = false;
        Square left1 = null;
        Square left2 = null;
        Square right1 = null;
        Square right2 = null;

        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            Square curSquare = squares[i][row];
            if (curSquare != null) {
                int count = 1;
                int curColor = curSquare.getColorNum();
                if (curColor >= 0) {
                    if (i - 2 >= 0) {
                        left2 = squares[i - 2][row];
                    }
                    if (i - 1 >= 0) {
                        left1 = squares[i - 1][row];
                    }
                    if (i + 2 < Constants.GRID_SIZE) {
                        right2 = squares[i + 2][row];
                    }
                    if (i + 1 < Constants.GRID_SIZE) {
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
                }
                left1 = null;
                left2 = null;
                right1 = null;
                right2 = null;
            }
        }
        return match;
    }


    public boolean scanVertical(int col) {
        boolean match = false;
        Square up1 = null;
        Square up2 = null;
        Square down1 = null;
        Square down2 = null;

        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            Square curSquare = squares[col][i];
            if (curSquare != null) {
                int count = 1;
                int curColor = curSquare.getColorNum();
                if (curColor >= 0) {
                    if (i + 2 < Constants.GRID_SIZE) {
                        up2 = squares[col][i + 2];
                    }
                    if (i + 1 < Constants.GRID_SIZE) {
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
                }
                up1 = null;
                up2 = null;
                down1 = null;
                down2 = null;
            }
        }
        return match;
    }

    //TODO if squares have a certain criteria (match horizontal and vertical) match 4 or 5
    public void removeMatches() {
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {

                Square s = squares[i][j];
                if (s != null) {
                    if (!checkFail()) {
                        //add the current score to the new square when creating new squares
                        if (s.getHorizontalMatch() >= 3) {
                            squares[i][j].addScore(-(s.getHorizontalMatch() - 6) * 100);
                        }
                        else if (s.getVerticalMatch() >= 3) {
                            squares[i][j].addScore(-(s.getVerticalMatch() - 6) * 100);
                        }
                        else if (s.getHorizontalMatch() >= 3 && s.getVerticalMatch() >= 3) {
                            squares[i][j].addScore(300);
                        }
                    }

                    if ((s.getHorizontalMatch() >= 3 || s.getVerticalMatch() >= 3)) {
                        removeSquare(squares[i][j]);
                    }
                }
            }
        }
    }

    public int getScore() {
        return animatedScore;
    }
    public int getColorDestroyed(int i) {return colorDestroyed[i];}
    public int getColorObjectives(int i) {return colorObjectives[i];}

    public boolean isAnimating() {
        if (toDelete.size() > 0 || toSwap.size() > 0 || score > animatedScore) {
            return true;
        }
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                if (squares[i][j] != null && squares[i][j].isAnimating()) {
                    return true;
                }
            }
        }
        return false;
    }

    //where there is selected = *; change status of the variable within square
    public void processTouch(int x, int y) {
        boolean onX = false;
        if (selected == null) {
            //square is empty
            if (squares[x][y].getColorNum() < 0) {
                System.out.println("touched empty");
            }
            //square is white
            else if (squares[x][y].getColorNum() == 1) {
                removeSquare(squares[x][y]);
                updateColumns();
                moves--;
            } else {
                selected = squares[x][y];
                squares[x][y].setSelect(true);
            }
        } else {
            if (squares[x][y].getColorNum() < 0) {
                System.out.println("touched empty");
            } else {
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
                        moves--;
                    } else {
                        selected = squares[x][y];
                        squares[x][y].setSelect(true);
                    }
                }
            }
        }
        //second square on same row or column is clicked
        if (selected2 != null) {
            //TODO maybe check if ones in between are empty
            swapLineColors(selected, selected2, onX);
            updateColumns();
            moves--;
            squares[selected.x][selected.y].setSelect(false);
            selected = null;
            selected2 = null;
        }
    }

    public boolean checkObjectives() {
        for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
            if (colorObjectives[i] > colorDestroyed[i]) {
                return false;
            }
        }
        score += moves * 1500;
        moves = 0;
        return true;
    }

    public boolean checkFail() {
        return moves == 0;
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
        if (animatedScore < score) {
            if (moves > 0) {
                animatedScore += 51;
            } else {
                animatedScore += 151;
            }
        } else {
            animatedScore = score;
        }
    }

    public void render(ShapeRenderer renderer) {
        for(int i = 0; i < Constants.GRID_SIZE; i++) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                if (squares[i][j] != null) {
                    if (toDelete.size() == 0 && toSwap.size() == 0) {
                        squares[i][j].update();
                    }
                    squares[i][j].render(renderer);
                }
            }
        }
        for(int i = toDelete.size() - 1; i>=0; i--) {
            toDelete.get(i).renderDeleted(renderer,checkFail());
            if (toDelete.get(i).width < 0) {
                toDelete.remove(i);
            }
        }
        //render the old color on top of the swapped square for transition
        for(int i = toSwap.size() - 1; i>=0; i--) {
            toSwap.get(i).renderSwapped(renderer,getDirection(), i);
            if (toSwap.get(0).width < 0 || toSwap.get(0).height < 0) {
                toSwap.remove(0);
            }
        }
        update();
    }

}
