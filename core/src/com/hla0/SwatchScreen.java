package com.hla0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.util.ArrayList;

/**
 * Created by aft99 on 8/25/2016.
 */
public class SwatchScreen extends InputAdapter implements Screen{
    public static final String TAG = SwatchScreen.class.getName();
    ShapeRenderer renderer;
    Grid grid;
    OrthographicCamera camera;
    Viewport viewport;
    BitmapFont font;
    SpriteBatch spriteBatch;
    int curScreen;

    SwatchScreen() {
        camera = new OrthographicCamera();
        int level = 1;
        viewport = new FitViewport(Constants.gridSize * (Constants.boxSize + Constants.margin) + Constants.margin,
                Constants.gridSize * (Constants.boxSize + Constants.margin) + Constants.topPadding + Constants.bottomPadding + Constants.margin,
                camera);
        grid = new Grid(viewport, level);
        renderer = new ShapeRenderer();
        font = new BitmapFont();
        spriteBatch = new SpriteBatch();
        curScreen = 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        renderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        spriteBatch.begin();
        switch (curScreen) {
            case 0:
                font.setColor(Color.WHITE);
                font.getData().setScale(3,3);
                font.draw(spriteBatch,"Swatch",200,500);
                break;
            case 2:
                renderGame();
                break;
        }
        spriteBatch.end();
        renderer.end();
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
        switch (curScreen) {
            case 0:
                curScreen = 2;
                break;
            case 2:
                return gameTouch(screenX, screenY);
        }
        return false;
    }

    public void renderGame() {
        renderGrid();
        //TODO find better solution to hide new squares at top
        //temporary solution
        renderer.setColor(0, 0, 0, 1);
        renderer.rect(0, Constants.bottomPadding + Constants.gridSize * (Constants.boxSize + Constants.margin) + Constants.margin, 660, Constants.topPadding);
        renderGridUI();
    }

    public boolean gameTouch(int screenX, int screenY) {
        //do not process touches when grid is animating
        if (grid.animating) {
            grid.animating = grid.isAnimating();
        }
        if (!grid.animating) {
            Vector2 v = grid.transformToGrid(viewport.unproject(new Vector2(screenX, screenY)));
            boolean withinGrid = false;
            //removes margins from grid with some flexibility for slightly off touches
            if (v.x % 1 >= .07 && v.x % 1 <= .93 && v.y % 1 >= .07 && v.y % 1 <= .93) {
                if (v.x >= Constants.gridSize || v.x < 0 || v.y >= Constants.gridSize || v.y < 0) {
                    System.out.println("Outside grid");
                    withinGrid = false;
                } else {
                    System.out.println("Obtained grid coordinates: (" + (int) v.x + ", " + (int) v.y + ")");
                    grid.processTouch((int) v.x, (int) v.y);
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


    public void renderGrid() {
        Square[][] squares = grid.getSquares();
        ArrayList<Square> deleted = grid.getDeleted();
        ArrayList<Square> swapped = grid.getSwapped();
        for (int i = 0; i < Constants.gridSize; i++) {
            for (int j = 0; j < Constants.gridSize; j++) {
                if (squares[i][j] != null) {
                    if (deleted.size() == 0 && swapped.size() == 0) {
                        squares[i][j].update();
                    }
                    squares[i][j].render(renderer);
                }
            }
        }
        for (int i = deleted.size() - 1; i >= 0; i--) {
            deleted.get(i).renderDeleted(renderer);
            if (deleted.get(i).width < 0) {
                deleted.remove(i);
            }
        }

        //render the old color on top of the swapped square for transition
        for (int i = swapped.size() - 1; i >= 0; i--) {
            swapped.get(i).renderSwapped(renderer,grid.getDirection(),i);
            if (swapped.get(0).width < 0 || swapped.get(0).height < 0) {
                swapped.remove(0);
            }
        }
        grid.update();
    }

    public void renderGridUI() {
        //TODO add leading zeros
        int s = grid.getScore();
        CharSequence score = "" + s;
        if (s < 100000) {
            score = "0" + score;
        }
        if (s < 10000) {
            score = "0" + score;
        }
        if (s < 1000) {
            score = "0" + score;
        }
        if (s < 100) {
            score = "0" + score;
        }
        if (s < 10) {
            score = "0" + score;
        }
        if (s < 1) {
            score = "0" + score;
        }
        if (s == 0) {
            score = "000000";
        }
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,score,200,200);
    }
}
