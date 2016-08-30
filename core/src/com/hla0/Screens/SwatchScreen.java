package com.hla0.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hla0.Grid;
import com.hla0.Levels;
import com.hla0.Square;
import com.hla0.Swatch;
import com.hla0.util.Constants;

public class SwatchScreen extends InputAdapter implements Screen{
    public static final String TAG = SwatchScreen.class.getName();
    private Texture winCard;
    private Texture loseCard;
    Grid grid;
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    int curScreen;
    int levelsComplete;
    int parentScreen;
    FileHandle stars;
    FileHandle complete;
    boolean enter;
    boolean exit;
    //game 0, settings 1, win 2, lose 3
    int gameState;
    Swatch game;
    Sound winSound;
    Sound loseSound;
    int transitionTime;

    public SwatchScreen(Swatch g) {
        game = g;
        parentScreen = -1;
        camera = new OrthographicCamera();
        int level = 1;
        viewport = new FitViewport(Swatch.worldWidth,Swatch.worldHeight,camera);
        grid = new Grid(viewport, level, game);
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        curScreen = 0;
        gameState = 0;
        enter = true;
        exit = false;
        winCard = new Texture("win.png");
        loseCard = new Texture("lose.png");
        winSound = Gdx.audio.newSound(Gdx.files.internal("win.mp3"));
        loseSound = Gdx.audio.newSound(Gdx.files.internal("lose.wav"));
        transitionTime = 0;
    }

    @Override
    public void show() {
        enter = true; exit = false;
        gameState = 0;
        parseCompleteFile(complete);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        render();
    }
    public void render() {
        game.renderer.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        //when entering game
        if (!enter && !exit || gameState != 0) {
            game.renderer.begin(ShapeRenderer.ShapeType.Filled);
            grid.render(game.renderer);
            game.renderer.end();
            renderGridUI();
        }
        switch(gameState) {
            case 0:
                //transition from other screens
                if (enter) {
                    System.out.println("entered game");
                    gameState = 0;
                    //renderEnter();
                    enter = false;
                }
                else if (exit) {
                    System.out.println("exited game");
                    exit = false;
                }
                break;
            //all three render over grid
            case 1:
                //renderSettings();
                if (enter) {
                    System.out.println("entered Settings");
                    enter = false;
                }
                else if (exit) {
                    System.out.println("exited Settings");
                    gameState = 0;
                    exit = false;
                }
                else {
                    //should have back home, music, sound, help, maybe credits
                    //could go to level select but only not in free play mode
                }
                break;
            case 2:
                if (enter) {
                    renderWin(1);
                }
                else if (exit) {
                    transitionTime++;
                    //might need to switch to level select or splash or replay
                    renderWin(2);
                }
                else {
                    renderWin(0);
                }
                break;
            case 3:
                //renderLose();
                if (enter) {
                    transitionTime++;
                    System.out.println("entered lose");
                    //enter = false;
                    renderLose(1);
                }
                else if (exit) {
                    transitionTime++;
                    System.out.println("exited lose");
                    //exit = false;
                    //temporary
                    //might need to switch to level select or splash
                    renderLose(2);
                }
                else {
                    System.out.println("steady lose");
                    renderLose(0);
                }
                break;
        }
        if (gameState == 0 && !grid.isAnimating()) {
            if (grid.checkObjectives()) {
                parseCompleteFile(complete);
                processStars();
                System.out.println("Completed Level");
                //prompt for next level or level select
                if (!grid.isAnimating()) {
                    gameState = 2;
                    enter = true;
                }
            } else if (grid.checkFail()) {
                System.out.println("Lost");
                gameState = 3;
                grid.removeSquares();
                enter = true;
            }
        }
    }

    private void parseCompleteFile(FileHandle complete) {
        complete = Gdx.files.local("levelsComplete.txt");
        if (Gdx.files.local("levelsComplete.txt").exists()) {
            String data = complete.readString();
            System.out.println("File data: " + data);
            levelsComplete = Integer.parseInt(data);
            if (grid.checkObjectives() && grid.getLevel() > levelsComplete) {
                levelsComplete = grid.getLevel();
                complete.writeString("" + levelsComplete, false);
            }
        }
        else {
            levelsComplete = 0;
            complete.writeString("0",false);
        }
    }


    public void processStars() {
        //check stars
        //update stars
        stars = Gdx.files.local("levelStars.txt");
        if (Gdx.files.local("levelStars.txt").exists()) {
            String s = "";
            String sLine = stars.readString();
            if(grid.getLevel() != 0) {
                String insert = Levels.numStars(grid.getScore(), grid.getLevel());
                stars.writeString(sLine.substring(0, grid.getLevel()) + insert + sLine.substring(grid.getLevel() + 1, Constants.MAX_LEVEL), false);
            }
        }
        else {
            String s = "";
            for (int i = 0; i < Constants.MAX_LEVEL; i++) {
                s += "0";
            }
            stars.writeString(s,false);
        }
    }

