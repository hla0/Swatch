package com.hla0;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

import java.util.ArrayList;

public class Swatch extends Game {
	public static final String TAG = Swatch.class.getName();
	ShapeRenderer renderer;
	Grid grid;
	OrthographicCamera camera;
	Viewport viewport;

	//TODO Add separate screens and move current functions into relevant screen
	@Override
	public void create () {
		camera = new OrthographicCamera();
		int width = 9;
		int height = 9;
		viewport = new FitViewport(width * (Constants.boxSize + Constants.margin) + Constants.margin,
				height * (Constants.boxSize + Constants.margin) + Constants.topPadding + Constants.bottomPadding + Constants.margin,
				camera);
		grid = new Grid(width,height, viewport, 1);
		renderer = new ShapeRenderer();
		Gdx.input.setInputProcessor(grid);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Filled);
		renderGrid();
		//TODO find better solution to hide new squares at top
		//temporary solution
		renderer.setColor(0,0,0,1);
		renderer.rect(0,Constants.bottomPadding + 10 * (Constants.boxSize + Constants.margin) + Constants.margin,330,Constants.topPadding);
		//section to block new Squares not working
		//renderer.setColor(Color.RED);
		//renderer.rect((grid.getHeight() + 1) * (Constants.boxSize + Constants.margin),0,(grid.getWidth() + 1) * (Constants.boxSize + Constants.margin),Constants.topPadding);
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

		//TODO right removal of swapped squares
		//TODO render one at a time and add direction
		//render the old color on top of the swapped square for transition
		for (int i = 0; i < swapped.size(); i++) {
			//need to replace 0 with direction
			swapped.get(i).renderSwapped(renderer,0);
			if (swapped.get(i).width <= 0 || swapped.get(i).height <= 0) {
				swapped.remove(i);
			}
		}
		grid.update();
	}

	/**
	 * When the screen is resized, we need to inform the viewport. Note that when using an
	 * ExtendViewport, the world size might change as well.
	 */
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		//Gdx.app.log(TAG, "Viewport world dimensions: (" + viewport.getWorldHeight() + ", " + viewport.getWorldWidth() + ")");
	}

	@Override
	public void dispose () {
		renderer.dispose();
		super.dispose();
	}
}
