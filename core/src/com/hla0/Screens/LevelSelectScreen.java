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
import com.badlogic.gdx.math.Vector2;
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
    Texture levelButton;
    Texture lockedLevelButton;
    int levelsComplete;
    int nextScreen;
    String levelStars;
    boolean enter;
    boolean exit;
    Sound buttonPress;
    Vector2[] positions;
    Vector2[] size;
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
        positions = new Vector2[Constants.MAX_LEVEL];
        size = new Vector2[Constants.MAX_LEVEL];
        //find a better button sound
        buttonPress = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
        nextScreen = -1;
        levelButton = new Texture("level_button.png");
        lockedLevelButton = new Texture("locked_level_button.png");
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
                int xPos = (i * Constants.BOX_SIZE * 3 + Constants.MARGIN * 2) - Swatch.worldWidth / 2;
                int yPos = - j * Constants.BOX_SIZE * 3 + Constants.MARGIN * 2 + Swatch.worldHeight / 5;
                positions[index] = new Vector2(xPos,yPos);
                size[index] = new Vector2(Constants.BOX_SIZE * 2,Constants.BOX_SIZE * 2);
                game.batch.begin();
                CharSequence curLevel = "" + (index + 1);
                font.setColor(Color.BLACK);
                if (index + 1 > levelsComplete + 1) {
                    game.batch.draw(lockedLevelButton, xPos, yPos);
                }
                else {
                    game.batch.draw(levelButton,xPos,yPos);
                }
                font.getData().setScale(2,2);
                if (index + 1 >= 10) {
                    font.draw(game.batch,curLevel,xPos + Constants.BOX_SIZE - 15,yPos + Constants.BOX_SIZE + 12);
                }
                else {
                    font.draw(game.batch, curLevel, xPos + Constants.BOX_SIZE - 5, yPos + Constants.BOX_SIZE + 12);
                }
                game.batch.end();
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.getCurScreen() == 1) {
            Vector2 pos = viewport.unproject(new Vector2(screenX,screenY));
            int level = levelPressed((int)pos.x,(int)pos.y);
            System.out.println("Pressed " + level);
            if (level > 0 && level <= levelsComplete + 1) {
                exit = true;
                if (game.isSound()) {
                    buttonPress.play();
                }
                nextScreen = 2;
                game.loadLevel(level);
            }
            else {
                //TODO should be in start screen for free play mode
                exit = true;
                if (game.isSound()) {
                    buttonPress.play();
                }
                nextScreen = 2;
                game.loadLevel(0);
                //determine if another button was pressed or check that first
            }
            return true;
        }
        else {
            return false;
        }
    }

    public int levelPressed(int screenX, int screenY) {
        for (int i = 0; i < Constants.MAX_LEVEL; i++) {
            Vector2 pos = positions[i];
            Vector2 s = size[i];
            if (screenX > pos.x && screenX < pos.x + s.x) {
                if (screenY > pos.y && screenY < pos.y + s.y) {
                    return i + 1;
                }
            }
        }
        return 0;
    }


    @Override public void resize(int width, int height) {viewport.update(width,height);}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {buttonPress.dispose();
    }
}
