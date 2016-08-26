package com.hla0.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hla0.Grid;
import com.hla0.Square;
import com.hla0.Swatch;
import com.hla0.util.Constants;

public class SwatchScreen extends InputAdapter implements Screen{
    public static final String TAG = SwatchScreen.class.getName();
    ShapeRenderer renderer;
    Grid grid;
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    int curScreen;
    int levelsComplete;
    int parentScreen;
    FileHandle complete;
    Swatch game;

    public SwatchScreen(Swatch g) {
        game = g;
        parentScreen = -1;
        camera = new OrthographicCamera();
        renderer = new ShapeRenderer();
        int level = 1;
        viewport = new FitViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        grid = new Grid(viewport, level);
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
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
        renderer.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        viewport.apply();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        grid.render(renderer);
        renderer.end();
        renderGridUI();

        /*
        if (grid.checkObjectives()) {
            if (grid.level > levelsComplete) {
                levelsComplete = grid.level;
                complete.writeString("" + levelsComplete,false);
            }
            processStars();
            System.out.println("Completed Level");
            //prompt for next level or level select
            if (!grid.isAnimating()) {
                swatchScreen.switchScreen(4);
            }
        }
        else if (grid.checkFail()) {
            System.out.println("Lost");
            swatchScreen.switchScreen(5);
        }
        */

    }

    /*
    public void processStars() {
        //check stars
        //update stars
        stars = Gdx.files.local("levelStars.txt");
        if (Gdx.files.local("levelStars.txt").exists()) {
            String s = "";
            String sLine = stars.readString();
            stars.writeString(sLine.substring(0,grid.level) + "1" + sLine.substring(grid.level+1,Constants.MAX_LEVEL),false);
        }
        else {
            String s = "";
            for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                if (grid.level == i) {
                    s += "1";
                    //add star amount
                }
                s += "0";
            }
            stars.writeString(s,false);
        }
    }
    */

    public void renderGridUI() {
        font.setColor(Color.WHITE);
        //objectives
        int numObjectives = 0;
        //TODO add moves left and menu button
        for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
            if (grid.getColorObjectives(i) > 0) {
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.setColor(Square.getColor(i));
                int xPos = (int)(numObjectives * Constants.BOX_SIZE * 1.5 + Constants.MARGIN * 1.5 + Constants.LEFT_PADDING);
                int yPos = Swatch.worldHeight - Constants.BOX_SIZE * 4;
                renderer.rect(xPos, yPos, Constants.MARGIN, Constants.MARGIN);
                font.getData().setScale(2,2);
                int destroyed = grid.getColorDestroyed(i);
                if (destroyed > grid.getColorObjectives(i)) {
                    destroyed = grid.getColorObjectives(i);
                }
                renderer.end();
                game.batch.begin();

                font.draw(game.batch,destroyed + "/" + grid.getColorObjectives(i),xPos - 5,yPos - Constants.BOX_SIZE);
                game.batch.end();
                numObjectives++;
            }
        }

        game.batch.begin();
        //score
        font.getData().setScale(3,3);
        font.draw(game.batch,leadingZeros(grid.getScore()),200,200);
        game.batch.end();
    }

    public CharSequence leadingZeros(int s) {
        CharSequence score = "" + s;
        if (s < 100000) {score = "0" + score;}
        if (s < 10000) {score = "0" + score;}
        if (s < 1000) {score = "0" + score;}
        if (s < 100) {score = "0" + score;}
        if (s < 10) {score = "0" + score;}
        if (s < 1) {score = "0" + score;}
        if (s == 0) {score = "000000";}
        return score;
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
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.getCurScreen() == 2) {
            gameTouch(screenX,screenY);
            return true;
        }
        else {
            return false;
        }
    }


    public boolean gameTouch(int screenX, int screenY) {
        //do not process touches when grid is animating
        if (!grid.isAnimating()) {
            Vector2 v = transformToGrid(viewport.unproject(new Vector2(screenX, screenY)));
            boolean withinGrid;
            //removes MARGINs from grid with some flexibility for slightly off touches
            if (v.x % 1 >= .07 && v.x % 1 <= .93 && v.y % 1 >= .07 && v.y % 1 <= .93) {
                if (v.x >= Constants.GRID_SIZE || v.x < 0 || v.y >= Constants.GRID_SIZE || v.y < 0) {
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
        return new Vector2((v1.x - Constants.LEFT_PADDING - (Constants.MARGIN / 2))/(Constants.BOX_SIZE + Constants.MARGIN),
                (v1.y - Constants.BOTTOM_PADDING - (Constants.MARGIN / 2)) / (Constants.BOX_SIZE + Constants.MARGIN));
    }
}
