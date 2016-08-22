package com.hla0;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

/**
 * Created by aft99 on 8/22/2016.
 */
public class Input extends InputAdapter {
    Viewport viewport;
    public Input(Viewport viewport) {
        this.viewport = viewport;
    }
    @Override
    public boolean keyDown(int keycode) {
        System.out.println("Down: " + keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println(screenX + ", " + screenY);
        Vector2 v = transformToGrid(viewport.unproject(new Vector2(screenX,screenY)));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public Vector2 transformToGrid(Vector2 v1) {

        Vector2 v = new Vector2((v1.x - (Constants.margin / 2))/(Constants.boxSize + Constants.margin),
                (v1.y - Constants.bottomPadding - (Constants.margin / 2)) / (Constants.boxSize + Constants.margin));
        //removes margins from grid with some flexibility for slightly off touches
        if (v.x % 1 >= .07 && v.x % 1 <= .93 && v.y % 1 >= .07 && v.y % 1 <= .93) {
            System.out.println("Obtained grid coordinates: (" + (int)v.x + ", " + (int)v.y + ")");
        }
        else {
            System.out.println("Outside grid");
        }
        return v;
    }


}
