package com.hla0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.util.ArrayList;

public class Grid {
    Swatch game;
    private Square[][] squares;
    Square selected;
    Square selected2;
    Viewport viewport;
    boolean[] columnChanged;
    //check the state of all squares
    boolean animating;
    int[] colorDestroyed;
    int[] anchorDestroyed;
    int[] colorObjectives;
    int[] anchorObjectives;
    int totalDestroyed;
    int totalAnchorDestroyed;
    int moves;
    int direction;
    int animatedScore;
    int score;
    //y screen index where new squares would be added
    int top;
    ArrayList<Square> toDelete;
    ArrayList<Square> toSwap;
    public boolean soundRemove;
    //was animating but finished
    int level;
    int[][] levelMap;
    Sound remove;
    Sound select;
    int combo;

    public Grid(Viewport viewport, int level, Swatch g) {
        combo = 0;
        game = g;
        soundRemove = false;
        squares = new Square[Constants.GRID_SIZE][Constants.GRID_SIZE];
        levelMap = new int[Constants.GRID_SIZE][Constants.GRID_SIZE];
        columnChanged = new boolean[Constants.GRID_SIZE];
        colorDestroyed = new int[Constants.NUMBER_COLORS];
        anchorDestroyed = new int[Constants.NUMBER_COLORS];
        totalAnchorDestroyed = 0;
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
        select = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
        remove = Gdx.audio.newSound(Gdx.files.internal("remove.mp3"));
    }

