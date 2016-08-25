package com.hla0;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Swatch extends Game {
	SwatchScreen swatchScreen;
	public void create() {
		swatchScreen = new SwatchScreen();
		Gdx.input.setInputProcessor(swatchScreen);
		setScreen(swatchScreen);
	}

}