    //TODO text needs to be centered below properly for all amounts of numbers
    public void renderGridUI() {
        font.setColor(Color.WHITE);
        //objectives
        //TODO add moves left and menu button
        int numberObj = 0;
        int colorNum = 0;
        int[] obj = new int[Constants.NUMBER_COLORS];
        //assume obj do not go above 6
        for (int i = 0; i < Constants.NUMBER_COLORS && numberObj < 6; i++) {
            if (grid.getColorObjectives(i) > 0) {
                obj[numberObj] = i;
                numberObj++;
                colorNum++;
            }
        }
        for (int i = 0; i < Constants.NUMBER_COLORS + 1 && numberObj < 6; i++) {
            if (grid.getAnchorObjectives(i) > 0) {
                obj[numberObj] = i;
                numberObj++;
            }
        }
        font.setColor(Color.BLACK);
        //TODO moves left, settings, maybe level
        for (int i = 0; i < numberObj; i++) {
            int offset = 0;
            int index = obj[i];
            int xPos = i * Swatch.worldWidth / numberObj + Constants.MARGIN + Swatch.worldWidth / numberObj / numberObj;
            int yPos = Swatch.worldHeight - Constants.BOX_SIZE * 4;
            if (i < colorNum) {
                if (grid.getColorObjectives(index) < 10) {
                    //offset += Constants.MARGIN / 2;
                }
                if (grid.getColorDestroyed(index) >= 10) {
                    offset -= Constants.MARGIN / 2;
                }
                game.renderer.begin(ShapeRenderer.ShapeType.Filled);
                game.renderer.setColor(Square.getColor(index));
                game.renderer.rect(xPos, yPos, Constants.MARGIN, Constants.MARGIN);
                game.renderer.end();
                game.batch.begin();
                int destroyed = grid.getColorDestroyed(index);
                if (destroyed > grid.getColorObjectives(index)) {
                    destroyed = grid.getColorObjectives(index);
                }
                font.getData().setScale(2, 2);
                font.draw(game.batch, String.format("%d/%d",destroyed,grid.getColorObjectives(index)), xPos - Constants.MARGIN / 2 + offset, yPos - Constants.BOX_SIZE);
                game.batch.end();
            }
            else {
                if (grid.getAnchorObjectives(index) < 10) {
                    //offset += Constants.MARGIN / 2;
                }
                if (index == 8) {
                    if (grid.getTotalAnchorDestroyed() >= 10) {
                        offset -= Constants.MARGIN / 2;
                    }
                    game.renderer.begin(ShapeRenderer.ShapeType.Line);
                    game.renderer.setColor(Color.WHITE);
                    game.renderer.ellipse(xPos, yPos, Constants.MARGIN, Constants.MARGIN);
                    game.renderer.end();
                    game.batch.begin();
                    int destroyed = grid.getTotalAnchorDestroyed();
                    if (destroyed > grid.getAnchorObjectives(index)) {
                        destroyed = grid.getAnchorObjectives(index);
                    }
                    font.getData().setScale(2, 2);
                    font.draw(game.batch, String.format("%d/%d",destroyed,grid.getAnchorObjectives(index)), xPos - Constants.MARGIN / 2 + offset, yPos - Constants.BOX_SIZE);
                    game.batch.end();
                }
                else {
                    if (grid.getAnchorDestroyed(index) >= 10) {
                        offset -= Constants.MARGIN / 2;
                    }
                    game.renderer.begin(ShapeRenderer.ShapeType.Filled);
                    game.renderer.setColor(Square.getColor(index));
                    game.renderer.ellipse(xPos, yPos, Constants.MARGIN, Constants.MARGIN);
                    game.renderer.end();
                    game.batch.begin();
                    int destroyed = grid.getAnchorDestroyed(index);
                    if (destroyed > grid.getAnchorObjectives(index)) {
                        destroyed = grid.getAnchorDestroyed(index);
                    }
                    font.getData().setScale(2, 2);
                    font.draw(game.batch, String.format("%d/%d",destroyed,grid.getAnchorObjectives(index)), xPos - Constants.MARGIN / 2 + offset, yPos - Constants.BOX_SIZE);
                    game.batch.end();
                }
            }
        }

        game.batch.begin();
        //score
        if (grid.getMoves() >= 0) {
            font.draw(game.batch, "Moves: " + grid.getMoves(), Constants.MARGIN, Swatch.worldHeight - Constants.BOX_SIZE * 2);
        }
        font.getData().setScale(3,3);
        font.draw(game.batch,leadingZeros(grid.getScore()),200,200);
        game.batch.end();
    }

