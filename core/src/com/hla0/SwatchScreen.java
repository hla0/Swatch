package com.hla0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hla0.util.Constants;

public class SwatchScreen extends InputAdapter implements Screen{
    public static final String TAG = SwatchScreen.class.getName();
    ShapeRenderer renderer;
    Grid grid;
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    SpriteBatch spriteBatch;
    int curScreen;
    int levelsComplete;
    int parentScreen;
    int worldWidth;
    int worldHeight;
    boolean animating;
    int yPos;
    int yVelocity;
    Renderer r;
    SwatchScreen() {
        parentScreen = -1;
        camera = new OrthographicCamera();
        int level = 1;
        //490,790
        worldWidth = Constants.leftPadding + Constants.rightPadding + Constants.gridSize * (Constants.boxSize + Constants.margin) + Constants.margin;
        worldHeight = Constants.gridSize * (Constants.boxSize + Constants.margin) + Constants.topPadding + Constants.bottomPadding + Constants.margin;
        viewport = new FitViewport(worldWidth,worldHeight,camera);
        grid = new Grid(viewport, level);
        r = new Renderer(this,worldWidth,worldHeight,grid);
        animating = false;
        curScreen = 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .5f, .5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        viewport.apply();
        animating = r.render(curScreen,parentScreen, animating,camera);
    }

    /**
     * When the screen is resized, we need to inform the viewport. Note that when using an
     * ExtendViewport, the world size might change as well.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        //Gdx.app.log(TAG, "Viewport world dimensions: (" + viewport.getWorldHeight() + ", " + viewport.getWorldWidth() + ")");
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        renderer.dispose();
        spriteBatch.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!animating) {
            switch (curScreen) {
                case 0:
                    if (splashTouch(screenX,screenY)) {
                        return true;
                    }

                    break;
                case 1:
                    grid.loadLevel(1);
                    switchScreen(2);
                    break;
                case 2:
                    if (gameTouch(screenX, screenY)) {
                        return true;
                    }
                    else {
                        //for testing
                        //switchScreen(3);
                    }
                    break;
                case 3:
                    //settings
                    System.out.println("out of settings");
                    switchScreen(parentScreen);
                    break;
                case 4:
                    //winScreen
                    //decide to go to levelSelect
                    //curScreen = 1
                    //or next level
                    grid.level++;
                    grid.loadLevel(grid.level);
                    curScreen = 2;
                    break;
                case 5:
                    //levelSelect
                    //curScreen = 1
                    //retry
                    grid.loadLevel(grid.level);
                    curScreen = 2;
                    //loseScreen
                    break;
            }
        }
        return false;
    }
    public boolean splashTouch(int screenX, int screenY) {
        //choose levelSelect
        //switchScreen(1);
        //TODO choose mode
        //switchScreen(2);
        //choose settings
        //temporary
        if (viewport.unproject(new Vector2(screenX,screenY)).x > worldWidth/2) {
            System.out.println("settings");
            switchScreen(3);
        }
        else {
            switchScreen(1);
        }
        return true;
    }

    public boolean gameTouch(int screenX, int screenY) {
        //do not process touches when grid is animating
        if (animating) {
            animating = grid.isAnimating();
        }
        if (!animating) {
            Vector2 v = transformToGrid(viewport.unproject(new Vector2(screenX, screenY)));
            boolean withinGrid = false;
            //removes margins from grid with some flexibility for slightly off touches
            if (v.x % 1 >= .07 && v.x % 1 <= .93 && v.y % 1 >= .07 && v.y % 1 <= .93) {
                if (v.x >= Constants.gridSize || v.x < 0 || v.y >= Constants.gridSize || v.y < 0) {
                    System.out.println("Outside grid");
                    withinGrid = false;
                } else {
                    System.out.println("Obtained grid coordinates: (" + (int) v.x + ", " + (int) v.y + ")");
                    grid.processTouch((int)v.x,(int) v.y);
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
        Vector2 v = new Vector2((v1.x - Constants.leftPadding - (Constants.margin / 2))/(Constants.boxSize + Constants.margin),
                (v1.y - Constants.bottomPadding - (Constants.margin / 2)) / (Constants.boxSize + Constants.margin));
        return v;
    }

    public void switchScreen(int screen) {
        parentScreen = curScreen;
        curScreen = screen;
        animating = true;
        //animate screen going up
        //except splash and main
    }
}
