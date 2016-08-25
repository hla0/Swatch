package com.hla0;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.util.ArrayList;

public class Swatch extends Game {
	SwatchScreen swatchScreen;
	SplashScreen splashScreen;
	SettingsScreen settingsScreen;
	LevelScreen levelScreen;
	InputMultiplexer im;
	int curScreen;
	public void create() {
		im = new InputMultiplexer();
		splashScreen = new SplashScreen(this);
		swatchScreen = new SwatchScreen(this);
		settingsScreen = new SettingsScreen(this);
		levelScreen = new LevelScreen(this);
		im.addProcessor(swatchScreen);
		im.addProcessor(splashScreen);
		//im.addProcessor(settingsScreen);
		//im.addProcessor(levelScreen);
		Gdx.input.setInputProcessor(im);
		setScreen(0);
	}

	//overloaded set screen to give more control to Swatch
	public void setScreen(int index) {
		switch (index) {
			case 0:
				curScreen = 0;
				setScreen(splashScreen);
				break;
			case 1:
				curScreen = 1;
				setScreen(levelScreen);
				break;
			case 2:
				curScreen = 2;
				setScreen(swatchScreen);
				break;
		}
		System.out.println(curScreen);
	}

	//overloaded set Screen to allow loading levels from other screens
	public void setScreen(int index, int level) {
		setScreen(2);
		swatchScreen.setLevel(level);
	}

	/*
	public static final String TAG = Swatch.class.getName();
	ShapeRenderer renderer;
	Grid grid;
	OrthographicCamera camera;
	Viewport viewport;
	BitmapFont font;
	SpriteBatch spriteBatch;

	//TODO Add separate screens and move current functions into relevant screen
	@Override
	public void create () {
		camera = new OrthographicCamera();
		int width = 8;
		int height = 8;
		int level = 1;
		viewport = new FitViewport(width * (Constants.boxSize + Constants.margin) + Constants.margin,
				height * (Constants.boxSize + Constants.margin) + Constants.topPadding + Constants.bottomPadding + Constants.margin,
				camera);
		grid = new Grid(width,height, viewport, level);
		renderer = new ShapeRenderer();
		font = new BitmapFont();
		spriteBatch = new SpriteBatch();
		Gdx.input.setInputProcessor(grid);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		renderer.setProjectionMatrix(camera.combined);
		spriteBatch.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Filled);
		renderGrid();
		//TODO find better solution to hide new squares at top
		//temporary solution
		renderer.setColor(0,0,0,1);
		renderer.rect(0,Constants.bottomPadding + 10 * (Constants.boxSize + Constants.margin) + Constants.margin,330,Constants.topPadding);
		spriteBatch.begin();
		renderGridUI();
		spriteBatch.end();
		renderer.end();

	}

	public void renderGrid() {
		Square[][] squares = grid.getSquares();
		ArrayList<Square> deleted = grid.getDeleted();
		ArrayList<Square> swapped = grid.getSwapped();
		for (int i = 0; i < grid.getWidth(); i++) {
			for (int j = 0; j < grid.getHeight(); j++) {
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

	public void renderGridUI() {
		//TODO add leading zeros
		int s = grid.getScore();
		CharSequence score = "" + s;
		int length = 0;
		if (s == 0) {
			length = 1;
		}
		for (int i = 0; s > 0; i++) {
			s /= 10;
			length++;
		}
		for (int i = length; i < 6; i++) {
			score = "0" + score;
		}
		font.setColor(Color.WHITE);
		font.getData().setScale(3,3);
		font.draw(spriteBatch,score,200,200);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		//Gdx.app.log(TAG, "Viewport world dimensions: (" + viewport.getWorldHeight() + ", " + viewport.getWorldWidth() + ")");
	}

	@Override
	public void dispose () {
		renderer.dispose();
		spriteBatch.dispose();
		super.dispose();
	}
	*/
}
