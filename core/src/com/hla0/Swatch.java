package com.hla0;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hla0.util.Constants;

public class Swatch extends ApplicationAdapter {
	public static final String TAG = Swatch.class.getName();
	ShapeRenderer renderer;
	Grid grid;
	OrthographicCamera camera;
	Viewport viewport;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		int width = 10;
		int height = 10;
		viewport = new FitViewport(width * (Constants.boxSize + Constants.margin) + Constants.margin,
				height * (Constants.boxSize + Constants.margin) + Constants.topPadding + Constants.bottomPadding + Constants.margin,
				camera);
		grid = new Grid(width,height, viewport);
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
		renderer.end();
	}

	public void renderGrid() {
		Square[][] squares = grid.getSquares();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		for (int i = 0; i < grid.getWidth(); i++) {
			for (int j = 0; j < grid.getHeight(); j++) {
				if (squares[i][j] != null) {
					renderer.setColor(squares[i][j].getColor());
					renderer.rect(i * (Constants.boxSize + Constants.margin) + Constants.margin,
							j * (Constants.boxSize + Constants.margin) + Constants.bottomPadding + Constants.margin,
							Constants.boxSize, Constants.boxSize);
				}
			}
		}
	}

	/**
	 * When the screen is resized, we need to inform the viewport. Note that when using an
	 * ExtendViewport, the world size might change as well.
	 */
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		Gdx.app.log(TAG, "Viewport world dimensions: (" + viewport.getWorldHeight() + ", " + viewport.getWorldWidth() + ")");
	}

	@Override
	public void dispose () {
		renderer.dispose();
		super.dispose();
	}

	//TODO checkGround() {}
	//determine whether square is at the bottom of grid or the square below has been cleared;

}
