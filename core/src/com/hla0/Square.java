package com.hla0;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.hla0.util.Constants;

public class Square {
    //position on screen
    Vector2 pos;

    //indices in the grid
    int x;
    int y;
    int type;
    boolean haveRed;
    boolean haveBlue;
    boolean haveYellow;
    boolean selected;
    Color color;
    int colorNum;
    int width;
    int height;
    int horizontalMatch;
    int verticalMatch;
    boolean animating;

    Square(int x, int y, int c) {
        this.x = x;
        this.y = y;
        pos = new Vector2(screenConvertX(x),screenConvertY(y));
        this.type = type;
        selected = false;
        animating = false;
        colorNum = c;
        width = Constants.BOX_SIZE;
        height = Constants.BOX_SIZE;
        horizontalMatch = 0;
        verticalMatch = 0;
        selected = false;
        setColor(c);
    }



    public int screenConvertX(int x) {
        return x * (Constants.BOX_SIZE + Constants.MARGIN) + Constants.MARGIN + Constants.LEFT_PADDING;
    }

    public int screenConvertY(int y) {
        return y * (Constants.BOX_SIZE + Constants.MARGIN) + Constants.BOTTOM_PADDING + Constants.MARGIN;
    }

    public void setColor(int c) {
        //colors except black and white from row 700 of https://www.materialui.co/colors
        color = getColor(c);
        switch (c) {
            //black
            case 0:
                haveRed = true;
                haveBlue = true;
                haveYellow = true;
                break;
            //white
            case 1:
                haveRed = false;
                haveBlue = false;
                haveYellow = false;
                break;
            //red
            case 2:
                haveRed = true;
                haveBlue = false;
                haveYellow = false;
                break;
            //blue
            case 3:
                haveRed = false;
                haveBlue = true;
                haveYellow = false;
                break;
            //yellow
            case 4:
                haveRed = false;
                haveBlue = false;
                haveYellow = true;
                break;
            //green
            case 5:
                haveRed = false;
                haveBlue = true;
                haveYellow = true;
                break;
            //orange
            case 6:
                haveRed = true;
                haveBlue = false;
                haveYellow = true;
                break;
            //purple
            case 7:
                haveRed = true;
                haveBlue = true;
                haveYellow = false;
                break;
            default:
                haveRed = false;
                haveBlue = false;
                haveYellow = false;
                colorNum = -1;
                //background color
                color = getColor(-1);
                break;
        }
    }
    public Color getColor() {
        return color;
    }