    public void loadLevel(int level) {
        score = 0;
        combo = 0;
        reset();
        this.level = level;
        //different amount of moves per level
        moves = Levels.getNumberMoves(level);
        colorObjectives = Levels.getColorObjectives(level);
        anchorObjectives = Levels.getAnchorObjectives(level);
        for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
            colorDestroyed[i] = 0;
            anchorDestroyed[i] = 0;
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
                    toDelete.add(squares[i][j]);
                    squares[i][j] = null;
                }
            }
        }
    }


    //deletions (depends on mode) match 3 and explode with white
    //remove the Square from the grid
    public void removeSquare(Square s) {
        if (s != null) {
            if (s.getType() != 1) {
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
        if (y < 4) {
            squares[x][y] = new Square(x, y, Levels.availableColor(level));
        }
        else {
            squares[x][y] = new Square(x, y, Levels.availableColor(level), Levels.availableType(level));
        }
        squares[x][y].pos.y = yPos;
    }

    public void generateSquare(int x, int y, float yPos, int color) {
        if (y < 4) {
            squares[x][y] = new Square(x, y, color);
        }
        else{
            squares[x][y] = new Square(x, y, color, Levels.availableType(level));
        }
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
                        toSwap.add(new Square(x, i, squares[x][i].getColorNum(),squares[x][i].getType()));
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
                        toSwap.add(new Square(x, i, squares[x][i].getColorNum(),squares[x][i].getType()));
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
                        toSwap.add(new Square(i, y, squares[i][y].getColorNum(),squares[i][y].getType()));
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
                        toSwap.add(new Square(i, y, squares[i][y].getColorNum(),squares[i][y].getType()));
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
            //TODO count number of matches in each method to calculate combo
            if (scanHorizontal(i)) {
                //temporary does not work in all cases
                combo++;
                match = true;
                System.out.println("Found horizontal match on row " + i);
            }
        }
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            if (scanVertical(i)) {
                //temporary
                combo++;
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
        Square left3 = null;
        Square right1 = null;
        Square right2 = null;
        Square right3 = null;

        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            Square curSquare = squares[i][row];
            if (curSquare != null) {
                int count = 1;
                int curColor = curSquare.getColorNum();
                if (curColor >= 0) {
                    if (i - 3 >= 0) {
                        left3 = squares[i - 3][row];
                    }
                    if (i - 2 >= 0) {
                        left2 = squares[i - 2][row];
                    }
                    if (i - 1 >= 0) {
                        left1 = squares[i - 1][row];
                    }
                    if (i + 3 < Constants.GRID_SIZE) {
                        right3 = squares[i + 3][row];
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
                            if (left3 != null && left3.getColorNum() == curColor) {
                                count++;
                            }
                        }
                    }
                    if (right1 != null && right1.getColorNum() == curColor) {
                        count++;
                        if (right2 != null && right2.getColorNum() == curColor) {
                            count++;
                            if (right3 != null && right3.getColorNum() == curColor) {
                                count++;
                            }
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
        Square up3 = null;
        Square down1 = null;
        Square down2 = null;
        Square down3 = null;

        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            Square curSquare = squares[col][i];
            if (curSquare != null) {
                int count = 1;
                int curColor = curSquare.getColorNum();
                if (curColor >= 0) {
                    if (i + 3 < Constants.GRID_SIZE) {
                        up3 = squares[col][i + 3];
                    }
                    if (i + 2 < Constants.GRID_SIZE) {
                        up2 = squares[col][i + 2];
                    }
                    if (i + 1 < Constants.GRID_SIZE) {
                        up1 = squares[col][i + 1];
                    }
                    if (i - 3 >= 0) {
                        down3 = squares[col][i - 3];
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
                            if (down3 != null && down3.getColorNum() == curColor) {
                                count++;
                            }
                        }
                    }
                    if (up1 != null && up1.getColorNum() == curColor) {
                        count++;
                        if (up2 != null && up2.getColorNum() == curColor) {
                            count++;
                            if (up3 != null && up3.getColorNum() == curColor) {
                                count++;
                            }
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

    //TODO remove anchors
    public boolean removeAnchors() {
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            int j = 0;
            if (squares[i][j] != null) {
                System.out.println(j);
                while (j < Constants.GRID_SIZE && squares[i][j] != null && squares[i][j].getColorNum() < 0) {
                    j++;
                }
                if (j < Constants.GRID_SIZE && squares[i][j] != null && squares[i][j].getType() == 1) {
                    colorDestroyed[squares[i][j].getColorNum()]++;
                    totalAnchorDestroyed++;
                    anchorDestroyed[squares[i][j].getColorNum()]++;
                    columnChanged[i] = true;
                    squares[i][j].addScore(1000);
                    score += squares[i][j].getScore();
                    toDelete.add(squares[i][j]);
                    squares[i][j] = null;
                    return true;
                }
            }
        }
        return false;
    }

    //TODO if squares have a certain criteria (match horizontal and vertical) match 4 or 5 or 6
    //4, horizontal or vertical remove
    //both horizontal and vertical// diagonal remove
    //5, remove all of same color
    //6 remove all squares
    public void removeMatches() {
        flattenMatches();
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                Square s = squares[i][j];
                if (s != null && s.getType() != 1) {
                    if (!checkFail()) {
                        //add the current score to the new square when creating new squares
                        if (s.getHorizontalMatch() >= 6 || s.getVerticalMatch() >= 6) {
                            squares[i][j].addScore(2500);
                        }
                        else if (s.getHorizontalMatch() >= 5 || s.getVerticalMatch() >= 5) {
                            squares[i][j].addScore(1500);
                        }
                        else if (s.getHorizontalMatch() >= 3 && s.getVerticalMatch() >= 3) {
                            squares[i][j].addScore(800);
                        }
                        else if (s.getHorizontalMatch() >= 4 || s.getVerticalMatch() >= 4) {
                            squares[i][j].addScore(500);
                        }
                        else if (s.getHorizontalMatch() >= 3) {
                            squares[i][j].addScore(300);
                        }
                        else if (s.getVerticalMatch() >= 3) {
                            squares[i][j].addScore(300);
                        }
                    }

                    if ((s.getHorizontalMatch() >= 3 || s.getVerticalMatch() >= 3)) {
                        if (squares[i][j].getType() == 0) {
                            removeSquare(squares[i][j]);
                        }
                    }
                }
            }
        }
    }
    //TODO use non-anchor max to fix 2 anchor highest match bug
    public void flattenMatches() {
        for (int j = 0; j < Constants.GRID_SIZE; j++) {
                int maxHorizontal = 0;
                int index = 0;
                for (int i = 0; i < Constants.GRID_SIZE; i++) {
                    Square s = squares[i][j];
                    if (s != null && s.getType() != 1) {
                        if (s.getHorizontalMatch() > maxHorizontal) {
                            maxHorizontal = squares[i][j].getHorizontalMatch();
                            index = i;
                        }
                    }
                }
                for (int i = 0; i < Constants.GRID_SIZE; i++) {
                    Square s = squares[i][j];
                    if (s != null && s.getType() != 1) {
                        int h = s.getHorizontalMatch();
                        if (h >= 3 && h < maxHorizontal && index != i) {
                            squares[i][j].setHorizontalMatch(3);
                        }
                    }
                }

        }
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            int maxVertical = 0;
            int index = 0;
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                Square s = squares[i][j];
                if (s != null && s.getType() != 1) {
                    if (s.getVerticalMatch() > maxVertical) {
                        maxVertical = s.getVerticalMatch();
                        index = i;
                    }
                }
            }
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                Square s = squares[i][j];
                if (s != null && s.getType() != 1) {
                    int v = s.getVerticalMatch();
                    if (v >= 3 &&  v < maxVertical && index != i) {
                        squares[i][j].setVerticalMatch(3);
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
    public int getAnchorDestroyed(int i) {return anchorDestroyed[i];}
    public int getAnchorObjectives(int i) {return anchorObjectives[i];}
    public int getMoves() {return moves;}

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
        combo = 0;
        boolean onX = false;
        if (selected == null) {
            //square is empty
            if (squares[x][y].getColorNum() < 0 || squares[x][y].getType() == 1) {
                System.out.println("touched empty");
            }
            //square is white
            else if (squares[x][y].getColorNum() == 1) {
                combo++;
                removeSquare(squares[x][y]);
                updateColumns();
                moves--;
            } else {
                if (game.isSound()) {
                    select.play();
                }
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
                    if (game.isSound()) {
                        select.play();
                    }
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
            swapLineColors(selected, selected2, onX);
            updateColumns();
            moves--;
            squares[selected.x][selected.y].setSelect(false);
            selected = null;
            selected2 = null;
        }
    }

    public int getTotalAnchorDestroyed() {return totalAnchorDestroyed;}


    public boolean checkObjectives() {
        if (getLevel() != 0) {
            for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
                if (colorObjectives[i] > colorDestroyed[i]) {
                    return false;
                }
            }
            score += moves * 1500;
            moves = 0;
            return true;
        }
        else {
            //in free play mode
            //to prevent overflow eventually if it gets that far
            if (moves < -1000) {
                moves = -1;
            }
        }
        return false;
    }

    public boolean checkFail() {
        return moves == 0;
    }

    void update() {
        if (animating) {
            animating = isAnimating();
            //finished animating
            if (!soundRemove && toDelete.size() > 0) {
                if (game.isSound()) {
                    remove.play();
                }
                soundRemove = true;
            }
            if (!animating && !checkFail()) {
                if (soundRemove) {
                    soundRemove = false;
                }
                if (checkMatches() || removeAnchors()) {
                    updateColumns();
                }
            }
        }
        if (animatedScore < score) {
            if (moves > 0) {
                animatedScore += 251;
            } else {
                animatedScore += 451;
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
                //TODO change sound to swap sound
                if (game.isSound()) {
                    select.play();
                }
            }
        }
        update();
    }

}
