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
    Grid grid;
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    int curScreen;
    int levelsComplete;
    int parentScreen;
    FileHandle stars;
    FileHandle complete;
    boolean enter;
    boolean exit;
    //game 0, settings 1, win 2, lose 3
    int gameState;
    Swatch game;

    public SwatchScreen(Swatch g) {
        game = g;
        parentScreen = -1;
        camera = new OrthographicCamera();
        int level = 1;
        viewport = new FitViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        grid = new Grid(viewport, level);
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        curScreen = 0;
        gameState = 0;
        enter = true;
        exit = false;
    }

    @Override
    public void show() {
        enter = true; exit = false;
        gameState = 0;
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        render();
    }
    public void render() {
        game.renderer.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        //when entering game
        if (!enter && !exit || gameState != 0) {
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            grid.render(game.renderer);
            game.renderer.end();
            renderGridUI();
        }
        switch(gameState) {
            case 0:
                //transition from other screens
                if (enter) {
                    System.out.println("entered game");
                    gameState = 0;
                    //renderEnter();
                    enter = false;
                }
                else if (exit) {
                    System.out.println("exited game");
                    exit = false;
                }
                break;
            //all three render over grid
            case 1:
                //renderSettings();
                if (enter) {
                    System.out.println("entered Settings");
                    enter = false;
                }
                else if (exit) {
                    System.out.println("exited Settings");
                    gameState = 0;
                    exit = false;
                }
                else {

                }
                break;
            case 2:
                //renderWin();
                if (enter) {
                    System.out.println("entered win");
                    enter = false;
                }
                else if (exit) {
                    System.out.println("exited win");
                    //might need to switch to level select or splash or replay
                    grid.loadLevel(grid.getLevel() + 1);
                    gameState = 0;
                    exit = false;
                }
                else {

                }
                break;
            case 3:
                //renderLose();
                if (enter) {
                    System.out.println("entered lose");
                    enter = false;
                }
                else if (exit) {
                    System.out.println("exited lose");
                    exit = false;
                    //temporary
                    //might need to switch to level select or splash
                    grid.loadLevel(grid.getLevel());
                    gameState = 0;
                }
                else {

                }
                break;

        }
        if (gameState == 0) {
            if (grid.checkObjectives()) {
                if (grid.getLevel() > levelsComplete) {
                    levelsComplete = grid.getLevel();
                    complete = Gdx.files.local("levelsComplete.txt");
                    complete.writeString("" + levelsComplete, false);
                }
                processStars();
                System.out.println("Completed Level");
                //prompt for next level or level select
                if (!grid.isAnimating()) {
                    gameState = 2;
                    enter = true;
                }
            } else if (grid.checkFail()) {
                System.out.println("Lost");
                gameState = 3;
                enter = true;
            }
        }
    }


    public void processStars() {
        //check stars
        //update stars
        stars = Gdx.files.local("levelStars.txt");
        if (Gdx.files.local("levelStars.txt").exists()) {
            String s = "";
            String sLine = stars.readString();
            stars.writeString(sLine.substring(0,grid.getLevel()) + "1" + sLine.substring(grid.getLevel() + 1,Constants.MAX_LEVEL),false);
        }
        else {
            String s = "";
            for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                if (grid.getLevel() == i) {
                    s += "1";
                    //add star amount
                }
                s += "0";
            }
            stars.writeString(s,false);
        }
    }


    public void renderGridUI() {
        font.setColor(Color.WHITE);
        //objectives
        //TODO add moves left and menu button
        int numberObj = 0;
        int[] colorsObj = new int[Constants.NUMBER_COLORS];
        for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
            if (grid.getColorObjectives(i) > 0) {
                colorsObj[numberObj] = i;
                numberObj++;
            }
        }
        for (int i = 0; i < numberObj; i++) {
            int index = colorsObj[i];
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            game.renderer.setColor(Square.getColor(index));
            int xPos = i * Swatch.worldWidth/numberObj + Constants.MARGIN + Swatch.worldWidth/numberObj/numberObj;
            int yPos = Swatch.worldHeight - Constants.BOX_SIZE * 4;
            game.renderer.rect(xPos, yPos, Constants.MARGIN, Constants.MARGIN);
            font.getData().setScale(2,2);
            int destroyed = grid.getColorDestroyed(index);
            if (destroyed > grid.getColorObjectives(index)) {
                destroyed = grid.getColorObjectives(index);
            }
            game.renderer.end();
            game.batch.begin();
            font.draw(game.batch,destroyed + "/" + grid.getColorObjectives(index),xPos - Constants.MARGIN/2,yPos - Constants.BOX_SIZE);
            game.batch.end();
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
            switch (gameState) {
                case 0:
                    gameTouch(screenX,screenY);
                    break;
                //temporary since might need to process nextScreen
                case 1:
                    //from settings
                    exit = true;
                    break;
                case 2:
                    //from win
                    exit = true;
                    break;
                case 3:
                    //from lose
                    exit = true;
                    break;
            }
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
    public void start() {enter = true;}
}
