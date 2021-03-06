package com.hla0;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hla0.Screens.LevelSelectScreen;
import com.hla0.Screens.SettingsScreen;
import com.hla0.util.Constants;
import com.hla0.Screens.SwatchScreen;
import com.hla0.Screens.StartScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Swatch extends Game {
	public SpriteBatch batch;
	public ShapeRenderer renderer;
	public FreeTypeFontGenerator generator;
	public FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	public static final int worldWidth = Constants.LEFT_PADDING + Constants.RIGHT_PADDING + Constants.GRID_SIZE * (Constants.BOX_SIZE + Constants.MARGIN) + Constants.MARGIN;
	public static final int worldHeight = Constants.GRID_SIZE * (Constants.BOX_SIZE + Constants.MARGIN) + Constants.TOP_PADDING + Constants.BOTTOM_PADDING + Constants.MARGIN;
	int parentScreen;
	int curScreen;
	private SwatchScreen swatchScreen;
	private StartScreen startScreen;
	private SettingsScreen settingsScreen;
	private LevelSelectScreen levelSelectScreen;
	InputMultiplexer im;
	Music menu;
	Music swatch;
	boolean music;
	boolean sound;
	@Override
	public void create () {
		curScreen = 0;
		parentScreen = -1;
		generator = new FreeTypeFontGenerator(Gdx.files.internal("Helvetica.otf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		swatchScreen = new SwatchScreen(this);
		startScreen = new StartScreen(this);
		settingsScreen = new SettingsScreen(this);
		levelSelectScreen = new LevelSelectScreen(this);

		im = new InputMultiplexer();
		im.addProcessor(startScreen);
		im.addProcessor(levelSelectScreen);
		im.addProcessor(swatchScreen);
		im.addProcessor(settingsScreen);
		for (int i = 0; i < 4; i++) {
			render(i);
		}
		//TODO file should hold setting details
		music = true;
		sound = true;
		menu = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
		swatch = Gdx.audio.newMusic(Gdx.files.internal("game.mp3"));
		setScreen(0,-1);
		Gdx.input.setInputProcessor(im);
	}

	public void setScreen(int screen, int parent) {
		switch (screen) {
			case 0:
				if (isMusic()) {
					swatch.stop();
					menu.play();
					menu.setLooping(true);
				}
				setScreen(startScreen);
				break;
			case 1:
				if (isMusic()) {
					swatch.stop();
					menu.play();
					menu.setLooping(true);
				}
				setScreen(levelSelectScreen);
				break;
			case 2:
				//play game music instead
				//might need the swatch screen to stop music on win/lose and restart
				//might have music with swatchscreen itself
				if (isMusic()) {
					menu.stop();
					swatch.play();
					swatch.setLooping(true);
				}
				setScreen(swatchScreen);
				break;
			case 3:
				if (isMusic()) {
					if (getParentScreen() == 2) {
						menu.stop();
						swatch.play();
						swatch.setLooping(true);
					}
					else {
						swatch.stop();
						menu.play();
						menu.setLooping(true);
					}
				}
				setScreen(settingsScreen);
				break;
		}
		//reset all the screens besides the screen switched to
		//resetScreens();
		parentScreen = parent;
		curScreen = screen;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(159/255f, 168/255f, 218/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		menu.dispose();
		swatch.dispose();
		renderer.dispose();
	}

	//settings file
	public void toggleMusic() {
		music = !music;
		if (!music) {
			menu.stop();
			swatch.stop();
		}
	}

	public void loadLevel(int level) {
		swatchScreen.loadLevel(level);
	}

	public void toggleSound() {
		sound = !sound;
	}
	public boolean isMusic () {
		return music;
	}


	public boolean isSound() {
		return sound;
	}

	public int getParentScreen() {return parentScreen;}

	public int getCurScreen() {return curScreen;}
	public void render(int i) {
		float delta = Gdx.graphics.getDeltaTime();
		switch (i) {
			case 0:
				startScreen.render(delta);
				break;
			case 1:
				levelSelectScreen.render(delta);
				break;
			case 2:
				swatchScreen.render(delta);
				break;
			case 3:
				settingsScreen.render(delta);
				break;
		}
	}


}
