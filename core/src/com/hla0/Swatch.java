package com.hla0;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hla0.Screens.LevelSelectScreen;
import com.hla0.Screens.SettingsScreen;
import com.hla0.util.Constants;
import com.hla0.Screens.SwatchScreen;
import com.hla0.Screens.SplashScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Swatch extends Game {
	public SpriteBatch batch;
	public ShapeRenderer renderer;
	public static final int worldWidth = Constants.LEFT_PADDING + Constants.RIGHT_PADDING + Constants.GRID_SIZE * (Constants.BOX_SIZE + Constants.MARGIN) + Constants.MARGIN;
	public static final int worldHeight = Constants.GRID_SIZE * (Constants.BOX_SIZE + Constants.MARGIN) + Constants.TOP_PADDING + Constants.BOTTOM_PADDING + Constants.MARGIN;
	int parentScreen;
	int curScreen;
	private SwatchScreen swatchScreen;
	private SplashScreen splashScreen;
	private SettingsScreen settingsScreen;
	private LevelSelectScreen levelSelectScreen;
	InputMultiplexer im;
	@Override
	public void create () {
		curScreen = 0;
		parentScreen = -1;
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		swatchScreen = new SwatchScreen(this);
		splashScreen = new SplashScreen(this);
		settingsScreen = new SettingsScreen(this);
		levelSelectScreen = new LevelSelectScreen(this);

		im = new InputMultiplexer();
		im.addProcessor(splashScreen);
		im.addProcessor(levelSelectScreen);
		im.addProcessor(swatchScreen);
		im.addProcessor(settingsScreen);
		for (int i = 0; i < 4; i++) {
			render(i);
		}
		setScreen(splashScreen);
		Gdx.input.setInputProcessor(im);
	}

	public void setScreen(int screen, int parent) {
		switch (screen) {
			case 0:
				setScreen(splashScreen);
				splashScreen.start();
				break;
			case 1:
				setScreen(levelSelectScreen);
				levelSelectScreen.start();
				break;
			case 2:
				setScreen(swatchScreen);
				swatchScreen.start();
				break;
			case 3:
				setScreen(settingsScreen);
				settingsScreen.start();
				break;
		}
		//reset all the screens besides the screen switched to
		//resetScreens();
		parentScreen = parent;
		curScreen = screen;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	public int getParentScreen() {return parentScreen;}

	public int getCurScreen() {return curScreen;}
	public void render(int i) {
		switch (i) {
			case 0:
				splashScreen.render();
				break;
			case 1:
				levelSelectScreen.render();
				break;
			case 2:
				swatchScreen.render();
				break;
			case 3:
				settingsScreen.render();
				break;
		}
	}


}
