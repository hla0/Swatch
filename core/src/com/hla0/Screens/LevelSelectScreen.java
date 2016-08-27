package com.hla0.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hla0.Swatch;
import com.hla0.util.Constants;


public class LevelSelectScreen extends InputAdapter implements Screen{
    FileHandle stars;
    FileHandle complete;
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    Swatch game;
    int levelsComplete;
    int nextScreen;
    String levelStars;
    boolean enter;
    boolean exit;
    Sound buttonPress;
    public LevelSelectScreen (Swatch g) {
        game = g;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        game.renderer.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        parseCompleteFile(complete);
        levelStars = parseStarFile(stars);
        System.out.println(levelStars);
        enter = true;
        exit = false;
        //find a better button sound
        buttonPress = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
        nextScreen = -1;
    }

    private void parseCompleteFile(FileHandle complete) {
        complete = Gdx.files.local("levelsComplete.txt");
        if (Gdx.files.local("levelsComplete.txt").exists()) {
            String data = complete.readString();
            System.out.println("File data: " + data);
            levelsComplete = Integer.parseInt(data);
        }
        else {
            levelsComplete = 0;
            complete.writeString("0",false);
        }
    }

    private String parseStarFile (FileHandle stars) {
        String data;
        stars = Gdx.files.local("levelStars.txt");
        if (Gdx.files.local("levelStars.txt").exists()) {
             data = stars.readString();
            if (data.length() == 0) {
                String s = "";
                for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                    s += "0";
                }
                stars.writeString(s,false);
            }
            while (data.length() < Constants.MAX_LEVEL) {
                data += "0";
                stars.writeString("0",true);
            }
        }
        else {
            data = "";
            for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                data += "0";
            }
            stars.writeString(data,false);
            levelsComplete = 0;
        }
        return data;
    }

    @Override public void show() {
        enter = true;
        exit = false;
        parseCompleteFile(complete);
        levelStars = parseStarFile(stars);
        buttonPress = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
    }
    @Override
    public void render(float delta) {
        viewport.apply();
        render();
    }

    public void render() {
        if (enter) {
            System.out.println("entering level select");
            enter = false;
        }
        else if (exit) {
            System.out.println("exiting level select");
            game.setScreen(2,1);
            exit = false;
        }
        else {
            game.renderer.setProjectionMatrix(camera.combined);
            game.batch.setProjectionMatrix(camera.combined);
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            game.renderer.end();
            renderLevelSelect();
        }
    }


    //render level numbers and stars onto squares
    public void renderLevelSelect() {
        //need to separate into two loops to put text in box
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                int index = i + j * 4;
                //int numStars = Integer.parseInt(levelStars.substring(index,index+1));
                game.renderer.begin(ShapeRenderer.ShapeType.Filled);
                if (index + 1 > levelsComplete + 1) {
                    game.renderer.setColor(Color.BLACK);
                }
                else {
                    game.renderer.setColor(Color.WHITE);
                }
                int xPos = (i * Constants.BOX_SIZE * 3 + Constants.MARGIN * 2) - Swatch.worldWidth / 2;
                int yPos = j * Constants.BOX_SIZE * 3 + Constants.MARGIN * 2 - Swatch.worldHeight / 4;
                game.renderer.rect(xPos,yPos, Constants.BOX_SIZE * 2,Constants.BOX_SIZE * 2);
                game.renderer.end();
                game.batch.begin();
                if (index + 1 > levelsComplete + 1) {
                    game.renderer.setColor(Color.BLACK);
                    font.setColor(Color.WHITE);
                }
                else {
                    game.renderer.setColor(Color.WHITE);
                    font.setColor(Color.BLACK);
                }
                CharSequence curLevel = "" + (index + 1);
                font.draw(game.batch,curLevel,xPos,yPos);
                game.batch.end();
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.getCurScreen() == 1) {
            exit = true;
            //if touched level
            if (game.isSound()) {
                buttonPress.play();
            }
            nextScreen = 2;
            return true;
        }
        else {
            return false;
        }
    }


    @Override public void resize(int width, int height) {viewport.update(width,height);}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {buttonPress.dispose();
    }
    public void start() {enter = true;}
}
