package com.hla0.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hla0.Swatch;

/**
 * Created by aft99 on 8/26/2016.
 */
public class SettingsScreen extends InputAdapter implements Screen {
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    Swatch game;
    boolean enter;

    public SettingsScreen (Swatch g) {
        game = g;
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.worldWidth, game.worldHeight, camera);
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enter = true;
    }

    public void renderSettings() {
        game.renderer.begin(ShapeRenderer.ShapeType.Filled);
        game.renderer.setColor(Color.TAN);
        game.renderer.rect(-Swatch.worldWidth/2,-Swatch.worldHeight/2, Swatch.worldWidth, Swatch.worldHeight);
        game.renderer.end();
        game.batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3, 3);
        font.draw(game.batch, "Settings", 0, 0);
        game.batch.end();
    }


    @Override public void show() {}
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .5f, .5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        render();
    }

    public void render() {
        viewport.apply();
        game.renderer.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        renderSettings();
    }

    @Override public void resize(int width, int height) {viewport.update(width,height);}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.getCurScreen() == 3) {
            //getParentScreen
            game.setScreen(game.getParentScreen(),3);
            return true;
        }
        else {
            return false;
        }
    }

    public void start() {enter = true;}

}