    public static Color getColor(int i) {
        switch (i) {
            //black
            case 0:
                return new Color(66 / 255f, 66 / 255f, 66 / 255f, 1);
            //white
            case 1:
                return new Color(245 / 255f, 245 / 255f, 245 / 255f, 1);
            //red
            case 2:
                return new Color(211 / 255f, 47 / 255f, 47 / 255f, 1);
            //blue
            case 3:
                return new Color(25 / 255f, 118 / 255f, 210 / 255f, 1);
            //yellow
            case 4:
                return new Color(251 / 255f, 192 / 255f, 45 / 255f, 1);
            //green
            case 5:
                return new Color(56 / 255f, 142 / 255f, 60 / 255f, 1);
            //orange
            case 6:
                return new Color(245 / 255f, 124 / 255f, 0 / 255f, 1);
            //purple
            case 7:
                return new Color(81 / 255f, 45 / 255f, 168 / 255f, 1);
            default:
                return new Color(0, 0, 0, 1);
        }
    }
    public int getColorNum() { return colorNum; }
    public int getColorNum(boolean r, boolean b, boolean y) {
        if (r && b && y) {
            return 0;
        }
        else if (r && !b && !y) {
            return 2;
        }
        else if (!r && b && !y) {
            return 3;
        }
        else if (!r && !b && y) {
            return 4;
        }
        else if (!r && b && y) {
            return 5;
        }
        else if (r && !b && y) {
            return 6;
        }
        else if (r && b && !y) {
            return 7;
        }
        else {
            return 1;
        }

    };
    public void swapColor(boolean r, boolean b,boolean y) {
        if (r) {
            invertRed();
        }
        if (b) {
            invertBlue();
        }
        if (y) {
            invertYellow();
        }
        colorNum = getColorNum(haveRed,haveBlue,haveYellow);
        setColor(getColorNum(haveRed, haveBlue, haveYellow));
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public void moveTo(int x, int y) {this.x = x; this.y = y;}
    public void invertRed() {haveRed = !haveRed;}
    public void invertBlue() {haveBlue = !haveBlue;}
    public void invertYellow() {haveYellow = !haveYellow;}
    public void animate() {animating = true;}
    public boolean isAnimating() {return animating;}
    public void setSelect(boolean s) {selected = s;}

    //if pos does not match x and y on grid move down
    public void update() {
        if (pos.y > screenConvertY(y)) {
            pos.y -= Constants.FALL_VELOCITY;
        }
        else if (pos.y < screenConvertY(y)) {
            pos.y = screenConvertY(y) + Constants.FALL_VELOCITY;
        }
        else {
            animating = false;
        }
    }

    //draw based on position on screen
    public void render(ShapeRenderer r) {
        //render shadow
        if (getColorNum() >= 0 && pos.y < Constants.BOTTOM_PADDING + Constants.MARGIN + (Constants.BOX_SIZE + Constants.MARGIN) * Constants.GRID_SIZE) {
            r.setColor(new Color(Constants.SHADOW_COLOR, Constants.SHADOW_COLOR, Constants.SHADOW_COLOR, 0.5f));
            r.rect(pos.x + 2, pos.y - 2, Constants.BOX_SIZE, Constants.BOX_SIZE);
            r.setColor(getColor());
            r.rect(pos.x, pos.y, Constants.BOX_SIZE, Constants.BOX_SIZE);
        }

        //TODO properly animate selected
        if (selected) {
            r.setColor(new Color(1,1,1,0.5f));
            r.rect(pos.x + Constants.MARGIN/2,pos.y + Constants.MARGIN/2,Constants.BOX_SIZE - Constants.MARGIN,Constants.BOX_SIZE - Constants.MARGIN);
        }
    }

    public void renderDeleted(ShapeRenderer r) {
        width -= Constants.SHRINK_VELOCITY;
        height -= Constants.SHRINK_VELOCITY;
        pos.x += Constants.SHRINK_VELOCITY / 2;
        pos.y += Constants.SHRINK_VELOCITY / 2;

        //render shadow
        if (getColorNum() >= 0) {
            r.setColor(new Color(Constants.SHADOW_COLOR, Constants.SHADOW_COLOR, Constants.SHADOW_COLOR, 0.5f));
            r.rect(pos.x + 2, pos.y - 2, width, height);
            r.setColor(getColor());
            r.rect(pos.x,pos.y,width,height);
        }
    }

    public void renderSwapped(ShapeRenderer r, int dir, int index) {
        r.setColor(getColor());
        if (index == 0)
            switch (dir) {
                case 0:
                    //animate up
                    height -= Constants.SWAP_VELOCITY;
                    pos.y += Constants.SWAP_VELOCITY;
                    break;
                case 1:
                    //animate down
                    height -= Constants.SWAP_VELOCITY;
                    break;
                case 2:
                    //animate left
                    width -= Constants.SWAP_VELOCITY;
                    break;
                case 3:
                    //animate right
                    width -= Constants.SWAP_VELOCITY;
                    pos.x += Constants.SWAP_VELOCITY;
                    break;
            }
        r.rect(pos.x,pos.y,width,height);
    }

    public void setHorizontalMatch(int m) {horizontalMatch = m;}
    public void setVerticalMatch(int m) {verticalMatch = m;}
    public int getHorizontalMatch() {return horizontalMatch;}
    public int getVerticalMatch() {return verticalMatch;}

}
