package com.hla0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hla0.util.Constants;

import java.util.ArrayList;

public class Renderer {
    ShapeRenderer renderer;
    SpriteBatch spriteBatch;
    BitmapFont font;
    int yPos;
    int yVelocity;
    int worldWidth;
    int worldHeight;
    FileHandle complete;
    FileHandle stars;
    int levelsComplete;
    SwatchScreen swatchScreen;
    Grid grid;
    boolean animating;

    Renderer(SwatchScreen s,int w, int h, Grid g) {
        swatchScreen = s;
        yPos = 0;
        yVelocity = 0;
        worldWidth = w;
        worldHeight = h;
        animating = false;
        grid = g;
        renderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        spriteBatch = new SpriteBatch();
        stars = Gdx.files.local("levelStars.txt");
        complete = Gdx.files.local("levelsComplete.txt");
        if (Gdx.files.local("levelsComplete.txt").exists()) {
            String data = complete.readString();
            System.out.println("File data: " + data);
            levelsComplete = Integer.parseInt(complete.readString());
        }
        else {
            String string = "";
            for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                string += "0";
            }
            stars.writeString(string,false);
            levelsComplete = 0;
        }
    }
    public boolean render(int curScreen, int parentScreen, boolean animating,OrthographicCamera camera) {
        renderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        this.animating = animating;
        if (!animating) {
            yPos = 0;
            yVelocity = 0;
        }
        else {
            yVelocity += Constants.SCREEN_ACCELERATION;
            yPos += yVelocity;
            if (yPos > worldHeight) {
                animating = false;
            }
        }
        switch (curScreen) {
            case 0:
                renderSplashScreen(0);
                if (animating) {
                    renderScreen(parentScreen,yPos);
                }
                break;
            case 1:
                renderLevelSelect(yPos);
                if (animating) {
                    renderScreen(parentScreen,yPos);
                }
                break;
            case 2:
                renderGame();
                if (animating) {
                    renderScreen(parentScreen,yPos);
                }
                break;
            case 3:
                System.out.println("in settings");
                if (parentScreen != 2) {
                    renderSettings(0,false);
                }
                if (animating) {
                    renderScreen(parentScreen,yPos);
                }
                if (parentScreen == 2) {
                    renderSettings(yPos,true);
                }

                //switchScreen(parentScreen);
                break;
            case 4:
                //not animating currently
                renderWin(yPos);
                if (animating) {
                    renderGame();
                }
                break;
            case 5:
                renderLose(yPos);
                if (animating) {
                    renderGame();
                }
                break;
        }
        this.animating = animating;
        return animating;
    }


    void renderScreen(int screen, int y) {
        switch(screen) {
            case 0:
                renderSplashScreen(y);
                break;
            case 1:
                renderLevelSelect(y);
                break;
            case 2:
                renderGame();
                break;
            case 3:
                renderSettings(y,false);
                break;
            case 4:
                //might be off since it is going down
                renderWin(y);
                break;
            case 5:
                renderLose(y);
                break;
        }
    }

    //TODO image with surronnding a solid color that matches background
    public void renderSplashScreen (int y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GOLD);
        renderer.rect(0,y,worldWidth,worldHeight - y);
        System.out.println(y);
        renderer.end();
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"Swatch",200,500 + y);
        spriteBatch.end();
    }
    //
    public void renderSettings(int y, boolean down) {
        if (!animating) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Color.TAN);
            renderer.rect(0, 0, worldWidth, worldHeight);
            renderer.end();
            spriteBatch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(3, 3);
            font.draw(spriteBatch, "Settings", 200, 500);
            spriteBatch.end();
        }
        else {
            if (down) {
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.setColor(Color.TAN);
                renderer.rect(0, worldHeight - y, worldWidth, worldHeight);
                renderer.end();
                spriteBatch.begin();
                font.setColor(Color.WHITE);
                font.getData().setScale(3, 3);
                font.draw(spriteBatch, "Settings", 200, worldHeight - y + 500);
                spriteBatch.end();
            } else {
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.setColor(Color.TAN);
                renderer.rect(0, y, worldWidth, worldHeight);
                renderer.end();
                spriteBatch.begin();
                font.setColor(Color.WHITE);
                font.getData().setScale(3, 3);
                font.draw(spriteBatch, "Settings", 200, 500 + y);
                spriteBatch.end();
            }
        }
    }
    //render level numbers and stars onto squares
    public void renderLevelSelect(int y) {
        //need to separate into two loops to put text in box
        stars = Gdx.files.local("levelStars.txt");
        if (Gdx.files.local("levelStars.txt").exists()) {
            String data = stars.readString();
            if (data.length() == 0) {
                String s = "";
                for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                    s += "0";
                }
                stars.writeString(s,false);
            }
        }
        else {
            String s = "";
            for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                s += "0";
            }
            stars.writeString(s,false);
            levelsComplete = 0;
        }
        int numStars = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                int index = i + j * 4;
                if (Gdx.files.local("levelStars.txt").exists()) {
                    String s = stars.readString();
                    if (s.length() >= Constants.MAX_LEVEL) {
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
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                if (index + 1 > levelsComplete + 1) {
                    renderer.setColor(Color.BLACK);
                }
                else {
                    renderer.setColor(Color.WHITE);
                }
                int xPos = i * Constants.BOX_SIZE * 3 + Constants.MARGIN * 2;
                int yPos = worldHeight * 3 / 4 - j * Constants.BOX_SIZE * 3 + Constants.MARGIN;
                renderer.rect(xPos,yPos + y, Constants.BOX_SIZE * 2,Constants.BOX_SIZE * 2);
                renderer.end();

                spriteBatch.begin();
                if (index + 1 > levelsComplete + 1) {
                    renderer.setColor(Color.BLACK);
                    font.setColor(Color.WHITE);
                }
                else {
                    renderer.setColor(Color.WHITE);
                    font.setColor(Color.BLACK);
                }
                CharSequence curLevel = "" + (index + 1);
                font.draw(spriteBatch,curLevel,xPos,yPos + y);
                spriteBatch.end();
            }
        }
    }

    public void renderGame() {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        spriteBatch.begin();
        renderGrid();
        renderGridUI();
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
        spriteBatch.end();
        renderer.end();
    }

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

    public void renderGrid() {
        Square[][] squares = grid.getSquares();
        ArrayList<Square> deleted = grid.getDeleted();
        ArrayList<Square> swapped = grid.getSwapped();
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
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
        for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
            if (grid.colorObjectives[i] > 0) {
                renderer.setColor(Square.getColor(i));
                int xPos = (int)(numObjectives * Constants.BOX_SIZE * 1.5 + Constants.MARGIN * 1.5 + Constants.LEFT_PADDING);
                int yPos = worldHeight - Constants.BOX_SIZE * 4;
                renderer.rect(xPos, yPos, Constants.MARGIN, Constants.MARGIN);
                font.getData().setScale(2,2);
                int destroyed = grid.colorDestroyed[i];
                if (destroyed > grid.colorObjectives[i]) {
                    destroyed = grid.colorObjectives[i];
                }
                font.draw(spriteBatch,destroyed + "/" + grid.colorObjectives[i],xPos - 5,yPos - Constants.BOX_SIZE);
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


    public void renderWin(int y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.CHARTREUSE);
        renderer.rect(0,y,worldWidth,worldHeight);
        renderer.end();
        //temporary win screen
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"You win",200,200 + y);
        spriteBatch.end();
    }

    public void renderLose(int y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.CHARTREUSE);
        renderer.rect(0,y,worldWidth,worldHeight);
        renderer.end();
        //temporary lose screen
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"You lose",200,200 + y);
        spriteBatch.end();
    }
}
