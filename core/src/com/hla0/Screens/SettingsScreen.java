package com.hla0.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
    boolean exit;
    Sound buttonPress;
    public SettingsScreen (Swatch g) {
        game = g;
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.worldWidth, game.worldHeight, camera);
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enter = true;
        exit = false;
        buttonPress = Gdx.audio.newSound(Gdx.files.internal("select.wav"));
    }

    public void renderSettings() {
        if (enter) {
            System.out.println("entering settings");
            enter = false;
        }
        else if (exit) {
            System.out.println("exiting settings");
            exit = false;
            game.setScreen(game.getParentScreen(),3);
        }
        else {
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            game.renderer.setColor(Color.TAN);
            game.renderer.rect(-Swatch.worldWidth / 2, -Swatch.worldHeight / 2, Swatch.worldWidth, Swatch.worldHeight);
            game.renderer.end();
            game.batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(3, 3);
            font.draw(game.batch, "Settings", 0, 0);
            game.batch.end();
        }
    }


    @Override public void show() {enter = true; exit = false;buttonPress = Gdx.audio.newSound(Gdx.files.internal("select.wav"));}
    @Override
    public void render(float delta) {
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
            if (game.isSound()) {
                buttonPress.play();
            }
            game.toggleSound();
            game.toggleMusic();
            exit = true;
            return true;
        }
        else {
            return false;
        }
    }
    //TODO remove these
    public void start() {enter = true;}

}
