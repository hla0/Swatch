package com.hla0;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.hla0.util.Constants;

import java.util.ArrayList;

/**
 * Created by aft99 on 8/25/2016.
 */
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

    Renderer(SwatchScreen s, ShapeRenderer r, SpriteBatch sb, BitmapFont f, int w, int h, Grid g) {
        swatchScreen = s;
        renderer = r;
        spriteBatch = sb;
        font = f;
        yPos = 0;
        yVelocity = 0;
        worldWidth = w;
        worldHeight = h;
        grid = g;
        stars = Gdx.files.local("levelStars.txt");
        complete = Gdx.files.local("levelsComplete.txt");
        if (Gdx.files.local("levelsComplete.txt").exists()) {
            String data = complete.readString();
            System.out.println("File data: " + data);
            levelsComplete = Integer.parseInt(complete.readString());
        }
        else {
            String string = "";
            for (int i = 0; i < Constants.maxLevels; i++) {
                string += "0";
            }
            stars.writeString(string,false);
            levelsComplete = 0;
        }
    }
    public boolean render(int curScreen, int parentScreen, boolean animating) {
        if (!animating) {
            yPos = 0;
            yVelocity = 0;
        }
        else {
            yVelocity += Constants.screenAcceleration;
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
                    //temporary
                    /*
                    if (parentScreen == 2) {
                        renderer.begin(ShapeRenderer.ShapeType.Filled);
                        renderer.setColor(Color.TEAL);
                        renderer.rect(0,0,worldWidth,worldHeight);
                        renderer.end();
                    }
                    */
                    renderScreen(parentScreen,yPos);
                }
                break;
            case 3:
                System.out.println("in settings");
                renderSettings(0);
                if (animating) {
                    renderScreen(parentScreen,yPos);
                }
                //switchScreen(parentScreen);
                break;
            case 4:
                if (animating) {
                    renderGame();
                }
                renderWin(yPos);
                break;
            case 5:
                if (animating) {
                    renderGame();
                }
                renderLose(yPos);
                break;
        }
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
                renderSettings(y);
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


    public void renderSplashScreen (int y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.TEAL);
        renderer.rect(0,y,worldWidth,worldHeight - y);
        System.out.println(y);
        renderer.end();
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"Swatch",200,500 + y);
        spriteBatch.end();
    }

    public void renderSettings(int y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.TAN);
        renderer.rect(0,y,worldWidth,worldHeight);
        renderer.end();
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"Settings",200,500 + y);
        spriteBatch.end();
    }
    //render level numbers and stars onto squares
    public void renderLevelSelect(int y) {
        //need to separate into two loops to put text in box
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        spriteBatch.begin();
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
                if (index + 1 > levelsComplete + 1) {
                    renderer.setColor(Color.BLACK);
                    font.setColor(Color.WHITE);
                }
                else {
                    renderer.setColor(Color.WHITE);
                    font.setColor(Color.BLACK);
                }
                int xPos = i * Constants.boxSize * 3 + Constants.margin * 2;
                int yPos = worldHeight * 3 / 4 - j * Constants.boxSize * 3 + Constants.margin;
                renderer.rect(xPos,yPos + y, Constants.boxSize * 2,Constants.boxSize * 2);
                CharSequence curLevel = "" + (index + 1);
                font.setColor(Color.BLACK);
                font.draw(spriteBatch,curLevel,xPos,yPos + y);
            }
        }
        spriteBatch.end();
        renderer.end();
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
            if (!grid.isAnimating()) {
                swatchScreen.switchScreen(4);
            }
        }
        if (grid.checkFail()) {
            System.out.println("Lost");
            swatchScreen.switchScreen(5);
        }
        spriteBatch.end();
        renderer.end();
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
            if (grid.colorObjectives[i] > 0) {
                renderer.setColor(Square.getColor(i));
                int xPos = (int)(numObjectives * Constants.boxSize * 1.5 + Constants.margin * 1.5 + Constants.leftPadding);
                int yPos = worldHeight - Constants.boxSize * 4;
                renderer.rect(xPos, yPos, Constants.margin, Constants.margin);
                font.getData().setScale(2,2);
                int destroyed = grid.colorDestroyed[i];
                if (destroyed > grid.colorObjectives[i]) {
                    destroyed = grid.colorObjectives[i];
                }
                font.draw(spriteBatch,destroyed + "/" + grid.colorObjectives[i],xPos - 5,yPos - Constants.boxSize);
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
        renderer.setColor(Color.TEAL);
        renderer.rect(0,y,worldWidth,worldHeight - y);
        renderer.end();
        //temporary win screen
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"You win",200,200);
        spriteBatch.end();
    }

    public void renderLose(int y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.TEAL);
        renderer.rect(0,y,worldWidth,worldHeight - y);
        renderer.end();
        //temporary lose screen
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3,3);
        font.draw(spriteBatch,"You lose",200,200);
        spriteBatch.end();
    }
}
