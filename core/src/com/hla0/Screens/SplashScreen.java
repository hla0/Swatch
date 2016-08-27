package com.hla0.Screens;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.Square;
import com.hla0.Swatch;
import com.hla0.util.Constants;

public class SplashScreen extends InputAdapter implements Screen{
    private Swatch game;
    private Texture texture;
    private Viewport viewport;
    private OrthographicCamera camera;
    int yPos;
    int yVelocity;
    int nextScreen;
    boolean enter;
    boolean exit;
    float alpha;
    float time;
    Sound buttonPress;
    boolean startPressed;
    boolean settingsPressed;
    public SplashScreen (Swatch g) {
        startPressed = false;
        settingsPressed = false;
        time = 0;
        game = g;
        camera = new OrthographicCamera();
        //texture = new Texture("badlogic.jpg");
        texture = new Texture("splash_icon.png");
        //viewport = new ScreenViewport(camera);
        //viewport = new StretchViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        viewport = new FitViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        enter = true;
        exit = false;
        //TODO find a better button sound
        buttonPress = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
        alpha = 1;
    }

    @Override
    public void show() {
        time = 0;
        exit = false;
        yPos = 0;
        yVelocity = 0;
        nextScreen = -1;
        alpha = 1;
        enter = true;
        startPressed = false;
        settingsPressed = false;
        buttonPress = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        render();
    }

    public void render() {
        game.batch.setProjectionMatrix(camera.combined);
        if (exit) {
            //System.out.println("Exiting splash");
            renderExit();
        }
        else {
            time++;
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < 6; i++) {
                //draw rotating squares
                game.renderer.setColor(Square.getColor(i + 2));
                game.renderer.rect((int)(Math.cos(-i * 45 + time / 45) * 6 * Constants.BOX_SIZE) - Constants.BOX_SIZE/2,
                        (int)(Math.sin(-i * 45 + time / 45) * 6 * Constants.BOX_SIZE) - Constants.BOX_SIZE/2,
                        Constants.BOX_SIZE, Constants.BOX_SIZE);
            }

            //if button not pressed
            if (!startPressed) {
                game.renderer.setColor(0/255f,172/255f,193/255f,1);
            }
            //pressed
            else {
                game.renderer.setColor(0/255f, 131/255f, 143/255f, 1);
            }
            game.renderer.rect(-Constants.BOX_SIZE * 4 / 2,-Constants.BOX_SIZE * 3 - texture.getHeight(),Constants.BOX_SIZE * 4, Constants.BOX_SIZE * 3 / 2);
            game.renderer.end();
            game.batch.begin();
            game.batch.draw(texture, -texture.getWidth()/2, -texture.getHeight()/2);
            game.batch.end();
        }
        if (enter) {
            //System.out.println("Entering splash");
            renderEnter();
        }
    }

    public void renderExit() {
        yVelocity += Constants.SCREEN_ACCELERATION;
        yPos += yVelocity;
        //game.render(nextScreen);
        if (yPos > Swatch.worldHeight) {
            exit = false;
            game.setScreen(nextScreen,0);
        }
        game.renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 6; i++) {
            //draw rotating squares
            game.renderer.setColor(Square.getColor(i + 2));
            game.renderer.rect((int)(Math.cos(-i * 45 + time / 45) * 6 * Constants.BOX_SIZE) - Constants.BOX_SIZE/2,
                    ((int)(Math.sin(-i * 45 + time / 45) * 6 * Constants.BOX_SIZE) - Constants.BOX_SIZE/2) + yPos,
                    Constants.BOX_SIZE, Constants.BOX_SIZE);
        }
        if (startPressed) {
            game.renderer.setColor(0 / 255f, 131 / 255f, 143 / 255f, 1);
        }
        else {
            game.renderer.setColor(0/255f,172/255f,193/255f,1);
        }
        game.renderer.rect(-Constants.BOX_SIZE * 4 / 2,-Constants.BOX_SIZE * 3 - texture.getHeight() + yPos,Constants.BOX_SIZE * 4, Constants.BOX_SIZE * 3 / 2);
        //TODO animate settings leaving
        if (settingsPressed) {} else {}
        game.renderer.end();
        game.batch.begin();
        game.batch.draw(texture, -texture.getWidth()/2, -texture.getHeight()/2 + yPos);
        game.batch.end();
    }

    public void renderEnter() {
        if (alpha > 0) {
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            //game.renderer.setColor(0,.5f,.5f,0);
            //game.renderer.rect(-Swatch.worldWidth/2,-Swatch.worldHeight/2,Swatch.worldWidth,Swatch.worldHeight);
            alpha -= Constants.FADE_SPEED;
            game.renderer.end();
            //System.out.println("Alpha: " + alpha);
        }
        else {
            enter = false;
        }
    }
    @Override public void resize(int width, int height) {viewport.update(width,height);} @Override public void pause() {}@Override public void resume() {}@Override public void hide() {}@Override public void dispose() {buttonPress.dispose();}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.getCurScreen() == 0) {
            splashTouch(viewport.unproject(new Vector2(screenX,screenY)),true);
            return true;
        }
        return false;
    }

    public boolean splashTouch(Vector2 pos, boolean down) {
        //choose levelSelect
        //switchScreen(1);
        //TODO choose mode
        //switchScreen(2);
        //choose settings
        //temporary
       // -Constants.BOX_SIZE * 4 / 2,-Constants.BOX_SIZE * 3 - texture.getHeight(),Constants.BOX_SIZE * 4, Constants.BOX_SIZE * 3 / 2
        //System.out.println("in box");
        if (pos.y > -390 && pos.y < -390 + Constants.BOX_SIZE * 3 / 2) {
            if (pos.x > -90 && pos.x < 90) {
                System.out.println("in box");
                startPressed = true;
                buttonPress.play();
                nextScreen = 1;
                exit = true;
            }
        } else {
            exit = true;
            settingsPressed = true;
            System.out.println("settings");
            buttonPress.play();
            nextScreen = 3;
        }

        return true;
    }
    public void start() {enter = true; alpha = 1;}
}
