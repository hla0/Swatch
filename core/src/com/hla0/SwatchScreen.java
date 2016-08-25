package com.hla0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.util.ArrayList;

public class SwatchScreen extends InputAdapter implements Screen{
    public static final String TAG = SwatchScreen.class.getName();
    ShapeRenderer renderer;
    Grid grid;
    OrthographicCamera camera;
    Viewport viewport;
    BitmapFont font;
    SpriteBatch spriteBatch;
    int curScreen;
    FileHandle complete;
    FileHandle stars;
    int levelsComplete;
    int parentScreen;
    int worldWidth;
    int worldHeight;

    SwatchScreen() {
        parentScreen = -1;
        camera = new OrthographicCamera();
        int level = 1;
        //490,790
        worldWidth = Constants.gridSize * (Constants.boxSize + Constants.margin) + Constants.margin;
        worldHeight = Constants.gridSize * (Constants.boxSize + Constants.margin) + Constants.topPadding + Constants.bottomPadding + Constants.margin;
        viewport = new FitViewport(worldWidth,worldHeight,camera);
        grid = new Grid(viewport, level);
        renderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        spriteBatch = new SpriteBatch();
        stars = Gdx.files.local("levelStars.txt");
        complete = Gdx.files.local("levelsComplete.txt");
        if (Gdx.files.local("levelsComplete.txt").exists()) {
            String data = complete.readString();
            System.out.println("File data: " + data);
            level = Integer.parseInt(complete.readString());
        }
        else {
            String s = "";
            for (int i = 0; i < Constants.maxLevels; i++) {
                s += "0";
            }
            stars.writeString(s,false);
            levelsComplete = 0;
        }
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
            case 1:
                renderLevelSelect();
                break;
            case 2:
                renderGame();
                break;
            case 3:
                //renderSettings();
                switchScreen(parentScreen);
                break;
            case 4:
                renderWin();
                break;
            case 5:
                renderLose();
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

                //choose levelSelect
                switchScreen(1);
                //TODO choose mode
                //switchScreen(2);
                //choose settings
                //switchScreen(3);
                break;
            case 1:
                grid.loadLevel(2);
                switchScreen(2);
                break;
            case 2:
                return gameTouch(screenX, screenY);
            case 3:
                //settings
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
        return false;
    }

    public void switchScreen(int screen) {
        parentScreen = curScreen;
        curScreen = screen;
        //animate screen coming down
        //except splash and main
    }

    //render level numbers and stars onto squares
    public void renderLevelSelect() {
        stars = Gdx.files.local("levelStars.txt");
        if (Gdx.files.local("levelStars.txt").exists()) {
            String data = stars.readString();
            if (data.length() == 0) {
                String s = "";
                for (int i = 0; i < Constants.maxLevels; i++) {
                    s += "0";
                }
                stars.writeString(s,false);
            }
            System.out.println("File data: " + data);
        }
        else {
            String s = "";
            for (int i = 0; i < Constants.maxLevels; i++) {
                s += "0";
            }
            stars.writeString(s,false);
            levelsComplete = 0;
        }
        int numStars = 0;
        //set the color of levelCompleted + 1 and lower to be brighter
        renderer.setColor(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                int index = i + j * 4;
                if (Gdx.files.local("levelStars.txt").exists()) {
                    String s = stars.readString();
                    if (s.length() >= Constants.maxLevels) {
                        numStars = Integer.parseInt("" + stars.readString().charAt(index));
                    }
                    else {
                        numStars = 0;
                        s += "0";
                        stars.writeString("0",true);
                    }
                }
                else {
                    numStars = 0;
                }
                int xPos = i * Constants.boxSize * 3 + Constants.margin * 2;
                int yPos = worldHeight * 3 / 4 - j * Constants.boxSize * 3 + Constants.margin;
                renderer.rect(xPos,yPos, Constants.boxSize * 2,Constants.boxSize * 2);
                CharSequence curLevel = "" + (index + 1);
                font.setColor(Color.BLACK);
                font.draw(spriteBatch,curLevel,xPos,yPos);
            }
        }
    }

    public void renderGame() {
        renderGrid();
        renderGridUI();
        if (grid.checkObjectives()) {
            if (grid.level > levelsComplete) {
                levelsComplete = grid.level;
                complete.writeString("" + levelsComplete,false);
            }
            //check stars
            //update stars
            stars = Gdx.files.local("levelStars.txt");
            if (Gdx.files.local("levelStars.txt").exists()) {
                String s = "";
                String sLine = stars.readString();
                stars.writeString(sLine.substring(0,grid.level) + "1" + sLine.substring(grid.level+1,Constants.maxLevels),false);
            }
            else {
                String s = "";
                for (int i = 0; i < Constants.maxLevels; i++) {
                    if (grid.level == i) {
                        s += "1";
                        //add star amount
                    }
                    s += "0";
                }
                stars.writeString(s,false);
            }
            System.out.println("Completed Level");
            //prompt for next level or level select
            switchScreen(4);
        }
        if (grid.checkFail()) {
            System.out.println("Lost");
            switchScreen(5);
        }
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

    //change spacing depending on number of objectives
    public void renderGridUI() {
        font.setColor(Color.WHITE);
        //objectives
        int numObjectives = 0;

        //TODO add moves left and menu button
        for (int i = 0; i < Constants.numColors; i++) {
            if (grid.colorObjectives[i] > 0 && i >= 2) {
                renderer.setColor(Square.getColor(i));
                int xPos = (int)(numObjectives * Constants.boxSize * 1.5 + Constants.margin * 1.5);
                int yPos = worldHeight - Constants.boxSize * 2;
                renderer.rect(xPos, yPos, Constants.margin, Constants.margin);
                font.getData().setScale(2,2);
                int destroyed = grid.colorDestroyed[i];
                if (destroyed > grid.colorObjectives[i]) {
                    destroyed = grid.colorObjectives[i];
                }
                font.draw(spriteBatch,destroyed + "/" + grid.colorObjectives[i],xPos,yPos - Constants.boxSize);
                numObjectives++;
            }
        }


        //score
        font.getData().setScale(3,3);
        font.draw(spriteBatch,leadingZeros(grid.getScore()),200,200);
    }

    //can make more succinct
    public CharSequence leadingZeros(int s) {
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
        return score;
    }


    public void renderWin() {
        //temporary win screen
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"You win",200,200);
    }

    public void renderLose() {
        //temporary lose screen
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"You lose",200,200);
    }
}
