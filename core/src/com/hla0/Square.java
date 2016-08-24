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
    int size;
    boolean animating;

    Square(int x, int y, int c) {
        this.x = x;
        this.y = y;
        pos = new Vector2(screenConvertX(x),screenConvertY(y));
        this.type = type;
        selected = false;
        animating = false;
        colorNum = c;
        size = Constants.boxSize;
        selected = false;
        setColor(c);
    }



    public int screenConvertX(int x) {
        return x * (Constants.boxSize + Constants.margin) + Constants.margin;
    }

    public int screenConvertY(int y) {
        return y * (Constants.boxSize + Constants.margin) + Constants.bottomPadding + Constants.margin;
    }

    public void setColor(int c) {
        //colors except black and white from row 700 of https://www.materialui.co/colors
        switch (c) {
            //black
            case 0:
                haveRed = true;
                haveBlue = true;
                haveYellow = true;
                color = new Color(66/255f, 66/255f, 66/255f, 1);
                break;
            //white
            case 1:
                haveRed = false;
                haveBlue = false;
                haveYellow = false;
                color = new Color(245/255f, 245/255f, 245/255f, 1);
                break;
            //red
            case 2:
                haveRed = true;
                haveBlue = false;
                haveYellow = false;
                color = new Color(211/255f, 47/255f, 47/255f, 1);
                break;
            //blue
            case 3:
                haveRed = false;
                haveBlue = true;
                haveYellow = false;
                color = new Color(25/255f, 118/255f, 210/255f, 1);
                break;
            //yellow
            case 4:
                haveRed = false;
                haveBlue = false;
                haveYellow = true;
                color = new Color(251/255f, 192/255f, 45/255f, 1);
                break;
            //green
            case 5:
                haveRed = false;
                haveBlue = true;
                haveYellow = true;
                color = new Color(56/255f, 142/255f, 60/255f, 1);
                break;
            //orange
            case 6:
                haveRed = true;
                haveBlue = false;
                haveYellow = true;
                color = new Color(245/255f, 124/255f, 0/255f, 1);
                break;
            //purple
            case 7:
                haveRed = true;
                haveBlue = true;
                haveYellow = false;
                color = new Color(81/255f, 45/255f, 168/255f, 1);
                break;
        }
    }
    public Color getColor() {
        return color;
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
    public boolean isSelected() {return selected;}


    //if pos does not match x and y on grid move down
    public void update() {
        if (pos.y > screenConvertY(y)) {
            pos.y -= Constants.velocity;
        }
        else if (pos.y < screenConvertY(y)) {
            pos.y = screenConvertY(y) + Constants.velocity;
        }
        else {
            animating = false;
        }
    }

    //draw based on position on screen
    public void render(ShapeRenderer r) {
        r.setColor(getColor());
        r.rect(pos.x,pos.y,Constants.boxSize,Constants.boxSize);

        //TODO properly animate selected
        if (selected) {
            r.setColor(new Color(1,1,1,0.5f));
            r.rect(pos.x + Constants.margin/2,pos.y + Constants.margin/2,Constants.boxSize - Constants.margin,Constants.boxSize - Constants.margin);
        }
    }

    public void renderDeleted(ShapeRenderer r) {
        r.setColor(getColor());
        size -= Constants.shrinkVelocity;
        pos.x += Constants.shrinkVelocity / 2;
        pos.y += Constants.shrinkVelocity / 2;
        r.rect(pos.x,pos.y,size,size);
    }

    //TODO right render of swapped
    //temporary version of render swapped
    public void renderSwapped(ShapeRenderer r) {
        r.setColor(getColor());
        size -= Constants.shrinkVelocity;
        //pos.x += Constants.shrinkVelocity / 2;
        //pos.y += Constants.shrinkVelocity / 2;
        r.rect(pos.x,pos.y,size + 10,size + 10);
    }
}
