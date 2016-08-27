package com.hla0.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.Swatch;
import com.hla0.util.Constants;

public class SplashScreen extends InputAdapter implements Screen{
    private Swatch game;
    private Texture texture;
    private Viewport viewport;
    private OrthographicCamera camera;
    boolean screenTransition;
    ShapeRenderer renderer;
    int yPos;
    int yVelocity;
    int nextScreen;
    boolean enter;
    float alpha;
    public SplashScreen (Swatch g) {
        renderer = new ShapeRenderer();
        game = g;
        camera = new OrthographicCamera();
        texture = new Texture("Swatch Splash Icon.png");
        //viewport = new ScreenViewport(camera);
        //viewport = new StretchViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        viewport = new FitViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        enter = true;
        alpha = 1;
    }

    @Override
    public void show() {
        screenTransition = false;
        yPos = 0;
        yVelocity = 0;
        nextScreen = -1;
        alpha = 1;
        enter = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .5f, .5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        viewport.apply();
        render();
    }

    public void render() {
        game.batch.setProjectionMatrix(camera.combined);
        if (enter) {
            renderEnter();
        }
        if (screenTransition) {
            renderScreenTransition();
        }
        else {
            game.batch.begin();
            game.batch.draw(texture, -Swatch.worldWidth/2, -Swatch.worldHeight / 5);
            game.batch.end();
        }
    }

    public void renderScreenTransition() {

        yVelocity += Constants.SCREEN_ACCELERATION;
        yPos += yVelocity;
        //game.render(nextScreen);
        if (yPos > Swatch.worldHeight) {
            screenTransition = false;
            game.setScreen(nextScreen,0);
        }
        game.batch.begin();
        game.batch.draw(texture, -Swatch.worldWidth/2, -Swatch.worldHeight / 5 + yPos);
        game.batch.end();
    }

    public void renderEnter() {
        if (alpha > 0) {
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            alpha -= Constants.FADE_SPEED;
            game.renderer.end();
        }
        else {
            enter = false;
        }
    }
    @Override public void resize(int width, int height) {viewport.update(width,height);} @Override public void pause() {}@Override public void resume() {}@Override public void hide() {}@Override public void dispose() {}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.getCurScreen() == 0) {
            splashTouch(screenX,screenY);
            return true;
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
        System.out.println(viewport.unproject(new Vector2(screenX,screenY)).x + ", " + viewport.unproject(new Vector2(screenX,screenY)).y);

        if (viewport.unproject(new Vector2(screenX,screenY)).x > 0) {
            screenTransition = true;
            System.out.println("settings");
            nextScreen = 3;
        }
        else {
            screenTransition = true;
            //level select
            nextScreen = 1;
        }
        return true;
    }
    public void start() {enter = true; alpha = 1;}
}
