package com.hla0.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.Swatch;

public class SplashScreen extends InputAdapter implements Screen{
    private Swatch game;
    private Texture texture;
    private Viewport viewport;
    private OrthographicCamera camera;
    public SplashScreen (Swatch g) {
        game = g;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        //viewport = new StretchViewport(Swatch.worldWidth,Swatch.worldHeight,camera);

    }

    @Override
    public void show() {
        texture = new Texture("badlogic.jpg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        System.out.println("Inside splash screen");
        game.batch.draw(texture,0,0);
        game.batch.end();
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
            System.out.println("settings");
            game.setScreen(3,0);
        }
        else {
            //level select
            game.setScreen(1,0);
        }
        return true;
    }
}