    public void renderWin(int state) {
        game.batch.begin();
        switch (state) {
            case 0:
                game.batch.draw(winCard, 0, Constants.BOTTOM_PADDING);
                break;
            case 1:
                //enter
                transitionTime++;
                if (transitionTime == 1) {
                    if (game.isSound()) {
                        winSound.play();
                    }
                }
                game.batch.draw(winCard, 0, Constants.BOTTOM_PADDING + Swatch.worldHeight - transitionTime * 35);
                System.out.println("entered lose");
                if (transitionTime > 30) {
                    enter = false;
                    transitionTime = 0;
                }
                break;
            case 2:
                //exit
                transitionTime++;
                game.batch.draw(winCard, 0, Constants.BOTTOM_PADDING + transitionTime * 35);
                System.out.println("exited lose");
                if (transitionTime > 30) {
                    grid.loadLevel(grid.getLevel() + 1);
                    gameState = 0;
                    exit = false;
                    transitionTime = 0;
                }
                break;
        }
        game.batch.end();
    }

    public void renderLose(int state) {
        game.batch.begin();
        switch (state) {
            case 0:
                game.batch.draw(loseCard, 0, Constants.BOTTOM_PADDING);
                break;
            case 1:
                //enter
                transitionTime++;
                if (transitionTime == 1) {
                    if (game.isSound()) {
                        loseSound.play();
                    }
                }
                System.out.println(transitionTime);
                game.batch.draw(loseCard, 0, Constants.BOTTOM_PADDING + Swatch.worldHeight - transitionTime * 35);
                System.out.println("entered lose");
                if (transitionTime > 30) {
                    enter = false;
                    transitionTime = 0;
                }
                break;
            case 2:
                //exit
                transitionTime++;
                game.batch.draw(loseCard, 0, Constants.BOTTOM_PADDING + transitionTime * 35);
                System.out.println("exited lose");
                if (transitionTime > 30) {
                    grid.loadLevel(grid.getLevel());
                    gameState = 0;
                    exit = false;
                    transitionTime = 0;
                }
                break;
        }
        game.batch.end();
    }
    public CharSequence leadingZeros(int s) {
        CharSequence score = "" + s;
        if (s < 100000) {score = "0" + score;}
        if (s < 10000) {score = "0" + score;}
        if (s < 1000) {score = "0" + score;}
        if (s < 100) {score = "0" + score;}
        if (s < 10) {score = "0" + score;}
        if (s < 1) {score = "0" + score;}
        if (s == 0) {score = "000000";}
        return score;
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

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose () {}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.getCurScreen() == 2) {
            switch (gameState) {
                case 0:
                    gameTouch(screenX,screenY);
                    break;
                //temporary since might need to process nextScreen
                case 1:
                    //from settings
                    exit = true;
                    break;
                case 2:
                    //from win
                    exit = true;
                    break;
                case 3:
                    //from lose
                    exit = true;
                    break;
            }
            return true;
        }
        else {
            return false;
        }
    }

    public void gameTouch(int screenX, int screenY) {
        //do not process touches when grid is animating
        if (!grid.isAnimating()) {
            Vector2 v = transformToGrid(viewport.unproject(new Vector2(screenX, screenY)));
            //removes MARGINs from grid with some flexibility for slightly off touches
            if (v.x % 1 >= .07 && v.x % 1 <= .93 && v.y % 1 >= .07 && v.y % 1 <= .93) {
                if (v.x >= Constants.GRID_SIZE || v.x < 0 || v.y >= Constants.GRID_SIZE || v.y < 0) {
                    System.out.println("Outside grid");
                } else {
                    System.out.println("Obtained grid coordinates: (" + (int) v.x + ", " + (int) v.y + ")");
                    grid.processTouch((int)v.x,(int) v.y);
                }
            } else {
                System.out.println("Outside grid");
            }
        }
    }

    public Vector2 transformToGrid(Vector2 v1) {
        return new Vector2((v1.x - Constants.LEFT_PADDING - (Constants.MARGIN / 2)) / (Constants.BOX_SIZE + Constants.MARGIN),
                (v1.y - Constants.BOTTOM_PADDING - (Constants.MARGIN / 2)) / (Constants.BOX_SIZE + Constants.MARGIN));
    }

    public void loadLevel(int level) {
        grid.loadLevel(level);
    }

}
