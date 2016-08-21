package com.hla0;

import com.badlogic.gdx.graphics.Color;

public class Square {
    //indices in the grid
    int x;
    int y;
    int type;
    boolean haveRed;
    boolean haveBlue;
    boolean haveYellow;
    boolean selected;
    Color color;

    Square(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        selected = false;
        assignColor(type);
    }

    public void assignColor(int type) {
        //colors except black and white from row 700 of https://www.materialui.co/colors
        switch (type) {
            //red
            case 0:
                haveRed = true;
                haveBlue = false;
                haveYellow = false;
                color = new Color(211/255f, 47/255f, 47/255f, 1);
                break;
            //blue
            case 1:
                haveRed = false;
                haveBlue = true;
                haveYellow = false;
                color = new Color(25/255f, 118/255f, 210/255f, 1);
                break;
            //yellow
            case 2:
                haveRed = false;
                haveBlue = false;
                haveYellow = true;
                color = new Color(251/255f, 192/255f, 45/255f, 1);
                break;
            //green
            case 3:
                haveRed = false;
                haveBlue = true;
                haveYellow = true;
                color = new Color(56/255f, 142/255f, 60/255f, 1);
                break;
            //orange
            case 4:
                haveRed = true;
                haveBlue = false;
                haveYellow = true;
                color = new Color(245/255f, 124/255f, 0/255f, 1);
                break;
            //purple
            case 5:
                haveRed = true;
                haveBlue = true;
                haveYellow = false;
                color = new Color(81/255f, 45/255f, 168/255f, 1);
                break;
            //black
            case 6:
                haveRed = false;
                haveBlue = false;
                haveYellow = false;
                color = new Color(66/255f, 66/255f, 66/255f, 1);
                break;
            //white
            case 7:
                haveRed = true;
                haveBlue = true;
                haveYellow = true;
                color = new Color(245/255f, 245/255f, 245/255f, 1);
                break;
        }
    }
    public Color getColor() {
        return color;
    }
}
